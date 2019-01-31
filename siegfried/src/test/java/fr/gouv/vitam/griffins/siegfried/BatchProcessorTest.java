package fr.gouv.vitam.griffins.siegfried;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.gouv.vitam.griffins.siegfried.pojo.Action;
import fr.gouv.vitam.griffins.siegfried.pojo.BatchStatus;
import fr.gouv.vitam.griffins.siegfried.pojo.Input;
import fr.gouv.vitam.griffins.siegfried.pojo.Output;
import fr.gouv.vitam.griffins.siegfried.pojo.Outputs;
import fr.gouv.vitam.griffins.siegfried.pojo.Parameters;
import fr.gouv.vitam.griffins.siegfried.pojo.Values;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static fr.gouv.vitam.griffins.siegfried.BatchProcessor.inputFilesDirName;
import static fr.gouv.vitam.griffins.siegfried.BatchProcessor.parametersFileName;
import static fr.gouv.vitam.griffins.siegfried.BatchProcessor.resultFileName;
import static fr.gouv.vitam.griffins.siegfried.Main.ID;
import static fr.gouv.vitam.griffins.siegfried.Main.siegfriedHost;
import static fr.gouv.vitam.griffins.siegfried.status.ActionType.ANALYSE;
import static fr.gouv.vitam.griffins.siegfried.status.ActionType.EXTRACT;
import static fr.gouv.vitam.griffins.siegfried.status.ActionType.GENERATE;
import static fr.gouv.vitam.griffins.siegfried.status.ActionType.IDENTIFY;
import static fr.gouv.vitam.griffins.siegfried.status.GriffinStatus.KO;
import static fr.gouv.vitam.griffins.siegfried.status.GriffinStatus.OK;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.assertj.core.api.Assertions.assertThat;

public class BatchProcessorTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Rule
    public TemporaryFolder tmpGriffinFolder = new TemporaryFolder();
    private String batchName = "batch-test";

    @Test
    public void should_IDENTIFY_gif_picture() throws Exception {
        // Given
        Input input = new Input("462px-Opensource.gif", "fmt/11");
        Action action = new Action(IDENTIFY, new Values("GIF", Collections.emptyList()));
        generateBatch(action, input);

        Path batchDirectory = tmpGriffinFolder.getRoot().toPath().resolve(ID).resolve(batchName);
        BatchProcessor batchProcessor = new BatchProcessor(batchDirectory, siegfriedHost);

        // When
        batchProcessor.execute();

        // Then
        Outputs outputs = getOutputs();
        List<Output> actual = outputs.getOutputs().get(input.getName());

        assertThat(actual).hasSize(1);
        assertThat(actual).extracting(Output::getAction).containsExactly(IDENTIFY);
        assertThat(actual).extracting(Output::getStatus).containsExactly(OK);
    }

    @Test
    public void should_GENERATE_error() throws Exception {
        // Given
        Input input = new Input("462px-Opensource.gif", "fmt/11");
        Action action = new Action(GENERATE);
        generateBatch(action, input);

        Path batchDirectory = tmpGriffinFolder.getRoot().toPath().resolve(ID).resolve(batchName);
        BatchProcessor batchProcessor = new BatchProcessor(batchDirectory, siegfriedHost);

        // When
        BatchStatus status = batchProcessor.execute();

        // Then
        assertThat(status.status).isEqualTo(KO);
        assertThat(Paths.get(tmpGriffinFolder.getRoot().getPath(), ID, batchName, resultFileName)).doesNotExist();
    }

    @Test
    public void should_EXTRACT_error() throws Exception {
        // Given
        Input input = new Input("462px-Opensource.gif", "fmt/11");
        Action action = new Action(EXTRACT);
        generateBatch(action, input);

        Path batchDirectory = tmpGriffinFolder.getRoot().toPath().resolve(ID).resolve(batchName);
        BatchProcessor batchProcessor = new BatchProcessor(batchDirectory, siegfriedHost);

        // When
        BatchStatus status = batchProcessor.execute();

        // Then
        assertThat(status.status).isEqualTo(KO);
        assertThat(Paths.get(tmpGriffinFolder.getRoot().getPath(), ID, batchName, resultFileName)).doesNotExist();
    }

    @Test
    public void should_ANALYSE_error() throws Exception {
        // Given
        Input input = new Input("462px-Opensource.gif", "fmt/11");
        Action action = new Action(ANALYSE);
        generateBatch(action, input);

        Path batchDirectory = tmpGriffinFolder.getRoot().toPath().resolve(ID).resolve(batchName);
        BatchProcessor batchProcessor = new BatchProcessor(batchDirectory, siegfriedHost);

        // When
        BatchStatus status = batchProcessor.execute();

        // Then
        assertThat(status.status).isEqualTo(KO);
        assertThat(Paths.get(tmpGriffinFolder.getRoot().getPath(), ID, batchName, resultFileName)).doesNotExist();
    }

    @Test
    public void should_return_error_if_no_parameters_file_in_batch_status() throws Exception {
        // Given
        tmpGriffinFolder.newFolder(ID);
        tmpGriffinFolder.newFolder(ID, batchName).toPath();

        Path batchDirectory = tmpGriffinFolder.getRoot().toPath().resolve(ID).resolve(batchName);
        BatchProcessor batchProcessor = new BatchProcessor(batchDirectory, "");

        // When
        BatchStatus status = batchProcessor.execute();

        // Then
        assertThat(status.status).isEqualTo(KO);
    }

    private Outputs getOutputs() throws IOException {
        return objectMapper.readValue(Paths.get(tmpGriffinFolder.getRoot().getPath(), ID, batchName, resultFileName).toFile(), Outputs.class);
    }

    private Parameters generateBatch(Action action, Input input) throws Exception {
        tmpGriffinFolder.newFolder(ID);

        Path batchFolder = tmpGriffinFolder.newFolder(ID, batchName).toPath();
        String inputFilesFolder = tmpGriffinFolder.newFolder(ID, batchName, inputFilesDirName).toString();

        Path src = Paths.get(String.format("src/test/resources/batch-reference/%s/%s", inputFilesDirName, input.getName()));
        Path target = Paths.get(inputFilesFolder, input.getName());
        Files.copy(src, target, REPLACE_EXISTING);

        Parameters parameters = new Parameters();
        parameters.setDebug(false);
        parameters.setRequestId("requestId");
        parameters.setId(batchName);
        parameters.setActions(Collections.singletonList(action));
        parameters.setInputs(Collections.singletonList(input));

        FileAttribute<Set<PosixFilePermission>> setFileAttribute = PosixFilePermissions.asFileAttribute(Files.getPosixFilePermissions(batchFolder, NOFOLLOW_LINKS));
        File parametersFile = Files.createFile(Paths.get(batchFolder.toString(), parametersFileName), setFileAttribute).toFile();

        objectMapper.writer().writeValue(parametersFile, parameters);
        return parameters;
    }
}
