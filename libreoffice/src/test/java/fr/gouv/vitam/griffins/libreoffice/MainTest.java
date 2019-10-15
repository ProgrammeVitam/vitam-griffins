/*
 * Copyright French Prime minister Office/SGMAP/DINSIC/Vitam Program (2015-2019)
 *
 * contact.vitam@culture.gouv.fr
 *
 * This software is a computer program whose purpose is to implement a digital archiving back-office system managing
 * high volumetry securely and efficiently.
 *
 * This software is governed by the CeCILL 2.1 license under French law and abiding by the rules of distribution of free
 * software. You can use, modify and/ or redistribute the software under the terms of the CeCILL 2.1 license as
 * circulated by CEA, CNRS and INRIA at the following URL "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and rights to copy, modify and redistribute granted by the license,
 * users are provided only with a limited warranty and the software's author, the holder of the economic rights, and the
 * successive licensors have only limited liability.
 *
 * In this respect, the user's attention is drawn to the risks associated with loading, using, modifying and/or
 * developing or reproducing the software by the user in light of its specific status of free software, that may mean
 * that it is complicated to manipulate, and that also therefore means that it is reserved for developers and
 * experienced professionals having in-depth computer knowledge. Users are therefore encouraged to load and test the
 * software's suitability as regards their requirements in conditions enabling the security of their systems and/or data
 * to be ensured and, more generally, to use and operate it in the same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had knowledge of the CeCILL 2.1 license and that you
 * accept its terms.
 */
package fr.gouv.vitam.griffins.libreoffice;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.gouv.vitam.griffins.libreoffice.pojo.Action;
import fr.gouv.vitam.griffins.libreoffice.pojo.BatchStatus;
import fr.gouv.vitam.griffins.libreoffice.pojo.Input;
import fr.gouv.vitam.griffins.libreoffice.pojo.Output;
import fr.gouv.vitam.griffins.libreoffice.pojo.Parameters;
import fr.gouv.vitam.griffins.libreoffice.pojo.Result;
import fr.gouv.vitam.griffins.libreoffice.pojo.Values;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static fr.gouv.vitam.griffins.libreoffice.BatchProcessor.inputFilesDirName;
import static fr.gouv.vitam.griffins.libreoffice.BatchProcessor.parametersFileName;
import static fr.gouv.vitam.griffins.libreoffice.BatchProcessor.resultFileName;
import static fr.gouv.vitam.griffins.libreoffice.Main.ID;
import static fr.gouv.vitam.griffins.libreoffice.status.ActionType.ANALYSE;
import static fr.gouv.vitam.griffins.libreoffice.status.ActionType.EXTRACT;
import static fr.gouv.vitam.griffins.libreoffice.status.ActionType.GENERATE;
import static fr.gouv.vitam.griffins.libreoffice.status.ActionType.IDENTIFY;
import static fr.gouv.vitam.griffins.libreoffice.status.GriffinStatus.KO;
import static fr.gouv.vitam.griffins.libreoffice.status.GriffinStatus.OK;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.assertj.core.api.Assertions.assertThat;

public class MainTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Rule
    public TemporaryFolder tmpGriffinFolder = new TemporaryFolder();

    @Test
    public void should_GENERATE_DOC_file_from_odt() {
        // Given
        Input input = new Input("test.odt", "fmt/291");
        Values values = new Values("doc", Collections.singletonList("FilterName:MS Word 97"));
        Action action = new Action(GENERATE, values);

        generateBatch(action, input);

        Path batchDirectory = tmpGriffinFolder.getRoot().toPath().resolve(input.getName());
        BatchProcessor batchProcessor = new BatchProcessor(batchDirectory);

        // When
        batchProcessor.execute();

        // Then
        Result result = getOutputs(input.getName());
        List<Output> actual = result.getOutputs().get(input.getName());

        assertThat(actual).hasSize(1);
        assertThat(actual).extracting(Output::getAction).containsExactly(GENERATE);
        assertThat(actual).extracting(Output::getStatus).containsExactly(OK);
        assertThat(actual.get(0).getOutputName()).endsWith(".doc");
    }

    @Test
    public void should_GENERATE_PDF_1A_file_from_odt() {
        // Given
        Input input = new Input("test.odt", "fmt/291");
        Values values = new Values("pdf", Arrays.asList("FilterData:SelectPdfVersion=1", "FilterData:UseLosslessCompression=true"));
        Action action = new Action(GENERATE, values);

        generateBatch(action, input);

        Path batchDirectory = tmpGriffinFolder.getRoot().toPath().resolve(input.getName());
        BatchProcessor batchProcessor = new BatchProcessor(batchDirectory);

        // When
        batchProcessor.execute();

        // Then
        Result result = getOutputs(input.getName());
        List<Output> actual = result.getOutputs().get(input.getName());

        assertThat(actual).hasSize(1);
        assertThat(actual).extracting(Output::getAction).containsExactly(GENERATE);
        assertThat(actual).extracting(Output::getStatus).containsExactly(OK);
        assertThat(actual.get(0).getOutputName()).endsWith(".pdf");
    }

    @Test
    public void should_GENERATE_for_multiple_instance() {
        String sourceName = "test.odt";
        List<String> names = IntStream.range(0, 4)
            .mapToObj(i -> String.format("%d_%s", i, sourceName))
            .collect(Collectors.toList());

        List<Output> actual = names.stream()
            .parallel()
            .flatMap(name -> {
                // Given
                Input input = new Input(name, "fmt/291");
                Values values = new Values("pdf", Collections.singletonList("FilterData:SelectPdfVersion=1"));
                Action action = new Action(GENERATE, values);

                generateBatch(action, input, sourceName);

                Path batchDirectory = tmpGriffinFolder.getRoot().toPath().resolve(input.getName());
                BatchProcessor batchProcessor = new BatchProcessor(batchDirectory);

                // When
                batchProcessor.execute();


                Result result = getOutputs(input.getName());
                return result.getOutputs().get(input.getName())
                    .stream();
            }).collect(Collectors.toList());

        // Then
        assertThat(actual).hasSize(4);
        assertThat(actual).extracting(Output::getAction).allMatch(a -> a.equals(GENERATE));
        assertThat(actual).extracting(Output::getStatus).allMatch(s -> s.equals(OK));
        assertThat(actual).extracting(Output::getOutputName)
            .containsExactlyElementsOf(names.stream().map(s -> String.format("%s.pdf", s)).collect(Collectors.toList()));
    }

    @Test
    public void should_GENERATE_for_big_number_of_file() {
        String sourceName = "test.odt";
        List<String> names = IntStream.range(0, 30)
            .mapToObj(i -> String.format("%d_%s", i, sourceName))
            .collect(Collectors.toList());

        List<Output> actual = names.stream()
            .flatMap(name -> {
                // Given
                Input input = new Input(name, "fmt/291");
                Values values = new Values("pdf", Collections.singletonList("FilterData:SelectPdfVersion=1"));
                Action action = new Action(GENERATE, values);

                generateBatch(action, input, sourceName);

                Path batchDirectory = tmpGriffinFolder.getRoot().toPath().resolve(input.getName());
                BatchProcessor batchProcessor = new BatchProcessor(batchDirectory);

                // When
                batchProcessor.execute();

                Result result = getOutputs(input.getName());
                return result.getOutputs().get(input.getName())
                    .stream();
            }).collect(Collectors.toList());

        // Then
        assertThat(actual).extracting(Output::getAction).allMatch(a -> a.equals(GENERATE));
        assertThat(actual).extracting(Output::getStatus).allMatch(s -> s.equals(OK));
        assertThat(actual).hasSize(names.size());
    }

    @Test
    public void should_GENERATE_for_wrong_file_error() {
        // Given
        Input input = new Input("library.jar", "fmt/136");
        Values values = new Values("pdf", Collections.emptyList());
        Action action = new Action(GENERATE, values);

        generateBatch(action, input);

        Path batchDirectory = tmpGriffinFolder.getRoot().toPath().resolve(input.getName());
        BatchProcessor batchProcessor = new BatchProcessor(batchDirectory);

        // When
        batchProcessor.execute();

        // Then
        Result result = getOutputs(input.getName());
        List<Output> actual = result.getOutputs().get(input.getName());
        System.out.println(actual);

        assertThat(actual).hasSize(1);
        assertThat(actual).extracting(Output::getAction).containsExactly(GENERATE);
        assertThat(actual.get(0).getStatus()).isEqualTo(GriffinStatus.KO);
    }

    @Test
    public void should_IDENTIFY_error() {
        // Given
        Input input = new Input("test.odt", "fmt/40");
        Action action = new Action(IDENTIFY);
        generateBatch(action, input);

        Path batchDirectory = tmpGriffinFolder.getRoot().toPath().resolve(input.getName());
        BatchProcessor batchProcessor = new BatchProcessor(batchDirectory);

        // When
        BatchStatus status = batchProcessor.execute();

        // Then
        assertThat(status.status).isEqualTo(KO);
        assertThat(Paths.get(tmpGriffinFolder.getRoot().getPath(), input.getName(), resultFileName)).doesNotExist();
    }

    @Test
    public void should_ANALYZE_error() {
        // Given
        Input input = new Input("test.odt", "fmt/40");
        Action action = new Action(ANALYSE);
        generateBatch(action, input);

        Path batchDirectory = tmpGriffinFolder.getRoot().toPath().resolve(input.getName());
        BatchProcessor batchProcessor = new BatchProcessor(batchDirectory);

        // When
        BatchStatus status = batchProcessor.execute();

        // Then
        assertThat(status.status).isEqualTo(KO);
        assertThat(Paths.get(tmpGriffinFolder.getRoot().getPath(), input.getName(), resultFileName)).doesNotExist();
    }

    @Test
    public void should_NOT_EXTRACT_metadata_error() {
        // Given
        Input input = new Input("test.odt", "fmt/40");
        Action action = new Action(EXTRACT, new Values(Arrays.asList("test")));
        generateBatch(action, input);

        Path batchDirectory = tmpGriffinFolder.getRoot().toPath().resolve(input.getName());
        BatchProcessor batchProcessor = new BatchProcessor(batchDirectory);

        // When
        BatchStatus status = batchProcessor.execute();

        // Then
        assertThat(status.status).isEqualTo(KO);
        assertThat(Paths.get(tmpGriffinFolder.getRoot().getPath(), input.getName(), resultFileName)).doesNotExist();
    }

    @Test
    public void should_return_error_if_no_parameters_file_in_batch_status() {
        // Given
        Path batchDirectory = tmpGriffinFolder.getRoot().toPath().resolve(ID);
        BatchProcessor batchProcessor = new BatchProcessor(batchDirectory);

        // When
        BatchStatus status = batchProcessor.execute();

        // Then
        assertThat(status.status).isEqualTo(KO);
    }

    private Result getOutputs(String batchName) {
        return getOutputs(Paths.get(tmpGriffinFolder.getRoot().getPath(), batchName, resultFileName));
    }

    private Result getOutputs(Path batch) {
        try {
            return objectMapper.readValue(batch.toFile(), Result.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Parameters generateBatch(Action action, Input input) {
        return generateBatch(action, input, input.getName());
    }

    private Parameters generateBatch(Action action, Input input, String sourceName) {
        try {
            String batchName = input.getName();

            Path batchFolder = tmpGriffinFolder.newFolder(batchName).toPath();
            String inputFilesFolder = tmpGriffinFolder.newFolder(batchName, inputFilesDirName).toString();

            Path src = Paths.get(String.format("src/test/resources/batch-reference/%s/%s", inputFilesDirName, sourceName));
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}