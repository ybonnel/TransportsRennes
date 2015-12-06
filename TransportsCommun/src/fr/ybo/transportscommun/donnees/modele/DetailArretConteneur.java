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
	private Integer secondes;
    private final int trajetId;
    private final int sequence;
	private final String direction;

	public DetailArretConteneur(final int horaire, final int trajetId, final int sequence, final String direction) {
        super();
        this.horaire = horaire;
        this.trajetId = trajetId;
        this.sequence = sequence;
		this.direction = direction;
    }

    public int getHoraire() {
        return horaire;
    }

	public void setHoraire(final int horaire) {
		this.horaire = horaire;
	}

	private boolean accurate;

	public boolean isAccurate() {
		return accurate;
	}

	public void setAccurate(final boolean accurate) {
		this.accurate = accurate;
	}

	public Integer getSecondes() {
		return secondes;
	}

	public void setSecondes(final Integer secondes) {
		this.secondes = secondes;
	}

	public int getTrajetId() {
        return trajetId;
    }

    public int getSequence() {
        return sequence;
    }

	public CharSequence getDirection() {
		return direction;
	}
}
