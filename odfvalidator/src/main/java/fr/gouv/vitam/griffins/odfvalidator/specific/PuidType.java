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

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class linking formats and any useful information for inner tool.
 */
public class PuidType {

    /**
     * The constant formatTypes and inner tool associated context.
     * <p>
     * Here odfvalidator is the reference at
     * https://incubator.apache.org/odftoolkit/conformance/ODFValidator.html
     *
     */
    public static final Map<String, PuidContext> formatTypes = Collections.unmodifiableMap(
            Stream.of(
                    new SimpleImmutableEntry<>("fmt/136", new PuidContext("ODF1.0","text")),
                    new SimpleImmutableEntry<>("fmt/137", new PuidContext("ODF1.0","spreadsheet")),
                    new SimpleImmutableEntry<>("fmt/138", new PuidContext("ODF1.0","presentation")),
                    new SimpleImmutableEntry<>("fmt/139", new PuidContext("ODF1.0","graphics")),
                    new SimpleImmutableEntry<>("fmt/290", new PuidContext("ODF1.1","text")),
                    new SimpleImmutableEntry<>("fmt/294", new PuidContext("ODF1.1","spreadsheet")),
                    new SimpleImmutableEntry<>("fmt/292", new PuidContext("ODF1.1","presentation")),
                    new SimpleImmutableEntry<>("fmt/296", new PuidContext("ODF1.1","graphics")),
                    new SimpleImmutableEntry<>("fmt/291", new PuidContext("ODF1.2","text")),
                    new SimpleImmutableEntry<>("fmt/295", new PuidContext("ODF1.2","spreadsheet")),
                    new SimpleImmutableEntry<>("fmt/293", new PuidContext("ODF1.2","presentation")),
                    new SimpleImmutableEntry<>("fmt/297", new PuidContext("ODF1.2","graphics"))
            ).collect(Collectors.toMap(SimpleImmutableEntry::getKey, SimpleImmutableEntry::getValue))
    );

    private PuidType() {
    }
}
