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

package fr.gouv.vitam.griffins.libreoffice.pojo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.nio.file.StandardOpenOption.CREATE;

public class LibreOfficeRegisteryModifications {
    private final String fileName = "registrymodifications.xcu";

    private static final String namePlaceHolder = "%name%";
    private static final String valuePlaceHolder = "%value%";
    private static final String lineToReplace = "<item oor:path=\"/org.openoffice.Office.Common/Filter/PDF/Export\"><prop oor:name=\"" + namePlaceHolder + "\" oor:op=\"fuse\"><value>" + valuePlaceHolder + "</value></prop></item>";

    private final String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<oor:items xmlns:oor=\"http://openoffice.org/2001/registry\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">";
    private final String footer = "</oor:items>";

    private final List<String> lines = Collections.unmodifiableList(
        Arrays.asList(
            "<item oor:path=\"/org.openoffice.Office.Common/Misc\"><prop oor:name=\"DisableUICustomization\" oor:op=\"fuse\"><value>false</value></prop></item>",
            "<item oor:path=\"/org.openoffice.Office.Common/Misc\"><prop oor:name=\"FirstRun\" oor:op=\"fuse\"><value>false</value></prop></item>",
            "<item oor:path=\"/org.openoffice.Office.Common/Misc\"><prop oor:name=\"MacroRecorderMode\" oor:op=\"fuse\"><value>false</value></prop></item>",
            "<item oor:path=\"/org.openoffice.Office.Common/Misc\"><prop oor:name=\"NotebookbarIconSize\" oor:op=\"fuse\"><value>0</value></prop></item>",
            "<item oor:path=\"/org.openoffice.Office.Common/Misc\"><prop oor:name=\"Persona\" oor:op=\"fuse\"><value>no</value></prop></item>",
            "<item oor:path=\"/org.openoffice.Office.Common/Misc\"><prop oor:name=\"PersonaSettings\" oor:op=\"fuse\"><value></value></prop></item>",
            "<item oor:path=\"/org.openoffice.Office.Common/Misc\"><prop oor:name=\"SidebarIconSize\" oor:op=\"fuse\"><value>0</value></prop></item>",
            "<item oor:path=\"/org.openoffice.Office.Common/Misc\"><prop oor:name=\"SymbolSet\" oor:op=\"fuse\"><value>2</value></prop></item>",
            "<item oor:path=\"/org.openoffice.Office.Common/Misc\"><prop oor:name=\"SymbolStyle\" oor:op=\"fuse\"><value>auto</value></prop></item>",
            "<item oor:path=\"/org.openoffice.Office.Common/Misc\"><prop oor:name=\"ToolboxStyle\" oor:op=\"fuse\"><value>1</value></prop></item>",
            "<item oor:path=\"/org.openoffice.Office.Common/Misc\"><prop oor:name=\"UseOpenCL\" oor:op=\"fuse\"><value>false</value></prop></item>",
            "<item oor:path=\"/org.openoffice.Office.Common/Misc\"><prop oor:name=\"UseSystemPrintDialog\" oor:op=\"fuse\"><value>false</value></prop></item>",
            "<item oor:path=\"/org.openoffice.Setup/L10N\"><prop oor:name=\"ooLocale\" oor:op=\"fuse\"><value>en-US</value></prop></item>",
            "<item oor:path=\"/org.openoffice.Setup/Office\"><prop oor:name=\"OfficeRestartInProgress\" oor:op=\"fuse\"><value>false</value></prop></item>",
            "<item oor:path=\"/org.openoffice.Setup/Office\"><prop oor:name=\"ooSetupInstCompleted\" oor:op=\"fuse\"><value>true</value></prop></item>"
        ));

    private final List<String> linesToAdd;

    public LibreOfficeRegisteryModifications(List<String> registryProperty) {
        this.linesToAdd = registryProperty.stream()
            .filter(r -> r.toLowerCase().startsWith("pdf:"))
            .map(this::transformArgsInEntry)
            .map(this::mapEntryToRegistryLine)
            .collect(Collectors.toList());
    }

    private Entry<String, String> transformArgsInEntry(String r) {
        String[] keyValue = r.replace("pdf:", "").split("=");
        return new SimpleImmutableEntry<>(keyValue[0], keyValue[1]);
    }

    private String mapEntryToRegistryLine(Entry<String, String> property) {
        ExportPdfVariable exportPdfVariable = ExportPdfVariable.valueOf(property.getKey());
        return lineToReplace.replace(namePlaceHolder, exportPdfVariable.name()).replace(valuePlaceHolder, exportPdfVariable.from(property.getValue()));
    }

    public Path toFile(Path path) throws IOException {
        List<String> objects = new ArrayList<>();
        objects.add(header);
        objects.addAll(linesToAdd);
        objects.addAll(lines);
        objects.add(footer);

        return Files.write(path.resolve(fileName), objects, CREATE);
    }

    enum ExportPdfVariable {
        Quality(90),
        OpenBookmarkLevels(-1),
        MaxImageResolution(300),
        FormsType(0),
        InitialView(0),
        Magnification(0),
        Zoom(100),
        PageLayout(0),
        InitialPage(1),
        UseLosslessCompression(false),
        ReduceImageResolution(false),
        UseTaggedPDF(false),
        ExportNotes(false),
        ExportNotesPages(false),
        ExportBookmarks(true),
        UseTransitionEffects(true),
        ExportFormFields(true),
        IsSkipEmptyPages(false),
        HideViewerMenubar(false),
        HideViewerToolbar(false),
        HideViewerWindowControls(false),
        ResizeWindowToInitialPage(false),
        CenterWindow(false),
        OpenInFullScreenMode(false),
        DisplayPDFDocumentTitle(true),
        SelectPdfVersion(0),
        FirstPageOnLeft(false);

        public final Function<String, String> mapper;

        ExportPdfVariable(int defaultValue) {
            this.mapper = v -> Integer.valueOf(v).toString();
        }

        ExportPdfVariable(Boolean defaultValue) {
            this.mapper = v -> parseBoolean(v).toString();
        }

        public String from(String value) {
            return mapper.apply(value);
        }

        private Boolean parseBoolean(String s) {
            if (s.equalsIgnoreCase("false")) {
                return Boolean.FALSE;
            }
            if (s.equalsIgnoreCase("true")) {
                return Boolean.TRUE;
            }
            throw new IllegalStateException(String.format("String %s is not a boolean", s));
        }
    }
}
