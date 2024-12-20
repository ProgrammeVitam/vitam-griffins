/*
 * Copyright French Prime minister Office/SGMAP/DINSIC/Vitam Program (2015-2020)
 *
 * contact.vitam@culture.gouv.fr
 *
 * This software is a computer program whose purpose is to implement a digital archiving back-office system managing
 * high volumetry securely and efficiently.
 *
 * This software is governed by the CeCILL-C license under French law and abiding by the rules of distribution of free
 * software. You can use, modify and/ or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at the following URL "https://cecill.info".
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
 * The fact that you are presently reading this means that you have had knowledge of the CeCILL-C license and that you
 * accept its terms.
 */
package fr.gouv.vitam.griffins.tesseract;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.gouv.vitam.griffins.tesseract.pojo.Action;
import fr.gouv.vitam.griffins.tesseract.pojo.BatchStatus;
import fr.gouv.vitam.griffins.tesseract.pojo.ExtractedMetadata;
import fr.gouv.vitam.griffins.tesseract.pojo.Input;
import fr.gouv.vitam.griffins.tesseract.pojo.Output;
import fr.gouv.vitam.griffins.tesseract.pojo.Parameters;
import fr.gouv.vitam.griffins.tesseract.pojo.Result;
import fr.gouv.vitam.griffins.tesseract.specific.InnerTool;
import fr.gouv.vitam.griffins.tesseract.specific.PuidType;
import fr.gouv.vitam.griffins.tesseract.specific.RawOutput;
import fr.gouv.vitam.griffins.tesseract.status.GriffinStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fr.gouv.vitam.griffins.tesseract.specific.ActionType.EXTRACT_AU;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class BatchProcessor {
    private static final Logger logger = LoggerFactory.getLogger(BatchProcessor.class);

    public static final String outputFilesDirName = "output-files";
    public static final String parametersFileName = "parameters.json";
    public static final String resultFileName = "result.json";
    public static final String inputFilesDirName = "input-files";

    public static final String TEXT_CONTENT = "TextContent";

    private final ObjectMapper mapper = new ObjectMapper();
    private InnerTool innerTool;
    private Parameters parameters;
    private final Path batchDirectory;

    public BatchProcessor(Path batchDirectory) {
        this.batchDirectory = batchDirectory;
    }

    public BatchStatus execute() {
        long startTime = System.currentTimeMillis();
        String batchProcessingId = batchDirectory.getFileName().toString();
        try {
            File file = batchDirectory.resolve(parametersFileName).toFile();
            parameters = mapper.readValue(file, Parameters.class);

            if (!batchProcessingId.equals(parameters.getId())) {
                throw new Exception("Batch id must be same as in " + parametersFileName);
            }

            Files.createDirectories(batchDirectory.resolve(outputFilesDirName));

            innerTool = new InnerTool();

            List<Output> outputs = parameters.getInputs()
                .stream()
                .flatMap(input -> executeActions(input, parameters))
                .collect(toList());

            addToFile(outputs, parameters.getRequestId(), parameters.getId());

            boolean isOutputsContainingError = outputs.stream().anyMatch(o -> o.getStatus() != GriffinStatus.OK);
            if (isOutputsContainingError || outputs.isEmpty()) {
                return BatchStatus.warning(batchDirectory.getFileName().toString(), startTime,
                    "Batch result contains error or is empty.");
            }
            return BatchStatus.ok(batchProcessingId, startTime);
        } catch (Exception e) {
            logger.error("{}", e);
            return BatchStatus.error(batchProcessingId, startTime, e);
        }
    }

    private void addToFile(List<Output> outputs, String requestId, String id) throws IOException {
        Map<String, List<Output>> outputsMap = outputs.stream()
            .collect(toMap(o -> o.getInput().getName(), Collections::singletonList,
                (o, o2) -> Stream.concat(o.stream(), o2.stream()).collect(Collectors.toList())));
        mapper.writer()
            .writeValue(batchDirectory.resolve(resultFileName).toFile(), Result.of(requestId, id, outputsMap));
    }

    private Stream<Output> executeActions(Input input, Parameters parameters) {
        if (PuidType.formatTypes.get(input.getFormatId()) == null) {
            return parameters.getActions()
                .stream()
                .map(action -> Output.error(input, action.getType(), "Can't apply to this format", Main.ID));
        }
        return parameters.getActions()
            .stream()
            .map(action -> apply(action, input))
            .map(this::postProcess);
    }

    private Output postProcess(RawOutput raw) {
        if (EXTRACT_AU.equals(raw.action.getType())) {
            ExtractedMetadata extractedMetadata = new ExtractedMetadata();
            extractedMetadata.put(TEXT_CONTENT, raw.stdout);
            return raw.postProcess(parameters.isDebug(), extractedMetadata);
        }
        return raw.postProcess(parameters.isDebug(), null);
    }

    private RawOutput apply(Action action, Input input) {
        try {
            RawOutput result =
                innerTool.apply(action, getInputPath(input), input.getFormatId(), getOutputPath(input, action),
                    parameters.isDebug());
            return Objects.requireNonNullElseGet(result, RawOutput::new).setContext(input, action);
        } catch (Exception e) {
            logger.error("{}", e);
            return new RawOutput(e).setContext(input, action);
        }
    }

    private String getInputPath(Input input) {
        return batchDirectory.resolve(inputFilesDirName).resolve(input.getName()).toString();
    }

    private String getOutputPath(Input input, Action action) {
        String outputName;
        switch (action.getType()) {
            case ANALYSE:
                return null;
            case IDENTIFY:
            case EXTRACT:
            case EXTRACT_AU:
                outputName = String.format("%s-%s.%s", action.getType().name(), input.getName(), "txt");
                break;
            case GENERATE:
                outputName = String
                    .format("%s-%s.%s", action.getType().name(), input.getName(), action.getValues().getExtension());
                break;
            default:
                throw new IllegalStateException("Unreachable");
        }
        return batchDirectory.resolve(outputFilesDirName).resolve(outputName).toString();
    }

    public static String stdToString(InputStream std) throws IOException {
        StringBuilder textBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(std, UTF_8))) {
            String c;
            while ((c = reader.readLine()) != null) {
                textBuilder.append(c);
            }
        }
        return textBuilder.toString();
    }

    public Path getBatchDirectory() {
        return batchDirectory;
    }
}
