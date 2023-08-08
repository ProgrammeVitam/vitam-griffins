package fr.gouv.vitam.griffins.ffmpeg;

import static fr.gouv.vitam.griffins.ffmpeg.BatchProcessor.inputFilesDirName;
import static fr.gouv.vitam.griffins.ffmpeg.BatchProcessor.parametersFileName;
import static fr.gouv.vitam.griffins.ffmpeg.BatchProcessor.resultFileName;
import static fr.gouv.vitam.griffins.ffmpeg.Main.ID;
import static fr.gouv.vitam.griffins.ffmpeg.status.ActionType.GENERATE;
import static fr.gouv.vitam.griffins.ffmpeg.status.GriffinStatus.OK;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.assertj.core.api.Assertions.assertThat;

import fr.gouv.vitam.griffins.ffmpeg.pojo.Action;
import fr.gouv.vitam.griffins.ffmpeg.pojo.Input;
import fr.gouv.vitam.griffins.ffmpeg.pojo.Output;
import fr.gouv.vitam.griffins.ffmpeg.pojo.Outputs;
import fr.gouv.vitam.griffins.ffmpeg.pojo.Parameters;
import fr.gouv.vitam.griffins.ffmpeg.pojo.Values;

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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.fasterxml.jackson.databind.ObjectMapper;

public class MainTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Rule
    public TemporaryFolder tmpGriffinFolder = new TemporaryFolder();
    private String batchName = "batch-test";

    @Test
    public void should_GENERATE_mp4_from_video() throws Exception {
        // Given
        String[] param = { "-codec:v", "libx264", "-preset", "superfast", "-crf", "99", "-profile:v", "baseline", "-level", "3.0", "-acodec", "libmp3lame" };
        Input input = new Input("vid1.mp4", "fmt/199");
        Action action = new Action(GENERATE, new Values("MP4", Arrays.asList(param)));
        generateBatch(action, input);

        Path batchDirectory = tmpGriffinFolder.getRoot().toPath().resolve(ID).resolve(batchName);
        BatchProcessor batchProcessor = new BatchProcessor(batchDirectory);

        // When
        batchProcessor.execute();

        // Then
        Outputs outputs = getOutputs();
        List<Output> actual = outputs.getOutputs().get(input.getName());

        assertThat(actual).hasSize(1);
        assertThat(actual).extracting(Output::getAction).containsExactly(GENERATE);
        assertThat(actual).extracting(Output::getOutputName)
                .containsExactly(String.format("%s-%s.%s", GENERATE.name(), input.getName(), action.getValues().getExtension()));
        assertThat(actual).extracting(Output::getStatus).containsExactly(OK);
    }

    @Test
    public void should_GENERATE_png_from_video() throws Exception {
        // Given
        String[] param = { "-vf", "thumbnail,scale=640:480", "-frames:v", "1" };
        Input input = new Input("vid1.mp4", "fmt/199");
        Action action = new Action(GENERATE, new Values("PNG", Arrays.asList(param)));
        generateBatch(action, input);

        Path batchDirectory = tmpGriffinFolder.getRoot().toPath().resolve(ID).resolve(batchName);
        BatchProcessor batchProcessor = new BatchProcessor(batchDirectory);

        // When
        batchProcessor.execute();

        // Then
        Outputs outputs = getOutputs();
        List<Output> actual = outputs.getOutputs().get(input.getName());

        assertThat(actual).hasSize(1);
        assertThat(actual).extracting(Output::getAction).containsExactly(GENERATE);
        assertThat(actual).extracting(Output::getOutputName)
                .containsExactly(String.format("%s-%s.%s", GENERATE.name(), input.getName(), action.getValues().getExtension()));
        assertThat(actual).extracting(Output::getStatus).containsExactly(OK);
    }

    @Test
    public void should_GENERATE_mp3_from_audio() throws Exception {
        // Given
        String[] param = { "-codec:a", "libmp3lame" };
        Input input = new Input("aud1.wma", "fmt/132");
        Action action = new Action(GENERATE, new Values("MP3", Arrays.asList(param)));
        generateBatch(action, input);

        Path batchDirectory = tmpGriffinFolder.getRoot().toPath().resolve(ID).resolve(batchName);
        BatchProcessor batchProcessor = new BatchProcessor(batchDirectory);

        // When
        batchProcessor.execute();

        // Then
        Outputs outputs = getOutputs();
        List<Output> actual = outputs.getOutputs().get(input.getName());

        assertThat(actual).hasSize(1);
        assertThat(actual).extracting(Output::getAction).containsExactly(GENERATE);
        assertThat(actual).extracting(Output::getOutputName)
                .containsExactly(String.format("%s-%s.%s", GENERATE.name(), input.getName(), action.getValues().getExtension()));
        assertThat(actual).extracting(Output::getStatus).containsExactly(OK);
    }

    private Outputs getOutputs() throws IOException {
        return objectMapper.readValue(Paths.get(tmpGriffinFolder.getRoot().getPath(), ID, batchName, resultFileName).toFile(), Outputs.class);
    }

    private Parameters generateBatch(Action action, Input input) throws Exception {
        tmpGriffinFolder.newFolder(ID);

        Path batchFolder = tmpGriffinFolder.newFolder(ID, batchName).toPath();
        String inputFilesFolder = tmpGriffinFolder.newFolder(ID, batchName, inputFilesDirName).toString();

        Path src = new File(Main.class.getResource(String.format("/%s/batch-reference/%s/%s", ID, inputFilesDirName, input.getName())).toURI())
                .toPath();
        Path target = Paths.get(inputFilesFolder, input.getName());
        Files.copy(src, target, REPLACE_EXISTING);

        Parameters parameters = new Parameters();
        parameters.setDebug(false);
        parameters.setRequestId("requestId");
        parameters.setId(batchName);
        parameters.setActions(Collections.singletonList(action));
        parameters.setInputs(Collections.singletonList(input));
        File parametersFile;
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            FileAttribute<Set<PosixFilePermission>> attributes = PosixFilePermissions.asFileAttribute(Files.getPosixFilePermissions(batchFolder, NOFOLLOW_LINKS));
            parametersFile = Files.createFile(Paths.get(batchFolder.toString(), parametersFileName), attributes).toFile();
        } else {
            parametersFile = Files.createFile(Paths.get(batchFolder.toString(), parametersFileName)).toFile();
        }

        objectMapper.writer().writeValue(parametersFile, parameters);
        return parameters;
    }

}
