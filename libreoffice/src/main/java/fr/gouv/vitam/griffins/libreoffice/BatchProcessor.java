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
import fr.gouv.vitam.griffins.libreoffice.status.GriffinStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fr.gouv.vitam.griffins.libreoffice.PuidType.formatTypes;
import static fr.gouv.vitam.griffins.libreoffice.status.ActionType.GENERATE;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class BatchProcessor {
    public static final String outputFilesDirName = "output-files";
    public static final String parametersFileName = "parameters.json";
    public static final String resultFileName = "result.json";
    public static final String inputFilesDirName = "input-files";
    private static final Logger logger = LoggerFactory.getLogger(BatchProcessor.class);
    private final ObjectMapper mapper = new ObjectMapper();
    private final Path batchDirectory;
    private Parameters parameters;

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

            List<Output> outputs = parameters.getActions()
                .stream()
                .flatMap(a -> executeAll(a, parameters).stream())
                .collect(toList());

            addToFile(outputs, parameters.getRequestId(), parameters.getId());

            boolean isOutputsContainingError = outputs.stream().anyMatch(o -> o.getStatus() != GriffinStatus.OK);
            if (isOutputsContainingError || outputs.isEmpty()) {
                return BatchStatus.warning(batchDirectory.getFileName().toString(), startTime, "Batch result contains error or is empty.");
            }
            return BatchStatus.ok(batchProcessingId, startTime);
        } catch (Exception e) {
            logger.error("{}", e);
            return BatchStatus.error(batchProcessingId, startTime, e);
        }
    }

    private void addToFile(List<Output> outputs, String requestId, String id) throws IOException {
        Map<String, List<Output>> outputsMap = outputs.stream()
            .collect(
                toMap(o -> o.getInput().getName(), Collections::singletonList, (o, o2) -> Stream.concat(o.stream(), o2.stream()).collect(toList())));
        mapper.writer().writeValue(batchDirectory.resolve(resultFileName).toFile(), Result.of(requestId, id, outputsMap));
    }

    private List<Output> executeAll(Action action, Parameters parameters) {
        if (!GENERATE.equals(action.getType())) {
            throw new IllegalStateException(String.format("Cannot execute libreoffice for action of type %s.", action.getType()));
        }
        ProcessBuilder processBuilder = new ProcessBuilder(getLibreOfficeParams(action, parameters.getInputs()));
        try {
            Process libreoffice = processBuilder.start();
            libreoffice.waitFor();
            return getOutputFrom(libreoffice, processBuilder, parameters, action);
        } catch (Exception e) {
            logger.error("{}", e);
            return getOutputFromException(e, processBuilder, parameters, action);
        }
    }

    private List<Output> getOutputFrom(Process libreoffice, ProcessBuilder processBuilder, Parameters parameters, Action action) throws IOException {
        List<String> outputFiles = Files.list(batchDirectory.resolve(outputFilesDirName))
            .map(f -> f.getFileName().toString())
            .map(f -> f.contains(".")
                ? f.substring(0, f.lastIndexOf("."))
                : f)
            .collect(toList());

        String libreofficeStdErr = stdToString(libreoffice.getErrorStream());

        return parameters.getInputs()
            .stream()
            .map(input -> getOutput(libreofficeStdErr, processBuilder, action, outputFiles, input, parameters.isDebug()))
            .collect(Collectors.toList());
    }

    private Output getOutput(String libreofficeStdErr, ProcessBuilder processBuilder, Action action, List<String> outputFiles, Input input, boolean debug) {
        String inputName = input.getName().contains(".")
            ? input.getName().substring(0, input.getName().lastIndexOf("."))
            : input.getName();

        String commands = String.join(" ", processBuilder.command());
        if (outputFiles.contains(inputName)) {
            String outputName = inputName + "." + action.getValues().getExtension();
            return debug
                ? Output.ok(input, outputName, action.getType(), "", "", commands)
                : Output.ok(input, outputName, action.getType());
        }
        return debug
            ? Output.error(input, action.getType(), libreofficeStdErr, commands)
            : Output.error(input, action.getType());
    }

    private String stdToString(InputStream std) {
        try {
            StringBuilder textBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(std, UTF_8))) {
                String c;
                while ((c = reader.readLine()) != null) {
                    textBuilder.append(c);
                }
            }
            return textBuilder.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Output> getOutputFromException(Exception exception, ProcessBuilder processBuilder, Parameters parameters, Action action) {
        try {
            List<String> outputFiles = Files.list(batchDirectory.resolve(outputFilesDirName))
                .map(f -> f.getFileName().toString())
                .map(f -> f.contains(".")
                    ? f.substring(0, f.lastIndexOf("."))
                    : f)
                .collect(toList());

            return parameters.getInputs()
                .stream()
                .map(input -> getOutput(exception.getMessage(), processBuilder, action, outputFiles, input, parameters.isDebug()))
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> getLibreOfficeParams(Action action, List<Input> inputs) {
        boolean inputsIsInFormatList = inputs.stream().map(Input::getFormatId).allMatch(formatTypes::containsKey);
        if (!inputsIsInFormatList) {
            throw new IllegalStateException("Cannot proceed inputs");
        }

        List<String> libreoffice = new ArrayList<>(Arrays.asList("libreoffice", "--nolockcheck", "--norestore", "--headless", "--convert-to"));

        if (action.getValues().getArgs() == null || action.getValues().getArgs().isEmpty()) {
            libreoffice.add(action.getValues().getExtension());
        } else {
            libreoffice.add(String.format("%s:%s", action.getValues().getExtension(), String.join(":", action.getValues().getArgs())));
        }

        libreoffice.add("--outdir");
        libreoffice.add(batchDirectory.resolve(outputFilesDirName).toString());
        libreoffice.addAll(inputs.stream().map(i -> batchDirectory.resolve(inputFilesDirName).resolve(i.getName()).toString()).collect(toList()));
        return libreoffice;
    }
}
