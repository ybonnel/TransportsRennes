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

public class PortionTrajetBus
{
    protected String ligneId;
    protected String arretDepartId;
    protected String heureDepart;
    protected String arretArriveeId;
    protected String heureArrivee;
	protected Integer directionId;

    public String getLigneId() {
        return ligneId;
    }

    public void setLigneId(final String value) {
	    ligneId = value;
    }

    public String getArretDepartId() {
        return arretDepartId;
    }

    public void setArretDepartId(final String value) {
	    arretDepartId = value;
    }

    public String getHeureDepart() {
        return heureDepart;
    }

    public void setHeureDepart(final String value) {
	    heureDepart = value;
    }

    public String getArretArriveeId() {
        return arretArriveeId;
    }

    public void setArretArriveeId(final String value) {
	    arretArriveeId = value;
    }

    public String getHeureArrivee() {
        return heureArrivee;
    }

    public void setHeureArrivee(final String value) {
	    heureArrivee = value;
    }

	public Integer getDirectionId() {
		return directionId;
	}

	public void setDirectionId(Integer directionId) {
		this.directionId = directionId;
	}
}
