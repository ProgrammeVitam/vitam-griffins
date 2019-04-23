package fr.gouv.vitam.griffins.imagemagick;

import static fr.gouv.vitam.griffins.imagemagick.BatchProcessor.ALL_METADATA;
import static fr.gouv.vitam.griffins.imagemagick.BatchProcessor.RAW_METADATA;
import static fr.gouv.vitam.griffins.imagemagick.BatchProcessor.inputFilesDirName;
import static fr.gouv.vitam.griffins.imagemagick.BatchProcessor.parametersFileName;
import static fr.gouv.vitam.griffins.imagemagick.BatchProcessor.resultFileName;
import static fr.gouv.vitam.griffins.imagemagick.Main.ID;
import static fr.gouv.vitam.griffins.imagemagick.status.ActionType.ANALYSE;
import static fr.gouv.vitam.griffins.imagemagick.status.ActionType.EXTRACT;
import static fr.gouv.vitam.griffins.imagemagick.status.ActionType.GENERATE;
import static fr.gouv.vitam.griffins.imagemagick.status.ActionType.IDENTIFY;
import static fr.gouv.vitam.griffins.imagemagick.status.AnalyseResult.NOT_VALID;
import static fr.gouv.vitam.griffins.imagemagick.status.AnalyseResult.VALID_ALL;
import static fr.gouv.vitam.griffins.imagemagick.status.GriffinStatus.KO;
import static fr.gouv.vitam.griffins.imagemagick.status.GriffinStatus.OK;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import fr.gouv.vitam.griffins.imagemagick.pojo.Action;
import fr.gouv.vitam.griffins.imagemagick.pojo.BatchStatus;
import fr.gouv.vitam.griffins.imagemagick.pojo.Input;
import fr.gouv.vitam.griffins.imagemagick.pojo.Output;
import fr.gouv.vitam.griffins.imagemagick.pojo.Outputs;
import fr.gouv.vitam.griffins.imagemagick.pojo.Parameters;
import fr.gouv.vitam.griffins.imagemagick.pojo.Values;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class MainTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Rule
    public TemporaryFolder tmpGriffinFolder = new TemporaryFolder();
    private String batchName = "batch-test";

    @Test
    public void should_GENERATE_gif_picture() throws Exception {
        // Given
        Input input = new Input("280px-PNG_transparency_demonstration_1.png", "fmt/11");
        Action action = new Action(GENERATE, new Values("GIF", Collections.emptyList()));
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
    public void should_GENERATE_gif_picture_with_args() throws Exception {
        // Given
        Input input = new Input("280px-PNG_transparency_demonstration_1.png", "fmt/11");
        Action action = new Action(GENERATE, new Values("JPG", Arrays.asList("-resize", "50%")));
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
    public void should_ANALYZE_png_picture() throws Exception {
        // Given
        Input input = new Input("280px-PNG_transparency_demonstration_1.png", "fmt/11");
        Action action = new Action(ANALYSE);
        generateBatch(action, input);

        Path batchDirectory = tmpGriffinFolder.getRoot().toPath().resolve(ID).resolve(batchName);
        BatchProcessor batchProcessor = new BatchProcessor(batchDirectory);

        // When
        batchProcessor.execute();

        // Then
        Outputs outputs = getOutputs();
        List<Output> actual = outputs.getOutputs().get(input.getName());

        assertThat(actual).hasSize(1);
        assertThat(actual).extracting(Output::getAction).containsExactly(ANALYSE);
        assertThat(actual).extracting(Output::getOutputName).containsNull();
        assertThat(actual).extracting(Output::getStatus).containsExactly(OK);
        assertThat(actual).extracting(Output::getAnalyseResult).containsExactly(VALID_ALL);
    }

    @Test
    public void should_ANALYZE_jpg_picture_fall_in_warning() throws Exception {
        // Given
        Input input = new Input("picture-corrupted.jpg", "fmt/41");
        Action action = new Action(ANALYSE);
        generateBatch(action, input);

        Path batchDirectory = tmpGriffinFolder.getRoot().toPath().resolve(ID).resolve(batchName);
        BatchProcessor batchProcessor = new BatchProcessor(batchDirectory);

        // When
        batchProcessor.execute();

        // Then
        Outputs outputs = getOutputs();
        List<Output> actual = outputs.getOutputs().get(input.getName());

        assertThat(actual).hasSize(1);
        assertThat(actual).extracting(Output::getAction).containsExactly(ANALYSE);
        assertThat(actual).extracting(Output::getOutputName).containsNull();
        assertThat(actual).extracting(Output::getStatus).containsExactly(OK);
        assertThat(actual).extracting(Output::getAnalyseResult).containsExactly(NOT_VALID);
    }

    @Test
    public void should_IDENTIFY_error() throws Exception {
        // Given
        Input input = new Input("280px-PNG_transparency_demonstration_1.png", "fmt/11");
        Action action = new Action(IDENTIFY);
        generateBatch(action, input);

        Path batchDirectory = tmpGriffinFolder.getRoot().toPath().resolve(ID).resolve(batchName);
        BatchProcessor batchProcessor = new BatchProcessor(batchDirectory);

        // When
        BatchStatus status = batchProcessor.execute();

        // Then
        assertThat(status.status).isEqualTo(KO);
        assertThat(Paths.get(tmpGriffinFolder.getRoot().getPath(), ID, batchName, resultFileName)).doesNotExist();
    }

    @Test
    public void should_EXTRACT_metadata() throws Exception {
        // Given
        Input input = new Input("picture.jpg", "fmt/41");
        Action action = new Action(EXTRACT, new Values(Collections.singletonList(ALL_METADATA)));
        generateBatch(action, input);

        Path batchDirectory = tmpGriffinFolder.getRoot().toPath().resolve(ID).resolve(batchName);
        BatchProcessor batchProcessor = new BatchProcessor(batchDirectory);

        // When
        batchProcessor.execute();

        // Then
        Outputs outputs = getOutputs();
        List<Output> actual = outputs.getOutputs().get(input.getName());

        assertThat(actual).hasSize(1);
        assertThat(actual).extracting(Output::getAction).containsExactly(EXTRACT);
        assertThat(actual).extracting(Output::getStatus).containsExactly(OK);
        Map<String, List<Object>> metadata = actual.get(0).getExtractedMetadata().getOtherMetadata();
        assertThat(metadata.get("mimeType")).contains("image/jpeg");
        Object geometry = metadata.get("geometry").get(0);
        String expectedJson = "{\"height\":[\"598\"], \"width\":[\"800\"], \"x\":[\"0\"], \"y\":[\"0\"]}";
        JsonNode expected = mapper.readValue(expectedJson, JsonNode.class);
        JsonNode geometryNode = mapper.convertValue(geometry, JsonNode.class);
        assertThat(geometryNode).isEqualTo(expected);
    }

    @Test
    public void should_EXTRACT_metadata_with_raw_metadata() throws Exception {
        // Given
        Input input = new Input("picture.jpg", "fmt/41");
        Action action = new Action(EXTRACT, new Values(Collections.singletonList(RAW_METADATA)));
        generateBatch(action, input);

        Path batchDirectory = tmpGriffinFolder.getRoot().toPath().resolve(ID).resolve(batchName);
        BatchProcessor batchProcessor = new BatchProcessor(batchDirectory);

        // When
        batchProcessor.execute();

        // Then
        Outputs outputs = getOutputs();
        List<Output> actual = outputs.getOutputs().get(input.getName());
        assertThat(actual).hasSize(1);
        assertThat(actual).extracting(Output::getAction).containsExactly(EXTRACT);
        assertThat(actual).extracting(Output::getStatus).containsExactly(OK);
        String rawMetadata = actual.get(0).getExtractedMetadata().getRawMetadata();
        assertThat(rawMetadata).contains("\"format\": \"JPEG\"");
    }

    @Test
    public void should_EXTRACT_metadata_and_filter() throws Exception {
        // Given
        Input input = new Input("picture.jpg", "fmt/41");
        Action action = new Action(EXTRACT, new Values(Arrays.asList("mimeType", "geometry")));
        generateBatch(action, input);

        Path batchDirectory = tmpGriffinFolder.getRoot().toPath().resolve(ID).resolve(batchName);
        BatchProcessor batchProcessor = new BatchProcessor(batchDirectory);

        // When
        batchProcessor.execute();

        // Then
        Outputs outputs = getOutputs();
        List<Output> actual = outputs.getOutputs().get(input.getName());

        assertThat(actual).hasSize(1);
        assertThat(actual).extracting(Output::getAction).containsExactly(EXTRACT);
        assertThat(actual).extracting(Output::getStatus).containsExactly(OK);
        Map<String, List<Object>> metadata = actual.get(0).getExtractedMetadata().getOtherMetadata();
        assertThat(metadata.size()).isEqualTo(2);
        assertThat(metadata.get("mimeType")).contains("image/jpeg");
        Object geometry = metadata.get("geometry").get(0);
        String expectedJson = "{\"height\":[\"598\"], \"width\":[\"800\"], \"x\":[\"0\"], \"y\":[\"0\"]}";
        JsonNode expected = mapper.readValue(expectedJson, JsonNode.class);
        JsonNode geometryNode = mapper.convertValue(geometry, JsonNode.class);
        assertThat(geometryNode).isEqualTo(expected);
    }

    @Test
    public void should_EXTRACT_metadata_with_args() throws Exception {
        // Given
        Input input = new Input("picture.jpg", "fmt/41");
        Action action =
            new Action(EXTRACT,
                new Values(Collections.singletonList("format:otherMetadata"),
                    Collections.singletonList(ALL_METADATA)));
        generateBatch(action, input);

        Path batchDirectory = tmpGriffinFolder.getRoot().toPath().resolve(ID).resolve(batchName);
        BatchProcessor batchProcessor = new BatchProcessor(batchDirectory);
        // When
        batchProcessor.execute();
        // Then
        Outputs outputs = getOutputs();
        List<Output> actual = outputs.getOutputs().get(input.getName());

        assertThat(actual).hasSize(1);
        assertThat(actual).extracting(Output::getAction).containsExactly(EXTRACT);
        assertThat(actual).extracting(Output::getStatus).containsExactly(OK);
        Map<String, List<Object>> metadata = actual.get(0).getExtractedMetadata().getOtherMetadata();
        assertThat(metadata.get("mimeType")).contains("image/jpeg");
        Object geometry = metadata.get("geometry").get(0);
        String expectedJson = "{\"height\":[\"598\"], \"width\":[\"800\"], \"x\":[\"0\"], \"y\":[\"0\"]}";
        JsonNode expected = mapper.readValue(expectedJson, JsonNode.class);
        JsonNode geometryNode = mapper.convertValue(geometry, JsonNode.class);
        assertThat(geometryNode).isEqualTo(expected);
    }

    @Test
    public void should_return_error_if_no_parameters_file_in_batch_status() throws Exception {
        // Given
        tmpGriffinFolder.newFolder(ID);
        Path batchFolder = tmpGriffinFolder.newFolder(ID, batchName).toPath();

        Path batchDirectory = tmpGriffinFolder.getRoot().toPath().resolve(ID).resolve(batchName);
        BatchProcessor batchProcessor = new BatchProcessor(batchDirectory);

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

        Path src = new File(Object.class.getResource(String.format("/%s/batch-reference/%s/%s", ID, inputFilesDirName, input.getName())).toURI())
            .toPath();
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

    private final ObjectMapper mapper = new ObjectMapper();


    @Test
    public void should_recursive_parse_json_struct() throws Exception {
        // Given
        BatchProcessor for_test = new BatchProcessor(Paths.get("for test"));
        InputStream resourceAsStream = getClass().getResourceAsStream("/vitam-imagemagick-griffin/batch-reference/imagemagick_output.json");
        // When
        ArrayNode metadata = (ArrayNode) this.mapper.readTree(resourceAsStream);
        Iterator<Entry<String, JsonNode>> image = metadata.get(0).get("image").fields();
        Map<String, List<Object>> result = StreamSupport.stream(Spliterators.spliteratorUnknownSize(image, Spliterator.ORDERED), false)
            .map(for_test::transformJsonElementsToArray)
            .map(for_test::transformJsonNodeToListOfObject)
            .collect(toMap(Entry::getKey, Entry::getValue));
        // Then
        Object channelDepthTab = result.get("channelDepthTab");
        Object chromaticity = result.get("chromaticity");
        String expectedChannelDepthTab =
            "[{\"red\":[\"8\"],\"green\":[\"8\"],\"blue\":[\"8\"]},{\"red\":[\"6\"],\"green\":[\"6\"],\"blue\":[\"6\"]}]";
        String expectedChromaticity =
            "[{\"redPrimary\":[{\"x\":[\"0.64\"],\"y\":[\"0.33\"]}],\"greenPrimary\":[{\"x\":[\"0.3\"],\"y\":[\"0.6\"]}],\"bluePrimary\":[{\"x\":[\"0.15\"],\"y\":[\"0.06\"]}],\"whitePrimary\":[{\"x\":[\"0.3127\"],\"y\":[\"0.329\"]}]}]";
        JsonNode expectedJson = mapper.readValue(expectedChannelDepthTab, JsonNode.class);
        JsonNode channelJson = mapper.convertValue(channelDepthTab, JsonNode.class);
        JsonNode expectedChromaticityNode = mapper.readValue(expectedChromaticity, JsonNode.class);
        JsonNode ChromaticityJson = mapper.convertValue(chromaticity, JsonNode.class);
        assertThat(channelJson).isEqualTo(expectedJson);
        assertThat(ChromaticityJson).isEqualTo(expectedChromaticityNode);
    }

}
