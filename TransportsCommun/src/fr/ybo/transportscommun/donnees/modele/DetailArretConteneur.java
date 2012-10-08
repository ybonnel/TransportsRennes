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

package fr.ybo.transportscommun.donnees.modele;

public class DetailArretConteneur {

    private int horaire;
	private Integer secondes = null;
    private int trajetId;
    private int sequence;
	private String direction;

	public DetailArretConteneur(int horaire, int trajetId, int sequence, String direction) {
        super();
        this.horaire = horaire;
        this.trajetId = trajetId;
        this.sequence = sequence;
		this.direction = direction;
    }

    public int getHoraire() {
        return horaire;
    }

	public void setHoraire(int horaire) {
		this.horaire = horaire;
	}

	private boolean accurate = false;

	public boolean isAccurate() {
		return accurate;
	}

	public void setAccurate(boolean accurate) {
		this.accurate = accurate;
	}

	public Integer getSecondes() {
		return secondes;
	}

	public void setSecondes(Integer secondes) {
		this.secondes = secondes;
	}

	public int getTrajetId() {
        return trajetId;
    }

    public int getSequence() {
        return sequence;
    }

	public String getDirection() {
		return direction;
	}
}
