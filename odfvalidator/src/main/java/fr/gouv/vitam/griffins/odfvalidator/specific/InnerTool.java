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

package fr.gouv.vitam.griffins.odfvalidator.specific;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import fr.gouv.vitam.griffins.odfvalidator.pojo.Action;
import fr.gouv.vitam.griffins.odfvalidator.status.AnalyseResult;
import fr.gouv.vitam.griffins.odfvalidator.Main;
import fr.gouv.vitam.griffins.odfvalidator.status.ActionType;
import org.odftoolkit.odfvalidator.*;

/**
 * Class encapsulating the tool used by the griffin.
 */
public class InnerTool {

    Map<String,ODFValidator> validators;
    Map<String,OdfValidatorMode> validationModes;

    /**
     * Instantiates a new inner tool.
     *
     * @throws Exception the exception
     */
    public InnerTool() throws Exception {
        validators=new HashMap<String,ODFValidator>();
        validators.put("ODF1.0",new ODFValidator(null, Logger.LogLevel.ERROR, OdfVersion.valueOf("1.0", false), false));
        validators.put("ODF1.1",new ODFValidator(null, Logger.LogLevel.ERROR, OdfVersion.valueOf("1.1", false), false));
        validators.put("ODF1.2",new ODFValidator(null, Logger.LogLevel.ERROR, OdfVersion.valueOf("1.2", false), false));
        validationModes=new HashMap<String,OdfValidatorMode>();
        validationModes.put("ODF1.0",OdfValidatorMode.VALIDATE);
        validationModes.put("ODF1.1",OdfValidatorMode.VALIDATE);
        validationModes.put("ODF1.2",OdfValidatorMode.EXTENDED_CONFORMANCE);
    }

    private RawOutput doValidate(String fileName, String format) {
        RawOutput result = null;

        // get the OdfValidator context
        PuidContext context = PuidType.formatTypes.get(format);
        ODFValidator odfValidator=null;
        odfValidator = validators.get(context.category);
        OdfValidatorMode odfMode=validationModes.get(context.category);

        ODFDetailedResult answer;
        try (OutputStream nullOutputStream = new OutputStream() { @Override public void write(int b) { } };
             PrintStream nullPrintStream=new PrintStream(nullOutputStream)){
            // the detailedValidateFile has been added to ODFValidator
            answer = odfValidator.detailedValidateFile(nullPrintStream, new File(fileName), odfMode, null);
        } catch (Exception e) {
            return new RawOutput(e);
        }

        if ((context.category.indexOf(answer.getDocVersion()) == -1) ||
                !answer.getMimeType().contains(context.type)) {
            result = new RawOutput(context.category, AnalyseResult.WRONG_FORMAT);
        }
        else if (answer.getHasErrors()==0)
            result = new RawOutput(context.category+"-"+context.type, AnalyseResult.VALID_ALL);
        else
            result= new RawOutput(context.category+"-"+context.type, AnalyseResult.NOT_VALID);

        return result;
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
        if (!action.getType().equals(ActionType.ANALYSE))
            return new RawOutput(new Exception(Main.ID + " can only ANALYSE"));
        return doValidate(inputFileName, format);
    }
}
