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

package fr.gouv.vitam.griffins.verapdf.specific;

import java.io.*;

import fr.gouv.vitam.griffins.verapdf.Main;
import fr.gouv.vitam.griffins.verapdf.pojo.Action;
import fr.gouv.vitam.griffins.verapdf.status.ActionType;
import fr.gouv.vitam.griffins.verapdf.status.AnalyseResult;
import org.verapdf.pdfa.Foundries;
import org.verapdf.pdfa.PDFAParser;
import org.verapdf.pdfa.PDFAValidator;
import org.verapdf.pdfa.VeraGreenfieldFoundryProvider;
import org.verapdf.pdfa.flavours.PDFAFlavour;
import org.verapdf.pdfa.results.ValidationResult;

/**
 * Class encapsulating the tool used by the griffin.
 */
public class InnerTool {

    /**
     * Instantiates a new inner tool.
     *
     * @throws Exception the exception
     */
    public InnerTool() throws Exception {
        VeraGreenfieldFoundryProvider.initialise();
    }

    // workaround for the fact that VeraPDF is not thread safe
    // as this method is static and synchronized only one VeraPDF call at a time is possible
    private static synchronized ValidationResult synchronizedValidate(String fileName, String flavourName) throws Exception {
        PDFAFlavour flavour = PDFAFlavour.fromString(flavourName);

        PDFAValidator validator = Foundries.defaultInstance().createValidator(flavour, false);
        try (PDFAParser parser = Foundries.defaultInstance().createParser(new File(fileName), flavour)) {
            ValidationResult result = validator.validate(parser);
            return result;
        }

    }


    private RawOutput doValidate(String fileName, String format) {
        RawOutput result = null;

        // get the PDF/A sub type to verify
        String subType = PuidType.formatTypes.get(format).substring(6);

        ValidationResult answer;
        try {
             answer = synchronizedValidate(fileName,subType);
        } catch (Exception e) {
            return new RawOutput(e);
        }

        if (answer.isCompliant())
            result = new RawOutput("PDF/A-"+subType, AnalyseResult.VALID_ALL);
        else
            result= new RawOutput("PDF/A-"+subType, AnalyseResult.NOT_VALID);

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
