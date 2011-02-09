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

package fr.ybo.itineraires.modele;

import fr.ybo.gtfs.modele.Arret;

public class JointureCorrespondance extends PortionTrajetPieton {
	private Arret arretDepart;
	private Arret arretArrivee;
	private double distance;

	public JointureCorrespondance(Arret arretDepart, Arret arretArrivee, double distance) {
		this.arretDepart = arretDepart;
		this.arretArrivee = arretArrivee;
		this.distance = distance;
	}

	public Arret getArretDepart() {
		return arretDepart;
	}

	public Arret getArretArrivee() {
		return arretArrivee;
	}

	public double getDistance() {
		return distance;
	}



	@Override
	public String toString() {
		return "Correspondance à pied";
	}
}
