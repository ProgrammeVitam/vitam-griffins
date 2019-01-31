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

package fr.gouv.vitam.griffins.siegfried.pojo;

public class RawOutput {
    private final Input input;

    private final String error;
    private final boolean successful;
    public final Action action;
    private final SiegfriedResponse siegFriedResponse;
    private final String executed;

    public RawOutput(String error, Input input, Action action, String executed) {
        this.error = error;
        this.executed = executed;
        this.successful = false;
        this.input = input;
        this.action = action;
        this.siegFriedResponse = null;
    }

    public RawOutput(SiegfriedResponse siegFriedResponse, Input input, Action action, String executed) {
        this.executed = executed;
        this.error = null;
        this.siegFriedResponse = siegFriedResponse;
        this.input = input;
        this.action = action;
        this.successful = true;
    }

    public Output toOutput(boolean debug) {
        if(successful) {
            return debug
                ? Output.ok(input, action.getType(), executed, siegFriedResponse.toFormatIdentification(input.getName()))
                : Output.ok(input, action.getType(), siegFriedResponse.toFormatIdentification(input.getName()));
        }

        return debug
            ? Output.error(input, action.getType(), error, executed)
            : Output.error(input, action.getType());
    }
}
