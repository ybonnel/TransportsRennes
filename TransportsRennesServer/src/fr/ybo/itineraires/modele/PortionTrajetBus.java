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
import fr.ybo.gtfs.modele.Ligne;

public class PortionTrajetBus extends PortionTrajet {

	private Arret arretDepart;
	private Arret arretArrivee;
	private Ligne ligne;

	public PortionTrajetBus(Arret arretDepart, Arret arretArrivee, Ligne ligne) {
		this.arretDepart = arretDepart;
		this.arretArrivee = arretArrivee;
		this.ligne = ligne;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Postion en bus :\n");
		stringBuilder.append("\tLigne :");
		stringBuilder.append(ligne.id);
		stringBuilder.append("\n\tDépart : ");
		stringBuilder.append(arretDepart.nom);
		stringBuilder.append("\n\tArrivee : ");
		stringBuilder.append(arretArrivee.nom);
		return stringBuilder.toString();
	}
}
