package fr.gouv.vitam.griffins.jhove;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.gouv.vitam.griffins.jhove.pojo.Action;
import fr.gouv.vitam.griffins.jhove.pojo.BatchStatus;
import fr.gouv.vitam.griffins.jhove.pojo.Input;
import fr.gouv.vitam.griffins.jhove.pojo.Output;
import fr.gouv.vitam.griffins.jhove.pojo.Parameters;
import fr.gouv.vitam.griffins.jhove.pojo.Result;
import fr.gouv.vitam.griffins.jhove.pojo.Values;
import fr.gouv.vitam.griffins.jhove.status.AnalyseResult;
import junitparams.JUnitParamsRunner;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static fr.gouv.vitam.griffins.jhove.BatchProcessor.inputFilesDirName;
import static fr.gouv.vitam.griffins.jhove.BatchProcessor.parametersFileName;
import static fr.gouv.vitam.griffins.jhove.BatchProcessor.resultFileName;
import static fr.gouv.vitam.griffins.jhove.Main.ID;
import static fr.gouv.vitam.griffins.jhove.status.ActionType.ANALYSE;
import static fr.gouv.vitam.griffins.jhove.status.ActionType.EXTRACT;
import static fr.gouv.vitam.griffins.jhove.status.ActionType.GENERATE;
import static fr.gouv.vitam.griffins.jhove.status.ActionType.IDENTIFY;
import static fr.gouv.vitam.griffins.jhove.status.GriffinStatus.KO;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnitParamsRunner.class)
public class MainTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Rule
    public TemporaryFolder tmpGriffinFolder = new TemporaryFolder();

    private Path jhoveConfig = Paths.get("config/jhove.conf");

    @Test
    @junitparams.Parameters(method = "getFilesParameters")
    public void should_ANALYZE_one_file(Input input) throws Exception {
        // Given
        Action action = new Action(ANALYSE);
        generateBatch(action, input);

        Path batchDirectory = tmpGriffinFolder.getRoot().toPath().resolve(input.getName());
        BatchProcessor batchProcessor = new BatchProcessor(batchDirectory, jhoveConfig);

        // When
        batchProcessor.execute();

        // Then
        Result result = getOutputs(input.getName());
        List<Output> actual = result.getOutputs().get(input.getName());

        assertThat(actual).hasSize(1);
        assertThat(actual).extracting(Output::getAction).containsExactly(ANALYSE);
        assertThat(actual).extracting(Output::getOutputName).containsNull();

        if (actual.get(0).getStatus() == KO) {
            assertThat(input.getName()).contains(AnalyseResult.WRONG_FORMAT.name());
        } else {
            assertThat(input.getName()).contains(actual.get(0).getAnalyseResult().toString());
        }
    }

    @Test
    public void should_IDENTIFY_error() throws Exception {
        // Given
        Input input = new Input("AIFFTestFile-VALID_ALL.aif", "x-fmt/136");
        Action action = new Action(IDENTIFY);
        generateBatch(action, input);

        Path batchDirectory = tmpGriffinFolder.getRoot().toPath().resolve(input.getName());
        BatchProcessor batchProcessor = new BatchProcessor(batchDirectory, jhoveConfig);

        // When
        BatchStatus status = batchProcessor.execute();

        // Then
        assertThat(status.status).isEqualTo(KO);
        assertThat(Paths.get(tmpGriffinFolder.getRoot().getPath(), input.getName(), resultFileName)).doesNotExist();
    }

    @Test
    public void should_GENERATE_metadata_error() throws Exception {
        // Given
        Input input = new Input("AIFFTestFile-VALID_ALL.aif", "x-fmt/136");
        Action action = new Action(GENERATE, new Values("AIF", null));
        generateBatch(action, input);

        Path batchDirectory = tmpGriffinFolder.getRoot().toPath().resolve(input.getName());
        BatchProcessor batchProcessor = new BatchProcessor(batchDirectory, jhoveConfig);

        // When
        BatchStatus status = batchProcessor.execute();

        // Then
        assertThat(status.status).isEqualTo(KO);
        assertThat(Paths.get(tmpGriffinFolder.getRoot().getPath(), input.getName(), resultFileName)).doesNotExist();
    }

    @Test
    public void should_EXTRACT_metadata_error() throws Exception {
        // Given
        Map<String, String> dataToExtract = new HashMap<>();
        dataToExtract.put("AU_METADATA_DATE", "/image/properties/xmp:ModifyDate");
        Input input = new Input("AIFFTestFile-VALID_ALL.aif", "x-fmt/136");
        Action action = new Action(EXTRACT, new Values(dataToExtract));
        generateBatch(action, input);

        Path batchDirectory = tmpGriffinFolder.getRoot().toPath().resolve(input.getName());
        BatchProcessor batchProcessor = new BatchProcessor(batchDirectory, jhoveConfig);

        // When
        BatchStatus status = batchProcessor.execute();

        // Then
        assertThat(status.status).isEqualTo(KO);
        assertThat(Paths.get(tmpGriffinFolder.getRoot().getPath(), input.getName(), resultFileName)).doesNotExist();
    }

    @Test
    public void should_return_error_if_no_parameters_file_in_batch_status() throws Exception {
        // Given
        Path batchFolder = tmpGriffinFolder.newFolder(ID).toPath();
        Files.createFile(Paths.get(batchFolder.toString() + ".ready"));

        Path batchDirectory = tmpGriffinFolder.getRoot().toPath().resolve(ID);
        BatchProcessor batchProcessor = new BatchProcessor(batchDirectory, jhoveConfig);

        // When
        BatchStatus status = batchProcessor.execute();

        // Then
        assertThat(status.status).isEqualTo(KO);
    }

    private Result getOutputs(String batchName) throws IOException {
        return objectMapper.readValue(Paths.get(tmpGriffinFolder.getRoot().getPath(), batchName, resultFileName).toFile(), Result.class);
    }

    private Parameters generateBatch(Action action, Input input) throws Exception {
        String batchName = input.getName();

        Path batchFolder = tmpGriffinFolder.newFolder(batchName).toPath();
        String inputFilesFolder = tmpGriffinFolder.newFolder(batchName, inputFilesDirName).toString();

        Path src = Paths.get(String.format("src/test/resources/batch-reference/%s/%s", inputFilesDirName, input.getName()));
        Path target = Paths.get(inputFilesFolder, input.getName());
        Files.copy(src, target, REPLACE_EXISTING);

        Parameters parameters = new Parameters();
        parameters.setDebug(false);
        parameters.setRequestId("requestId");
        parameters.setId(batchName);
        parameters.setActions(Collections.singletonList(action));
        parameters.setInputs(Collections.singletonList(input));

        FileAttribute<Set<PosixFilePermission>> setFileAttribute =
            PosixFilePermissions.asFileAttribute(Files.getPosixFilePermissions(batchFolder, NOFOLLOW_LINKS));
        File parametersFile = Files.createFile(Paths.get(batchFolder.toString(), parametersFileName), setFileAttribute).toFile();

        objectMapper.writer().writeValue(parametersFile, parameters);
        return parameters;
    }

    private List<Input> getFilesParameters() throws Exception {
        File file = new File("src/test/resources/batch-reference/" + parametersFileName);
        Parameters parameters = objectMapper.readValue(file, Parameters.class);

        return parameters.getInputs();
    }
}