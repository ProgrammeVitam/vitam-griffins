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

package fr.gouv.vitam.griffins.jhove.specific;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import edu.harvard.hul.ois.jhove.App;
import edu.harvard.hul.ois.jhove.JhoveBase;
import edu.harvard.hul.ois.jhove.Module;
import edu.harvard.hul.ois.jhove.RepInfo;
import fr.gouv.vitam.griffins.jhove.Main;
import fr.gouv.vitam.griffins.jhove.pojo.Action;
import fr.gouv.vitam.griffins.jhove.status.ActionType;
import fr.gouv.vitam.griffins.jhove.status.AnalyseResult;

/**
 * Class encapsulating the tool used by the griffin.
 * <p>
 * Here Jhove java library.
 */
public class InnerTool {

    private final App jhoveAPP;
    private final JhoveBase jhoveBase;

    private void getJhoveConf() throws IOException {
        String result = null;

        Path conf = Paths.get("config", "jhove.conf");
        if (Files.isRegularFile(conf))
            return;

        // create config/jhove.conf file from resources
        Files.createDirectories(Paths.get("config"));
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("jhove.conf");
        Files.copy(is, conf, StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Instantiates a new inner tool.
     *
     * @throws Exception the exception
     */
    public InnerTool() throws Exception {
        jhoveAPP = App.newAppWithName("Jhove");

        getJhoveConf();
        jhoveBase = new JhoveBase();
        jhoveBase.setLogLevel("WARNING");
        jhoveBase.init("config/jhove.conf", null);
        jhoveBase.setEncoding("utf-8");
        Path tempDirectory = Files.createTempDirectory(Main.ID);
        jhoveBase.setTempDirectory(tempDirectory.toString());
        jhoveBase.setBufferSize(65536);
        jhoveBase.setChecksumFlag(false);
        jhoveBase.setShowRawFlag(false);
        jhoveBase.setSignatureFlag(false);
    }

    private RawOutput doValidate(String fileName, String format) {
        Module jhoveModule;
        RawOutput result;

        // get the Jhove module for the specified format (puidType filter done at global level)
        String moduleName = PuidType.formatTypes.get(format);
        jhoveModule = jhoveBase.getModule(moduleName);

        // do Jhove call
        File file = new File(fileName);
        RepInfo info = new RepInfo(fileName);
        try {
            if (!jhoveBase.processFile(jhoveAPP, jhoveModule, false, file, info)) {
                info.setWellFormed(RepInfo.UNDETERMINED);
            }
        } catch (Exception e) {
            return new RawOutput(e);
        }

        if ((info.getValid() == RepInfo.TRUE)
                // special case for XML-hul module where undetermined
                //is when there's no DTD or schema
                || ((moduleName.equals("XML-hul") && (info.getValid() == RepInfo.UNDETERMINED)))) {
            result = new RawOutput(moduleName, AnalyseResult.VALID_ALL);
        } else if (info.getWellFormed() == RepInfo.TRUE) {
            result = new RawOutput(moduleName, AnalyseResult.WELL_FORMED);
        } else if ((info.getFormat() != null) && (!info.getFormat().isEmpty())) {
            result = new RawOutput(moduleName, AnalyseResult.NOT_VALID);
        } else {
            result = new RawOutput(moduleName, AnalyseResult.WRONG_FORMAT);
        }

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
