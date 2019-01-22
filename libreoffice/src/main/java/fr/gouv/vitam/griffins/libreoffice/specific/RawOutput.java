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

import fr.gouv.vitam.griffins.libreoffice.BatchProcessor;
import fr.gouv.vitam.griffins.libreoffice.pojo.Action;
import fr.gouv.vitam.griffins.libreoffice.pojo.Input;
import fr.gouv.vitam.griffins.libreoffice.pojo.Output;
import fr.gouv.vitam.griffins.libreoffice.status.AnalyseResult;

import java.io.*;

import static fr.gouv.vitam.griffins.libreoffice.status.ActionType.GENERATE;

/**
 * Class for the InnerTool output, this has to be converted to Output class which is the standard griffin output.
 */
public class RawOutput {
    /**
     * The Action (generic variable).
     */
    public Action action;
    /**
     * The Input (generic variable).
     */
    public Input input;
    /**
     * The Analyse result (generic variable).
     */
    public AnalyseResult analyseResult;
    /**
     * /**
     * The output file name (generic variable).
     */
    public String outputFileName;
    /**
     * The Error message (generic variable).
     */
    public String errorMessage;
    /**
     * The Execution context (generic variable).
     */
    public String executionContext;

    /**
     * The process exit code (specific to system command tool variable)
     */
    public int exitCode;

    /**
     * The process std out (specific to system command tool variable)
     */
    public String stdout;

    /**
     * The process std err (specific to system command tool variable)
     */
    public String stderr;

    /**
     * Instantiates a new Raw output, the standard way using exception.
     *
     * @param exception the exception
     */
    public RawOutput(Exception exception) {
        this.executionContext = "unknown command";
        this.analyseResult = null;
        if (exception != null) {
            StringWriter sw = new StringWriter();
            exception.printStackTrace(new PrintWriter(sw));
            this.errorMessage = exception.getMessage() + "\n" + sw.toString();
        } else
            this.errorMessage = "";
        this.exitCode = -1;
        this.stdout = "";
        this.stderr = "";
    }

    /**
     * Instantiates a new Raw output, the standard way using exception in system command execution context.
     *
     * @param process the Process
     * @param processBuilder the ProcessBuilder
     * @param exception the exception
     */
    public RawOutput(Process process, ProcessBuilder processBuilder, Exception exception) {
        if (processBuilder!=null)
            this.executionContext = String.join(" ", processBuilder.command());
        else
            this.executionContext = "no command";
        this.analyseResult = null;
        if (exception != null) {
            StringWriter sw = new StringWriter();
            exception.printStackTrace(new PrintWriter(sw));
            this.errorMessage = exception.getMessage() + "\n" + sw.toString();
        } else
            this.errorMessage = "";
        this.exitCode = process.exitValue();
        this.stdout = "";
        this.stderr = "";
        try {
            this.stdout = BatchProcessor.stdToString(process.getInputStream());
            this.stderr = BatchProcessor.stdToString(process.getErrorStream());
        } catch (Exception ignored) {
        }
    }

    /**
     * Instantiates a new Raw output, the standard way with an outputFileName generation in system command execution context.
     *
     * @param process the Process
     * @param processBuilder the ProcessBuilder
     * @param outputFileName the exception
     */
    public RawOutput(Process process, ProcessBuilder processBuilder, String outputFileName) throws IOException {
        this.executionContext = String.join(" ", processBuilder.command());
        this.outputFileName = outputFileName;
        this.errorMessage = null;
        this.exitCode = process.exitValue();
        this.stdout = "";
        this.stderr = "";
        try {
            this.stdout = BatchProcessor.stdToString(process.getInputStream());
            this.stderr = BatchProcessor.stdToString(process.getErrorStream());
        } catch (Exception ignored) {
        }
    }

    /**
     * Instantiates a new Raw output, the standard way with an outputFileName generation without system command execution context.
     *
     * @param executionContext the information on parameters used for this treatment
     * @param outputFileName the exception
     */
    public RawOutput(String executionContext, String outputFileName) throws IOException {
        this.executionContext = executionContext;
        this.outputFileName = outputFileName;
        this.errorMessage = null;
        this.exitCode = 0;
        this.stdout = "";
        this.stderr = "";
    }

    /**
     * Sets context (which Action on which Input).
     *
     * @param input  the input
     * @param action the action
     * @return the context
     */
    public RawOutput setContext(Input input, Action action) {
        this.input = input;
        this.action = action;
        return this;
    }

    /**
     * Process inner tool RawOutput to griffin standard Output.
     *
     * @param debug the debug
     * @return the output
     * @throws RuntimeException the runtime exception
     */
    public Output postProcess(boolean debug) throws RuntimeException {
        switch (action.getType()) {
            case GENERATE:
                Output result;
                if (errorMessage != null) {
                    return debug
                            ? Output.error(input, action.getType(), errorMessage, executionContext)
                            : Output.error(input, action.getType());
                }

                if (exitCode!=0)
                    result = Output.error(input, action.getType(), stderr,executionContext);
                else if (debug)
                    result = Output.ok(input, outputFileName, action.getType(),stderr,stdout,executionContext);
                else
                    result = Output.ok(input, outputFileName, action.getType());
                return result;
            default:
                throw new IllegalStateException(String.format("Cannot post process data from action of type %s only %s is supported.", action.getType(), GENERATE));
        }
    }
}