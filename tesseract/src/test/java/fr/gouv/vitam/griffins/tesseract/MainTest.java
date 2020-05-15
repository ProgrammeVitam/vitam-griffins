package fr.gouv.vitam.griffins.tesseract;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.gouv.vitam.griffins.tesseract.pojo.Action;
import fr.gouv.vitam.griffins.tesseract.pojo.BatchStatus;
import fr.gouv.vitam.griffins.tesseract.pojo.Input;
import fr.gouv.vitam.griffins.tesseract.pojo.Output;
import fr.gouv.vitam.griffins.tesseract.pojo.Parameters;
import fr.gouv.vitam.griffins.tesseract.pojo.Result;
import fr.gouv.vitam.griffins.tesseract.pojo.Values;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static fr.gouv.vitam.griffins.tesseract.specific.ActionType.ANALYSE;
import static fr.gouv.vitam.griffins.tesseract.specific.ActionType.EXTRACT;
import static fr.gouv.vitam.griffins.tesseract.specific.ActionType.EXTRACT_AU;
import static fr.gouv.vitam.griffins.tesseract.specific.ActionType.GENERATE;
import static fr.gouv.vitam.griffins.tesseract.specific.ActionType.IDENTIFY;
import static fr.gouv.vitam.griffins.tesseract.status.GriffinStatus.KO;
import static fr.gouv.vitam.griffins.tesseract.status.GriffinStatus.OK;
import static fr.gouv.vitam.griffins.tesseract.status.GriffinStatus.WARNING;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.assertj.core.api.Assertions.assertThat;

public class MainTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    static final String defaultFileName = "8093_311.4B.tif";
    static final String defaultFileFormat = "fmt/152";

    private String getOutputPath(Input input, Action action, BatchProcessor batchProcessor) {
        String outputName;
        switch (action.getType()) {
            case ANALYSE:
                return null;
            case IDENTIFY:
            case EXTRACT:
            case EXTRACT_AU:
                outputName = String.format("%s-%s.%s", action.getType().name(), input.getName(), "json");
                break;
            case GENERATE:
                outputName = String
                    .format("%s-%s.%s", action.getType().name(), input.getName(), action.getValues().getExtension());
                break;
            default:
                throw new IllegalStateException("Unreachable");
        }
        return batchProcessor.getBatchDirectory().resolve(BatchProcessor.outputFilesDirName).resolve(outputName)
            .toString();
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
        String outputfilename = getOutputPath(input, action, batchProcessor);
        if (outputfilename != null && Files.isRegularFile(Paths.get(outputfilename)))
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
        assertThat(Paths.get(tmpGriffonFolder.getRoot().getPath(), input.getName(), BatchProcessor.resultFileName))
            .exists();
    }

    @Test
    public void should_ANALYSE_metadata_error() throws Exception {
        // Given
        Input input = new Input(defaultFileName, defaultFileFormat);
        Action action = new Action(ANALYSE, new Values("AIF", null));
        generateBatch(action, input);

        Path batchDirectory = tmpGriffonFolder.getRoot().toPath().resolve(input.getName());
        BatchProcessor batchProcessor = new BatchProcessor(batchDirectory);

        // When
        BatchStatus status = batchProcessor.execute();

        // Then
        assertThat(status.status).isEqualTo(WARNING);
        assertThat(Paths.get(tmpGriffonFolder.getRoot().getPath(), input.getName(), BatchProcessor.resultFileName))
            .exists();
    }

    @Test
    public void should_NOT_EXTRACT_metadata_error() throws Exception {
        // Given
        Input input = new Input(defaultFileName, defaultFileFormat);
        Action action = new Action(EXTRACT, new Values(Collections.singletonList("test")));
        generateBatch(action, input);

        Path batchDirectory = tmpGriffonFolder.getRoot().toPath().resolve(input.getName());
        BatchProcessor batchProcessor = new BatchProcessor(batchDirectory);

        // When
        BatchStatus status = batchProcessor.execute();

        // Then
        assertThat(status.status).isEqualTo(WARNING);
        assertThat(Paths.get(tmpGriffonFolder.getRoot().getPath(), input.getName(), BatchProcessor.resultFileName))
            .exists();
    }

    @Test
    public void should_extract_metadata() throws Exception {
        // Given
        Input input = new Input(defaultFileName, defaultFileFormat);
        Action action = new Action(EXTRACT, new Values(Arrays.asList("high-level", "trated")));
        generateBatch(action, input);

        Path batchDirectory = tmpGriffonFolder.getRoot().toPath().resolve(input.getName());
        BatchProcessor batchProcessor = new BatchProcessor(batchDirectory);

        // When
        BatchStatus status = batchProcessor.execute();

        // Then
        assertThat(status.status).isEqualTo(WARNING);
        assertThat(Paths.get(tmpGriffonFolder.getRoot().getPath(), input.getName(), BatchProcessor.resultFileName))
            .exists();
    }

    @Test
    public void should_extract_metadata_for_au() throws Exception {
        // Given
        Input input = new Input("eng_bw.png", "fmt/12");
        Action action = new Action(EXTRACT_AU, new Values("txt", null));
        generateBatch(action, input);

        Path batchDirectory = tmpGriffonFolder.getRoot().toPath().resolve(input.getName());
        BatchProcessor batchProcessor = new BatchProcessor(batchDirectory);

        // When
        BatchStatus status = batchProcessor.execute();

        // Then
        assertThat(status.status).isEqualTo(OK);
        assertThat(Paths.get(tmpGriffonFolder.getRoot().getPath(), input.getName(), BatchProcessor.resultFileName))
            .exists();
        assertThat(getTextContent(input)).contains("Mild Splendour");
    }

    private String getTextContent(Input input) throws IOException {
        return (String) getOutputs(input.getName())
            .getOutputs()
            .get(input.getName())
            .get(0)
            .getExtractedMetadataAU()
            .get(BatchProcessor.TEXT_CONTENT);
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
        return objectMapper.readValue(
            Paths.get(tmpGriffonFolder.getRoot().getPath(), batchName, BatchProcessor.resultFileName).toFile(),
            Result.class);
    }

    private void generateBatch(Action action, Input input) throws Exception {
        String batchName = input.getName();

        Path batchFolder = tmpGriffonFolder.newFolder(batchName).toPath();
        String inputFilesFolder = tmpGriffonFolder.newFolder(batchName, BatchProcessor.inputFilesDirName).toString();

        Path src = Paths.get(String
            .format("src/test/resources/batch-reference/%s/%s", BatchProcessor.inputFilesDirName, input.getName()));
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
        File parametersFile =
            Files.createFile(Paths.get(batchFolder.toString(), BatchProcessor.parametersFileName), setFileAttribute)
                .toFile();

        objectMapper.writer().writeValue(parametersFile, parameters);
    }
}
