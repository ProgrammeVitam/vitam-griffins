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
import org.jodconverter.LocalConverter;
import org.jodconverter.office.LocalOfficeManager;
import org.jodconverter.office.OfficeException;
import org.jodconverter.office.OfficeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fr.gouv.vitam.griffins.libreoffice.status.ActionType.GENERATE;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class BatchProcessor {
    private static final Logger logger = LoggerFactory.getLogger(BatchProcessor.class);

    public static final String outputFilesDirName = "output-files";
    public static final String parametersFileName = "parameters.json";
    public static final String resultFileName = "result.json";
    public static final String inputFilesDirName = "input-files";

    private static final String FILTER_DATA = "FilterData";
    private static final String FILTER_NAME = "FilterName";
    private static final String FILTER_OPTIONS = "FilterOptions";

    private final ObjectMapper mapper = new ObjectMapper();
    private final Path batchDirectory;

    private Parameters parameters;

    public BatchProcessor(Path batchDirectory) {
        this.batchDirectory = batchDirectory;
    }

    public BatchStatus execute() {
        long startTime = System.currentTimeMillis();
        String batchProcessingId = batchDirectory.getFileName().toString();

        LocalOfficeManager officeManager = LocalOfficeManager.install();
        try {
            File file = batchDirectory.resolve(parametersFileName).toFile();
            parameters = mapper.readValue(file, Parameters.class);

            if (!batchProcessingId.equals(parameters.getId())) {
                throw new Exception("Batch id must be same as in " + parametersFileName);
            }

            Files.createDirectories(batchDirectory.resolve(outputFilesDirName));
            officeManager.start();

            List<Output> outputs = parameters.getActions()
                .stream()
                .flatMap(a -> parameters.getInputs().stream().map(i -> execute(a, i, parameters.isDebug())))
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
        } finally {
            stopOfficeManager(officeManager);
        }
    }

    private void addToFile(List<Output> outputs, String requestId, String id) throws IOException {
        Map<String, List<Output>> outputsMap = outputs.stream()
            .collect(
                toMap(o -> o.getInput().getName(), Collections::singletonList, (o, o2) -> Stream.concat(o.stream(), o2.stream()).collect(toList())));
        mapper.writer().writeValue(batchDirectory.resolve(resultFileName).toFile(), Result.of(requestId, id, outputsMap));
    }

    private Output execute(Action action, Input input, boolean debug) {
        if (!GENERATE.equals(action.getType())) {
            throw new IllegalStateException(String.format("Cannot execute libreoffice for action of type %s.", action.getType()));
        }

        File inputFile = batchDirectory.resolve(inputFilesDirName).resolve(input.getName()).toFile();

        String outputName = String.format("%s.%s", input.getName(), action.getValues().getExtension());
        File outputFile = batchDirectory.resolve(outputFilesDirName).resolve(outputName).toFile();

        Map<String, Object> customPropertiesFrom = getCustomPropertiesFrom(action);
        String commands = customPropertiesFrom.entrySet()
            .stream()
            .map(Object::toString)
            .collect(Collectors.joining("&"));

        try {
            LocalConverter.builder()
                .storeProperties(customPropertiesFrom)
                .build()
                .convert(inputFile)
                .to(outputFile)
                .execute();

            return debug
                ? Output.ok(input, outputName, action.getType(), "", "", commands)
                : Output.ok(input, outputName, action.getType());
        } catch (Exception e) {
            logger.error("{}", e);
            return debug
                ? Output.error(input, action.getType(), e.getMessage(), commands)
                : Output.error(input, action.getType());
        }
    }

    private Map<String, Object> getCustomPropertiesFrom(Action action) {
        Map<String, Object> customProperties = new HashMap<>();
        HashMap<String, Object> filterData = new HashMap<>();

        for (String arg : action.getValues().getArgs()) {
            String[] typeKeyValue = arg.split(":");

            String type = typeKeyValue[0];
            String value = typeKeyValue[1];
            if (type.equals(FILTER_NAME)) {
                customProperties.put(FILTER_NAME, value);
                continue;
            }

            if (type.equals(FILTER_OPTIONS)) {
                customProperties.put(FILTER_OPTIONS, value);
                continue;
            }

            if (type.equals(FILTER_DATA)) {
                String[] keyValue = value.split("=");

                String filterDataKey = keyValue[0];
                String filterDataValue = keyValue[1];
                filterData.put(filterDataKey, convertType(filterDataValue));
            }
        }

        if (!filterData.isEmpty()) {
            customProperties.put(FILTER_DATA, filterData);
        }
        return customProperties;
    }

    private Object convertType(String value) {
        if (isInteger(value)) {
            return Integer.valueOf(value);
        }
        if (value.equalsIgnoreCase(TRUE.toString())) {
            return TRUE;
        }
        if (value.equalsIgnoreCase(FALSE.toString())) {
            return FALSE;
        }
        return value;
    }

    private boolean isInteger(String value) {
        try {
            Integer.valueOf(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void stopOfficeManager(OfficeManager manager) {
        try {
            if (manager != null && manager.isRunning()) {
                manager.stop();
            }
        } catch (OfficeException ex) {
            // ignore
        }
    }
}
