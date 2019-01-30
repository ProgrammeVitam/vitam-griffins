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

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PuidType {
    public static final Map<String, String> formatTypes = Collections.unmodifiableMap(
        Stream.of(
            new SimpleImmutableEntry<>("fmt/40", "Microsoft Word 97/2000/XP"),
            new SimpleImmutableEntry<>("fmt/39", "Microsoft Word 6.0"),
            new SimpleImmutableEntry<>("fmt/412", "Microsoft Word Open XML 2007"),
            new SimpleImmutableEntry<>("fmt/280", "LaTeX (Master Document)"),
            new SimpleImmutableEntry<>("fmt/281", "LaTeX (Sub Document)"),
            new SimpleImmutableEntry<>("fmt/136", "ODF Text Document 1.0"),
            new SimpleImmutableEntry<>("fmt/290", "ODF Text Document 1.1"),
            new SimpleImmutableEntry<>("fmt/291", "ODF Text Document 1.2"),
            new SimpleImmutableEntry<>("x-fmt/94", "Pocket Word"),
            new SimpleImmutableEntry<>("fmt/969", "Rich Text Format 0"),
            new SimpleImmutableEntry<>("fmt/45", "Rich Text Format 1.0-1.4"),
            new SimpleImmutableEntry<>("fmt/50", "Rich Text Format 1.5-1.6"),
            new SimpleImmutableEntry<>("fmt/52", "Rich Text Format 1.7"),
            new SimpleImmutableEntry<>("fmt/53", "Rich Text Format 1.8"),
            new SimpleImmutableEntry<>("fmt/355", "Rich Text Format 1.9"),
            new SimpleImmutableEntry<>("x-fmt/400", "StarWriter 5.0"),
            new SimpleImmutableEntry<>("fmt/813", "StarWriter 4.0"),
            new SimpleImmutableEntry<>("fmt/812", "StarWriter 3.0"),
            new SimpleImmutableEntry<>("fmt/128", "Open Office.org 1.0 Text Document"),
            new SimpleImmutableEntry<>("x-fmt/111", "Text"),
            new SimpleImmutableEntry<>("x-fmt/44", "WordPerfect 6.0"),
            new SimpleImmutableEntry<>("x-fmt/203", "WordPerfect 5.2"),
            new SimpleImmutableEntry<>("x-fmt/393", "WordPerfect 5.0"),
            new SimpleImmutableEntry<>("fmt/949", "WordPerfect 4.0/4.1/4.2"),
            new SimpleImmutableEntry<>("x-fmt/394", "WordPerfect 5.1"),
            new SimpleImmutableEntry<>("fmt/163", "MS Works 1-3"),
            new SimpleImmutableEntry<>("fmt/233", "MS Works 3-4"),

            new SimpleImmutableEntry<>("x-fmt/18", "Text CSV"),
            new SimpleImmutableEntry<>("x-fmt/8", "dBASE II"),
            new SimpleImmutableEntry<>("x-fmt/9", "dBASE III"),
            new SimpleImmutableEntry<>("x-fmt/271", "dBASE III+"),
            new SimpleImmutableEntry<>("x-fmt/10", "dBASE IV"),
            new SimpleImmutableEntry<>("x-fmt/272", "dBASE V"),
            new SimpleImmutableEntry<>("x-fmt/41", "Data Interchange Format"),
            new SimpleImmutableEntry<>("fmt/137", "ODF SpreadSheet 1.0"),
            new SimpleImmutableEntry<>("fmt/294", "ODF SpreadSheet 1.1"),
            new SimpleImmutableEntry<>("fmt/295", "ODF SpreadSheet 1.2"),
            new SimpleImmutableEntry<>("fmt/214", "Microsoft Excel Open XML 2007"),
            new SimpleImmutableEntry<>("x-fmt/359", "StarCalc 5.0"),
            new SimpleImmutableEntry<>("fmt/809", "StarCalc 4.0"),
            new SimpleImmutableEntry<>("fmt/808", "StarCalc 3.0"),
            new SimpleImmutableEntry<>("fmt/129", "OpenOffice.org 1.0 Spreadsheet"),
            new SimpleImmutableEntry<>("fmt/61", "Microsoft Excel 97/2000/XP"),
            new SimpleImmutableEntry<>("fmt/59", "Microsoft Excel 95"),
            new SimpleImmutableEntry<>("x-fmt/17", "Microsoft Excel 97/2000/XP"),
            new SimpleImmutableEntry<>("fmt/598", "Microsoft Excel 2007/2010 XML Template"),

            new SimpleImmutableEntry<>("fmt/139", "OpenDocument Graphics 1.0"),
            new SimpleImmutableEntry<>("fmt/296", "OpenDocument Graphics 1.1"),
            new SimpleImmutableEntry<>("fmt/297", "OpenDocument Graphics 1.2"),
            new SimpleImmutableEntry<>("fmt/127", "OpenOffice.org 1.0 Drawing"),
            new SimpleImmutableEntry<>("x-fmt/401", "StarDraw 5.0"),
            new SimpleImmutableEntry<>("fmt/811", "StarDraw 4.0"),
            new SimpleImmutableEntry<>("fmt/810", "StarDraw 3.0"),

            new SimpleImmutableEntry<>("fmt/138", "OpenDocument Presentation 1.0"),
            new SimpleImmutableEntry<>("fmt/292", "OpenDocument Presentation 1.1"),
            new SimpleImmutableEntry<>("fmt/293", "OpenDocument Presentation 1.2"),
            new SimpleImmutableEntry<>("fmt/631", "Microsoft Powerpoint 2007/2010 XML Template"),
            new SimpleImmutableEntry<>("x-fmt/84", "Microsoft PowerPoint 97/2000/XP Template"),
            new SimpleImmutableEntry<>("fmt/126", "Microsoft PowerPoint 97/2000/XP"),
            new SimpleImmutableEntry<>("fmt/215", "Microsoft PowerPoint Open XML 2007"),
            new SimpleImmutableEntry<>("x-fmt/87", "Microsoft PowerPoint 97/2000/XP (Autoplay)"),
            new SimpleImmutableEntry<>("x-fmt/360", "StarImpress 5.0"),
            new SimpleImmutableEntry<>("fmt/815", "StarImpress 4.0"),
            new SimpleImmutableEntry<>("fmt/814", "StarImpress 3.0"),
            new SimpleImmutableEntry<>("fmt/130", "OpenOffice.org 1.0 Presentation")
        ).collect(Collectors.toMap(SimpleImmutableEntry::getKey, SimpleImmutableEntry::getValue))
    );

    private PuidType() {
    }
}
