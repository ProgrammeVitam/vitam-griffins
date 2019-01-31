/*******************************************************************************
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
 *******************************************************************************/

package fr.gouv.vitam.griffins.siegfried;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.gouv.vitam.griffins.siegfried.pojo.Action;
import fr.gouv.vitam.griffins.siegfried.pojo.BatchStatus;
import fr.gouv.vitam.griffins.siegfried.pojo.Input;
import fr.gouv.vitam.griffins.siegfried.pojo.Output;
import fr.gouv.vitam.griffins.siegfried.pojo.Outputs;
import fr.gouv.vitam.griffins.siegfried.pojo.Parameters;
import fr.gouv.vitam.griffins.siegfried.pojo.RawOutput;
import fr.gouv.vitam.griffins.siegfried.pojo.SiegfriedResponse;
import fr.gouv.vitam.griffins.siegfried.status.GriffinStatus;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static fr.gouv.vitam.griffins.siegfried.status.ActionType.IDENTIFY;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class BatchProcessor {
    public static final String outputFilesDirName = "output-files";
    public static final String parametersFileName = "parameters.json";
    public static final String resultFileName = "result.json";
    public static final String inputFilesDirName = "input-files";
    private static final Logger logger = LoggerFactory.getLogger(BatchProcessor.class);
    private final ObjectMapper mapper = new ObjectMapper();
    private final OkHttpClient client = new OkHttpClient();
    private final Encoder base64 = Base64.getEncoder();

    private final Pattern fileMatch = Pattern.compile("[a-zA-Z0-9_.\\-]+");

    private final Path batchDirectory;
    private final String siegfriedPath;

    public BatchProcessor(Path batchDirectory, String siegfriedPath) {
        this.batchDirectory = batchDirectory;
        this.siegfriedPath = siegfriedPath;
    }

    public BatchStatus execute() {
        long startTime = System.currentTimeMillis();
        String batchProcessingId = batchDirectory.getFileName().toString();
        try {
            File file = batchDirectory.resolve(parametersFileName).toFile();
            Parameters parameters = mapper.readValue(file, Parameters.class);

            if (!batchProcessingId.equals(parameters.getId())) {
                throw new Exception("Batch id must be same as in " + parametersFileName);
            }

            Files.createDirectory(batchDirectory.resolve(outputFilesDirName));

            List<Output> outputs = parameters.getInputs()
                .stream()
                .filter(i -> fileMatch.matcher(i.getName()).matches())
                .flatMap(input -> executeActions(input, parameters))
                .collect(toList());

            addToFile(outputs, parameters.getRequestId(), parameters.getId());

            boolean isOutputsContainingError = outputs.stream().anyMatch(o -> o.getStatus() != GriffinStatus.OK);
            if (isOutputsContainingError || outputs.isEmpty()) {
                return BatchStatus
                    .warning(batchProcessingId, startTime, "Batch result contains error or is empty.");
            }
            return BatchStatus.ok(batchProcessingId, startTime);
        } catch (Exception e) {
            logger.error("{}", e);
            return BatchStatus.error(batchProcessingId, startTime, e);
        }
    }

    private void addToFile(List<Output> outputs, String requestId, String id) throws IOException {
        BinaryOperator<List<Output>> binaryOperator = (o, o2) -> Stream.concat(o.stream(), o2.stream()).collect(toList());
        Function<Output, List<Output>> mapCollector = Collections::singletonList;
        Function<Output, String> keyMapper = o -> o.getInput().getName();

        Map<String, List<Output>> outputsMap = outputs.stream().collect(toMap(keyMapper, mapCollector, binaryOperator));

        mapper.writer()
            .writeValue(batchDirectory.resolve(resultFileName).toFile(), Outputs.of(requestId, id, outputsMap));
    }

    private Stream<Output> executeActions(Input input, Parameters parameters) {
        return parameters.getActions()
            .stream()
            .map(action -> apply(action, input))
            .map(raw -> postProcess(raw, parameters.isDebug()));
    }

    private RawOutput apply(Action action, Input input) {
        HttpUrl url = getSiegfriedUrl(input, action);
        Request request = new Request.Builder()
            .get()
            .url(url)
            .build();

        try (Response response = client.newCall(request).execute()) {
            ResponseBody body = response.body();
            if (!response.isSuccessful() || body == null) {
                return new RawOutput("Error response body empty or response not successful.", input, action, url.toString());
            }

            SiegfriedResponse siegFriedResponse = mapper.readValue(body.bytes(), SiegfriedResponse.class);
            return new RawOutput(siegFriedResponse, input, action, url.toString());
        } catch (IOException e) {
            logger.error("{}", e);
            return new RawOutput(e.getMessage(), input, action, url.toString());
        }
    }

    private HttpUrl getSiegfriedUrl(Input input, Action actionType) {
        if (actionType.getType() != IDENTIFY) {
            throw new RuntimeException(String.format("Cannot do an action of type %s", actionType.getType()));
        }

        byte[] inputPath = batchDirectory.resolve(inputFilesDirName).resolve(input.getName()).toString().getBytes();

        return new HttpUrl.Builder()
            .scheme("http")
            .host(siegfriedPath)
            .port(19000)
            .addPathSegment(actionType.getType().action)
            .addPathSegment(base64.encodeToString(inputPath))
            .addQueryParameter("base64", "true")
            .addQueryParameter("format", "json")
            .build();
    }

    private Output postProcess(RawOutput rawOutput, boolean debug) throws RuntimeException {
        if (rawOutput.action.getType() == IDENTIFY) {
            return rawOutput.toOutput(debug);
        }
        throw new IllegalStateException(String.format("Cannot post process action other than %s", IDENTIFY));
    }

}
