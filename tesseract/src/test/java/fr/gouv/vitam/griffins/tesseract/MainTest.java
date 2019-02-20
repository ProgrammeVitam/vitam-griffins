package fr.gouv.vitam.griffins.tesseract;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.gouv.vitam.griffins.tesseract.pojo.*;
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

import static fr.gouv.vitam.griffins.tesseract.specific.ActionType.ANALYSE;
import static fr.gouv.vitam.griffins.tesseract.specific.ActionType.EXTRACT_GOT;
import static fr.gouv.vitam.griffins.tesseract.specific.ActionType.GENERATE;
import static fr.gouv.vitam.griffins.tesseract.specific.ActionType.IDENTIFY;
import static fr.gouv.vitam.griffins.tesseract.status.GriffinStatus.WARNING;
import static fr.gouv.vitam.griffins.tesseract.status.GriffinStatus.KO;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.assertj.core.api.Assertions.assertThat;

public class MainTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    static String defaultFileName="8093_311.4B.tif";
    static String defaultFileFormat="fmt/152";

    private String getOutputPath(Input input, Action action, BatchProcessor batchProcessor) {
        String outputName;
        switch (action.getType()) {
            case ANALYSE:
                return null;
            case IDENTIFY:
            case EXTRACT_GOT:
            case EXTRACT_AU:
                outputName = String.format("%s-%s.%s", action.getType().name(), input.getName(), "json");
                break;
            case GENERATE:
                outputName = String.format("%s-%s.%s", action.getType().name(), input.getName(), action.getValues().getExtension());
                break;
            default:
                throw new IllegalStateException("Unreachable");
        }
        return batchProcessor.batchDirectory.resolve(batchProcessor.outputFilesDirName).resolve(outputName).toString();
    }

    @Rule
    public TemporaryFolder tmpGriffonFolder = new TemporaryFolder();

    @Test
    public void should_GENERATE_all_batch_reference_files() throws Exception {
        File file = new File("src/test/resources/batch-reference/" + BatchProcessor.parametersFileName);
        Parameters parameters = objectMapper.readValue(file, Parameters.class);

        for (Input input : parameters.getInputs())
            should_GENERATE_one_file(input);
    }

    private void should_GENERATE_one_file(Input input) throws Exception {
        // Given
        Action action = new Action(GENERATE, new Values("txt", null));
        generateBatch(action, input);

        Path batchDirectory = tmpGriffonFolder.getRoot().toPath().resolve(input.getName());
        BatchProcessor batchProcessor = new BatchProcessor(batchDirectory);

        // Suppress outputfile if any
        String outputfilename=getOutputPath(input, action, batchProcessor);
        if (Files.isRegularFile(Paths.get(outputfilename)))
            Files.delete(Paths.get(outputfilename));

        // When
        batchProcessor.execute();

        // Then result is analyze to be sure text has been extracted

        Result result = getOutputs(input.getName());
        List<Output> actual = result.getOutputs().get(input.getName());
        System.out.println(actual);

        assertThat(actual).hasSize(1);
        assertThat(actual).extracting(Output::getAction).containsExactly(GENERATE);
        assertThat(actual).extracting(Output::getOutputName).containsExactly(outputfilename);
        assertThat(Paths.get(outputfilename)).exists();
        assertThat(Files.size(Paths.get(outputfilename))).isGreaterThan(1);
    }

    @Test
    public void should_IDENTIFY_error() throws Exception {
        // Given
        Input input = new Input(defaultFileName, defaultFileFormat);
        Action action = new Action(IDENTIFY);
        generateBatch(action, input);

        Path batchDirectory = tmpGriffonFolder.getRoot().toPath().resolve(input.getName());
        BatchProcessor batchProcessor = new BatchProcessor(batchDirectory);

        // When
        BatchStatus status = batchProcessor.execute();

        // Then
        assertThat(status.status).isEqualTo(WARNING);
        assertThat(Paths.get(tmpGriffonFolder.getRoot().getPath(), input.getName(), BatchProcessor.resultFileName)).exists();
    }

    @Test
    public void should_ANALYSE_metadata_error() throws Exception {
        // Given
        Input input = new Input(defaultFileName, defaultFileFormat);
        Action action = new Action(ANALYSE, new Values("AIF",null));
        generateBatch(action, input);

        Path batchDirectory = tmpGriffonFolder.getRoot().toPath().resolve(input.getName());
        BatchProcessor batchProcessor = new BatchProcessor(batchDirectory);

        // When
        BatchStatus status = batchProcessor.execute();

        // Then
        assertThat(status.status).isEqualTo(WARNING);
        assertThat(Paths.get(tmpGriffonFolder.getRoot().getPath(), input.getName(), BatchProcessor.resultFileName)).exists();
    }

    @Test
    public void should_NOT_EXTRACT_metadata_error() throws Exception {
        // Given
        Input input = new Input(defaultFileName, defaultFileFormat);
        Action action = new Action(EXTRACT_GOT, new Values(Arrays.asList("test")));
        generateBatch(action, input);

        Path batchDirectory = tmpGriffonFolder.getRoot().toPath().resolve(input.getName());
        BatchProcessor batchProcessor = new BatchProcessor(batchDirectory);

        // When
        BatchStatus status = batchProcessor.execute();

        // Then
        assertThat(status.status).isEqualTo(WARNING);
        assertThat(Paths.get(tmpGriffonFolder.getRoot().getPath(), input.getName(), BatchProcessor.resultFileName)).exists();
    }

    @Test
    public void should_return_error_if_no_parameters_file_in_batch_status() throws Exception {
        // Given
        Path batchFolder = tmpGriffonFolder.newFolder(Main.ID).toPath();
        Files.createFile(Paths.get(batchFolder.toString() + ".ready"));

        Path batchDirectory = tmpGriffonFolder.getRoot().toPath().resolve(Main.ID);
        BatchProcessor batchProcessor = new BatchProcessor(batchDirectory);

        // When
        BatchStatus status = batchProcessor.execute();

        // Then
        assertThat(status.status).isEqualTo(KO);
    }

    private Result getOutputs(String batchName) throws IOException {
        return objectMapper.readValue(Paths.get(tmpGriffonFolder.getRoot().getPath(),batchName, BatchProcessor.resultFileName).toFile(), Result.class);
    }

    private Parameters generateBatch(Action action, Input input) throws Exception {
        String batchName=input.getName();

        Path batchFolder = tmpGriffonFolder.newFolder(batchName).toPath();
        String inputFilesFolder = tmpGriffonFolder.newFolder(batchName, BatchProcessor.inputFilesDirName).toString();

        Path src = Paths.get(String.format("src/test/resources/batch-reference/%s/%s", BatchProcessor.inputFilesDirName, input.getName()));
        Path target = Paths.get(inputFilesFolder, input.getName());
        Files.copy(src, target, REPLACE_EXISTING);

        Parameters parameters = new Parameters();
        parameters.setDebug(false);
        parameters.setRequestId("requestId");
        parameters.setId(batchName);
        parameters.setActions(Collections.singletonList(action));
        parameters.setInputs(Collections.singletonList(input));

        FileAttribute<Set<PosixFilePermission>> setFileAttribute = PosixFilePermissions.asFileAttribute(Files.getPosixFilePermissions(batchFolder, NOFOLLOW_LINKS));
        File parametersFile = Files.createFile(Paths.get(batchFolder.toString(), BatchProcessor.parametersFileName), setFileAttribute).toFile();

        objectMapper.writer().writeValue(parametersFile, parameters);
        return parameters;
    }
}