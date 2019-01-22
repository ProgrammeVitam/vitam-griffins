package fr.gouv.vitam.griffins.libreoffice;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.gouv.vitam.griffins.libreoffice.pojo.*;
import fr.gouv.vitam.griffins.libreoffice.pojo.Result;
import fr.gouv.vitam.griffins.libreoffice.status.GriffinStatus;
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
import java.util.*;

import static fr.gouv.vitam.griffins.libreoffice.BatchProcessor.inputFilesDirName;
import static fr.gouv.vitam.griffins.libreoffice.BatchProcessor.parametersFileName;
import static fr.gouv.vitam.griffins.libreoffice.BatchProcessor.resultFileName;
import static fr.gouv.vitam.griffins.libreoffice.Main.ID;
import static fr.gouv.vitam.griffins.libreoffice.status.ActionType.ANALYSE;
import static fr.gouv.vitam.griffins.libreoffice.status.ActionType.EXTRACT;
import static fr.gouv.vitam.griffins.libreoffice.status.ActionType.GENERATE;
import static fr.gouv.vitam.griffins.libreoffice.status.ActionType.IDENTIFY;
import static fr.gouv.vitam.griffins.libreoffice.status.GriffinStatus.ERROR;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.assertj.core.api.Assertions.assertThat;

public class MainTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    static String errorFileName ="library.jar";
    static String defaultFileFormat="fmt/136";

    @Rule
    public TemporaryFolder tmpGriffinFolder = new TemporaryFolder();

    @Test
    public void should_GENERATE_all_batch_reference_files() throws Exception {
        File file = new File("src/test/resources/batch-reference/" + parametersFileName);
        Parameters parameters = objectMapper.readValue(file, Parameters.class);

        for (Input input : parameters.getInputs())
            should_GENERATE_one_file(input);
    }

    private void should_GENERATE_one_file(Input input) throws Exception {
        // Given
        Values values=new Values("pdf", Arrays.asList("--format=pdf", "--export=SelectedPdfVersion=1"));
        Action action = new Action(GENERATE,values);

        generateBatch(action, input);

        Path batchDirectory = tmpGriffinFolder.getRoot().toPath().resolve(input.getName());
        BatchProcessor batchProcessor = new BatchProcessor(batchDirectory);

        // When
        batchProcessor.execute();

        // Then result is analyze in comparaison with expected result in file name

        Result result = getOutputs(input.getName());
        List<Output> actual = result.getOutputs().get(input.getName());
        System.out.println(actual);

        assertThat(actual).hasSize(1);
        assertThat(actual).extracting(Output::getAction).containsExactly(GENERATE);
        assertThat(actual.get(0).getOutputName()).endsWith(".pdf");
    }

    @Test
    public void should_GENERATE_for_wrong_file_error() throws Exception {
        // Given
        Input input = new Input(errorFileName, defaultFileFormat);
        Values values=new Values("pdf", Arrays.asList("--format=pdf", "--export=SelectedPdfVersion=1"));
        Action action = new Action(GENERATE,values);

        generateBatch(action, input);

        Path batchDirectory = tmpGriffinFolder.getRoot().toPath().resolve(input.getName());
        BatchProcessor batchProcessor = new BatchProcessor(batchDirectory);

        // When
        batchProcessor.execute();

        // Then result is analyze in comparaison with expected result in file name

        Result result = getOutputs(input.getName());
        List<Output> actual = result.getOutputs().get(input.getName());
        System.out.println(actual);

        assertThat(actual).hasSize(1);
        assertThat(actual).extracting(Output::getAction).containsExactly(GENERATE);
        assertThat(actual.get(0).getStatus()).isEqualTo(GriffinStatus.ERROR);
    }

    @Test
    public void should_IDENTIFY_error() throws Exception {
        // Given
        Input input = new Input(errorFileName, defaultFileFormat);
        Action action = new Action(IDENTIFY);
        generateBatch(action, input);

        Path batchDirectory = tmpGriffinFolder.getRoot().toPath().resolve(input.getName());
        BatchProcessor batchProcessor = new BatchProcessor(batchDirectory);

        // When
        BatchStatus status = batchProcessor.execute();

        // Then
        assertThat(status.status).isEqualTo(ERROR);
        assertThat(Paths.get(tmpGriffinFolder.getRoot().getPath(), input.getName(), resultFileName)).doesNotExist();
    }

    @Test
    public void should_ANALYZE_error() throws Exception {
        // Given
        Input input = new Input(errorFileName, defaultFileFormat);
        Action action = new Action(ANALYSE);
        generateBatch(action, input);

        Path batchDirectory = tmpGriffinFolder.getRoot().toPath().resolve(input.getName());
        BatchProcessor batchProcessor = new BatchProcessor(batchDirectory);

        // When
        BatchStatus status = batchProcessor.execute();

        // Then
        assertThat(status.status).isEqualTo(ERROR);
        assertThat(Paths.get(tmpGriffinFolder.getRoot().getPath(), input.getName(), resultFileName)).doesNotExist();
    }

    @Test
    public void should_EXTRACT_metadata_error() throws Exception {
        // Given
        Map<String, String> dataToExtract = new HashMap<>();
        dataToExtract.put("AU_METADATA_DATE", "/image/properties/xmp:ModifyDate");
        Input input = new Input(errorFileName, defaultFileFormat);
        Action action = new Action(EXTRACT, new Values(dataToExtract));
        generateBatch(action, input);

        Path batchDirectory = tmpGriffinFolder.getRoot().toPath().resolve(input.getName());
        BatchProcessor batchProcessor = new BatchProcessor(batchDirectory);

        // When
        BatchStatus status = batchProcessor.execute();

        // Then
        assertThat(status.status).isEqualTo(ERROR);
        assertThat(Paths.get(tmpGriffinFolder.getRoot().getPath(), input.getName(), resultFileName)).doesNotExist();
    }

    @Test
    public void should_return_error_if_no_parameters_file_in_batch_status() throws Exception {
        // Given
        Path batchFolder = tmpGriffinFolder.newFolder(ID).toPath();
        Files.createFile(Paths.get(batchFolder.toString() + ".ready"));

        Path batchDirectory = tmpGriffinFolder.getRoot().toPath().resolve(ID);
        BatchProcessor batchProcessor = new BatchProcessor(batchDirectory);

        // When
        BatchStatus status = batchProcessor.execute();

        // Then
        assertThat(status.status).isEqualTo(ERROR);
    }

    private Result getOutputs(String batchName) throws IOException {
        return objectMapper.readValue(Paths.get(tmpGriffinFolder.getRoot().getPath(),batchName, resultFileName).toFile(), Result.class);
    }

    private Parameters generateBatch(Action action, Input input) throws Exception {
        String batchName=input.getName();

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

        FileAttribute<Set<PosixFilePermission>> setFileAttribute = PosixFilePermissions.asFileAttribute(Files.getPosixFilePermissions(batchFolder, NOFOLLOW_LINKS));
        File parametersFile = Files.createFile(Paths.get(batchFolder.toString(), parametersFileName), setFileAttribute).toFile();

        objectMapper.writer().writeValue(parametersFile, parameters);
        return parameters;
    }
}