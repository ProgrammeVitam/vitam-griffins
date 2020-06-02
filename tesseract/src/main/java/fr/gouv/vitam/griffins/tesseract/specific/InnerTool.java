/*
 * Copyright French Prime minister Office/SGMAP/DINSIC/Vitam Program (2015-2020)
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
package fr.gouv.vitam.griffins.tesseract.specific;

import java.util.ArrayList;
import java.util.List;

import fr.gouv.vitam.griffins.tesseract.Main;
import fr.gouv.vitam.griffins.tesseract.pojo.Action;
import org.slf4j.LoggerFactory;

/**
 * Class encapsulating the tool used by the griffin.
 */
public class InnerTool {

    /**
     * Instantiates a new inner tool.
     *
     */
    public InnerTool() {
        // init the inner tool used
    }

    // define command line if an external tool is used
    private List<String> gettesseractParams(Action action, String inputFileName, String outputFileName) {
         List<String> actionCommand = new ArrayList<>(action.getType().action);

        actionCommand.replaceAll(c -> c.equals("%inputname%") ? inputFileName : c);
        // suppress extension from outputname as tesseract add it
        String extension=action.getValues().getExtension();
        String tmp=outputFileName.substring(0,outputFileName.lastIndexOf(extension)-1);
        actionCommand.replaceAll(c -> c.equals("%outputname%") ? tmp : c);

        int indexOf = actionCommand.indexOf("%args%");
        if (indexOf != -1) {
            actionCommand.remove(indexOf);
            actionCommand.addAll(indexOf, action.getValues().getArgs());
        }

        return actionCommand;
    }

    private RawOutput doAnalyse(Action action, String fileName, String format, boolean debugFlag) {

        // do what's needed for analysis or return error
        // throw new Exception(Main.ID+" can't do Analyse action")

        return null;
    }

    private RawOutput doGenerate(Action action, String inputFileName, String format, String outputFileName, boolean debugFlag) {
        RawOutput result;

        ProcessBuilder processBuilder = new ProcessBuilder(gettesseractParams(action, inputFileName, outputFileName));
        Process tesseract =null;
        try {
            tesseract = processBuilder.start();
            tesseract.waitFor();
            result= new RawOutput(tesseract, processBuilder, outputFileName);
        } catch (Exception e) {
            LoggerFactory.getLogger(Main.class).error("{}", e);
            result= new RawOutput(tesseract, processBuilder,e);
        }

        return result;
    }

    private RawOutput doIdentify(Action action, String inputFileName, String format, String outputFileName, boolean debugFlag) {

        // do what's needed for identification or return error
        // throw new Exception(Main.ID+" can't do Identify action")

        return null;
    }

    private RawOutput doExtractGOT(Action action, String inputFileName, String format, String outputFileName, boolean debugFlag) {

        // do what's needed for extraction of metadata for GOT enrichment or return error
        // throw new Exception(Main.ID+" can't do ExtractGOT action")

        return null;
    }

    private RawOutput doExtractAU(Action action, String inputFileName, String format, String outputFileName, boolean debugFlag) {

        // do what's needed for extraction of metadata for AU enrichment or return error
        // throw new Exception(Main.ID+" can't do ExtractAU action")

        return null;
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
        switch (action.getType()) {
            case ANALYSE:
                return doAnalyse(action, inputFileName, format, debugFlag);
            case EXTRACT_AU:
            case GENERATE:
                return doGenerate(action, inputFileName, format, outputFileName, debugFlag);
            case IDENTIFY:
                return doIdentify(action, inputFileName, format, outputFileName, debugFlag);
            case EXTRACT:
                return doExtractGOT(action, inputFileName, format, outputFileName, debugFlag);
            default:
                return new RawOutput(new Exception(Main.ID+" can't do " + action.getType()));
        }
    }
}
