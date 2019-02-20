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

package fr.gouv.vitam.griffins.imagemagick.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExtractedMetadata {
    @JsonProperty("MetadataToReplace")
    private Map<String, String> metadataToReplace;

    @JsonProperty("MetadataToAdd")
    private Map<String, List<String>> metadataToAdd;

    @JsonProperty("RawMetadata")
    private String rawMetadata;

    public ExtractedMetadata() {
        // Empty constructor for deserialization
    }

    public ExtractedMetadata(Map<String, String> metadataToReplace, Map<String, List<String>> metadataToAdd, String rawMetadata) {
        this.metadataToReplace = metadataToReplace;
        this.metadataToAdd = metadataToAdd;
        this.rawMetadata = rawMetadata;
    }

    public Map<String, String> getMetadataToReplace() {
        if (metadataToReplace == null) {
            return new HashMap<>();
        }
        return metadataToReplace;
    }

    public void setMetadataToReplace(Map<String, String> metadataToReplace) {
        this.metadataToReplace = metadataToReplace;
    }

    public Map<String, List<String>> getMetadataToAdd() {
        if (metadataToAdd == null) {
            return new HashMap<>();
        }
        return metadataToAdd;
    }

    public void setMetadataToAdd(Map<String, List<String>> metadataToAdd) {
        this.metadataToAdd = metadataToAdd;
    }

    public String getRawMetadata() {
        return rawMetadata;
    }

    public void setRawMetadata(String rawMetadata) {
        this.rawMetadata = rawMetadata;
    }
}
