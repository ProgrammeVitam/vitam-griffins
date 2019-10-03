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

package fr.gouv.vitam.griffins.ffmpeg;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PuidFfmpegType {
    public static final Map<String, String> formatTypes = Collections.unmodifiableMap(Stream.of(new SimpleImmutableEntry<>("fmt/1", "WAV"),
            new SimpleImmutableEntry<>("fmt/132", "WMA"), new SimpleImmutableEntry<>("fmt/134", "MP3"), new SimpleImmutableEntry<>("fmt/141", "WAV"),
            new SimpleImmutableEntry<>("fmt/142", "WAV"), new SimpleImmutableEntry<>("fmt/143", "WAV"), new SimpleImmutableEntry<>("fmt/198", "MP2"),
            new SimpleImmutableEntry<>("fmt/2", "WAV"), new SimpleImmutableEntry<>("fmt/203", "OGG"), new SimpleImmutableEntry<>("fmt/209", "SD2"),
            new SimpleImmutableEntry<>("fmt/279", "FLAC"), new SimpleImmutableEntry<>("fmt/314", "DXR"),
            new SimpleImmutableEntry<>("fmt/315", "PSID"), new SimpleImmutableEntry<>("fmt/316", "SID"), new SimpleImmutableEntry<>("fmt/323", "XM"),
            new SimpleImmutableEntry<>("fmt/347", "MP1"), new SimpleImmutableEntry<>("fmt/356", "AMR"), new SimpleImmutableEntry<>("fmt/357", "3GP"),
            new SimpleImmutableEntry<>("fmt/404", "RA"), new SimpleImmutableEntry<>("fmt/414", "AIF"), new SimpleImmutableEntry<>("fmt/416", "CAF"),
            new SimpleImmutableEntry<>("fmt/5", "AVI"), new SimpleImmutableEntry<>("fmt/527", "WAV"), new SimpleImmutableEntry<>("fmt/596", "MP4"),
            new SimpleImmutableEntry<>("fmt/6", "WAV"), new SimpleImmutableEntry<>("fmt/703", "WAV"), new SimpleImmutableEntry<>("fmt/704", "WAV"),
            new SimpleImmutableEntry<>("fmt/705", "WAV"), new SimpleImmutableEntry<>("fmt/706", "WAV"), new SimpleImmutableEntry<>("fmt/707", "WAV"),
            new SimpleImmutableEntry<>("fmt/708", "WAV"), new SimpleImmutableEntry<>("fmt/709", "WAV"), new SimpleImmutableEntry<>("fmt/710", "WAV"),
            new SimpleImmutableEntry<>("fmt/711", "WAV"), new SimpleImmutableEntry<>("fmt/716", "MOD"), new SimpleImmutableEntry<>("fmt/722", "OKT"),
            new SimpleImmutableEntry<>("fmt/735", "AC3"), new SimpleImmutableEntry<>("fmt/840", "ADX"), new SimpleImmutableEntry<>("fmt/841", "AIX"),
            new SimpleImmutableEntry<>("fmt/946", "OGG"), new SimpleImmutableEntry<>("fmt/947", "OGG"), new SimpleImmutableEntry<>("fmt/948", "OGG"),
            new SimpleImmutableEntry<>("fmt/952", "TTA"), new SimpleImmutableEntry<>("fmt/953", "TTA"), new SimpleImmutableEntry<>("fmt/954", "AWB"),
            new SimpleImmutableEntry<>("fmt/955", "DLS"), new SimpleImmutableEntry<>("fmt/961", "MXMF"), new SimpleImmutableEntry<>("fmt/962", "QCP"),
            new SimpleImmutableEntry<>("fmt/972", "MLP"), new SimpleImmutableEntry<>("fmt/973", "DTS"),
            new SimpleImmutableEntry<>("x-fmt/136", "AIFC"), new SimpleImmutableEntry<>("x-fmt/139", "AU"),
            new SimpleImmutableEntry<>("x-fmt/183", "RAM"), new SimpleImmutableEntry<>("x-fmt/201", "ULAW"),
            new SimpleImmutableEntry<>("x-fmt/222", "CDA"), new SimpleImmutableEntry<>("x-fmt/230", "MIDI"),
            new SimpleImmutableEntry<>("x-fmt/278", "RA"), new SimpleImmutableEntry<>("x-fmt/279", "M3U"),
            new SimpleImmutableEntry<>("x-fmt/389", "WAV"), new SimpleImmutableEntry<>("x-fmt/396", "WAV"),
            new SimpleImmutableEntry<>("x-fmt/397", "WAV"), new SimpleImmutableEntry<>("fmt/133", "WMV"),
            new SimpleImmutableEntry<>("fmt/199", "MP4"), new SimpleImmutableEntry<>("fmt/204", "RV"), new SimpleImmutableEntry<>("fmt/337", "MJ2"),
            new SimpleImmutableEntry<>("fmt/357", "3GP"), new SimpleImmutableEntry<>("fmt/383", "VICAR"),
            new SimpleImmutableEntry<>("fmt/425", "VOB"), new SimpleImmutableEntry<>("fmt/441", "WMV"), new SimpleImmutableEntry<>("fmt/499", "VIV"),
            new SimpleImmutableEntry<>("fmt/5", "AVI"), new SimpleImmutableEntry<>("fmt/528", "MNG"), new SimpleImmutableEntry<>("fmt/573", "WEBM"),
            new SimpleImmutableEntry<>("fmt/731", "BIK"), new SimpleImmutableEntry<>("fmt/732", "BIK2"), new SimpleImmutableEntry<>("fmt/945", "OGG"),
            new SimpleImmutableEntry<>("x-fmt/152", "DV"), new SimpleImmutableEntry<>("x-fmt/277", "RV"),
            new SimpleImmutableEntry<>("x-fmt/382", "FLV"), new SimpleImmutableEntry<>("x-fmt/384", "MOV"),
            new SimpleImmutableEntry<>("x-fmt/385", "MPEG"), new SimpleImmutableEntry<>("x-fmt/386", "MPEG"))
            .collect(Collectors.toMap(SimpleImmutableEntry::getKey, SimpleImmutableEntry::getValue)));

    private PuidFfmpegType() {
    }
}
