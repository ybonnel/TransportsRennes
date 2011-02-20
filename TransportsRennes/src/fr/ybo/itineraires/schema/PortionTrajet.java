/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package fr.ybo.itineraires.schema;

public final class PortionTrajet {

    protected JointureCorrespondance jointureCorrespondance;
    protected JointurePieton jointurePieton;
    protected PortionTrajetBus portionTrajetBus;

    public JointureCorrespondance getJointureCorrespondance() {
        return jointureCorrespondance;
    }

    public void setJointureCorrespondance(JointureCorrespondance jointureCorrespondance) {
        this.jointureCorrespondance = jointureCorrespondance;
    }

    public JointurePieton getJointurePieton() {
        return jointurePieton;
    }

    public void setJointurePieton(JointurePieton jointurePieton) {
        this.jointurePieton = jointurePieton;
    }

    public PortionTrajetBus getPortionTrajetBus() {
        return portionTrajetBus;
    }

    public void setPortionTrajetBus(PortionTrajetBus portionTrajetBus) {
        this.portionTrajetBus = portionTrajetBus;
    }
}
