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

package fr.gouv.vitam.griffins.jhove.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.gouv.vitam.griffins.jhove.status.GriffinStatus;

public class BatchStatus {
    @JsonProperty("BatchId")
    public final String batchId;
    @JsonProperty("StartTime")
    public final long startTime;
    @JsonProperty("EndTime")
    public final long endTime;
    @JsonProperty("Status")
    public final GriffinStatus status;
    @JsonProperty("Error")
    public final String error;

    private BatchStatus(String batchId, long startTime, long endTime, GriffinStatus status, String error) {
        this.batchId = batchId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.error = error;
    }

    public static BatchStatus warning(String batchId, long start, String error) {
        return new BatchStatus(batchId, start, System.currentTimeMillis(), GriffinStatus.WARNING, error);
    }

    public static BatchStatus ok(String batchId, long start) {
        return new BatchStatus(batchId, start, System.currentTimeMillis(), GriffinStatus.OK, "");
    }

    public static BatchStatus error(String batchId, long start, Throwable error) {
        return new BatchStatus(batchId, start, System.currentTimeMillis(), GriffinStatus.KO, error.getMessage());
    }

    @Override
    public String toString() {
        return "BatchStatus{" +
                "BatchId='" + batchId + '\'' +
                ", StartTime=" + startTime +
                ", EndTime=" + endTime +
                ", Delta=" + (endTime - startTime)+
                ", Status=" + status +
                ", Error='" + error + '\'' +
                '}';
    }
}
