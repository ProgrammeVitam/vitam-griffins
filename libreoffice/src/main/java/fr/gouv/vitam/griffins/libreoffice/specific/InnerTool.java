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

package fr.gouv.vitam.griffins.libreoffice.specific;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import fr.gouv.vitam.griffins.libreoffice.BatchProcessor;
import fr.gouv.vitam.griffins.libreoffice.pojo.Action;
import fr.gouv.vitam.griffins.libreoffice.status.ActionType;
import fr.gouv.vitam.griffins.libreoffice.Main;
import org.slf4j.LoggerFactory;


/**
 * Class encapsulating the tool used by the griffin.
 */
public class InnerTool {

    /**
     * The LibreOffice Loader
     */
    Process officeServerProcess;

    /**
     * Instantiates a new inner tool.
     *
     * @throws Exception the exception
     */
    public InnerTool() throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder("unoconv", "--listener");

        // launch a soffice listener server, if already running or launchinf error immediate return
        Process unoconv = processBuilder.start();
        boolean endedFlag = unoconv.waitFor(10, TimeUnit.SECONDS);
        if (endedFlag) {
            String stdErr = BatchProcessor.stdToString(unoconv.getErrorStream());
            if (!stdErr.isEmpty())
                throw new Exception("Unoconv returned error [" + stdErr+"]");
        }
    }

    private List<String> getUnoconvParams(Action action, String inputFileName, String outputFileName) {
        // define unoconv args
        List<String> actionCommand = new ArrayList<>(action.getType().action);

        actionCommand.replaceAll(c -> c.equals("%inputname%") ? inputFileName : c);
        actionCommand.replaceAll(c -> c.equals("%outputname%") ? outputFileName : c);

        int indexOf = actionCommand.indexOf("%args%");
        if (indexOf != -1) {
            actionCommand.remove(indexOf);
            actionCommand.add(indexOf, "-vvv");
            actionCommand.addAll(indexOf + 1, action.getValues().getArgs());
        }

        return actionCommand;
    }

    private RawOutput doGenerate(Action action, String inputFileName, String outputFileName) {
        RawOutput result = null;

        ProcessBuilder processBuilder = new ProcessBuilder(getUnoconvParams(action, inputFileName, outputFileName));
        Process unoconv =null;
        try {
            unoconv = processBuilder.start();
            // TODO timeout
            unoconv.waitFor();
            return new RawOutput(unoconv, processBuilder, outputFileName);
        } catch (Exception e) {
            LoggerFactory.getLogger(Main.class).error("{}", e);
            return new RawOutput(unoconv, processBuilder,e);
        }
    }

    /**
     * Apply the action in the inner tool for one file in given format.
     * <p>
     * This is the main class to adapt for a inner tool treating file by file
     *
     * @param action         the action
     * @param inputFileName  the input file name
     * @param format         the format
     * @param outputFileName the output file name if relevant
     * @return the raw output
     */
    public RawOutput apply(Action action, String inputFileName, String format, String outputFileName, boolean debugFlag) {
        if (!action.getType().equals(ActionType.GENERATE))
            return new RawOutput(null,null,new Exception(Main.ID + " can only GENERATE"));
        return doGenerate(action, inputFileName, outputFileName);
    }
}

