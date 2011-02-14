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

public class JointurePieton extends PortionTrajetPieton {

	private final Arret arret;
	private final Adresse adresse;

	public JointurePieton(Arret arret, Adresse adresse, Double distance) {
		this.arret = arret;
		this.adresse = adresse;
		this.distance = distance;
	}

	public Arret getArret() {
		return arret;
	}

	public String toXml() {
		StringBuilder stringBuilder = new StringBuilder();
		if (tempsTrajet != null) {
			stringBuilder.append("<tempsTrajet>");
			stringBuilder.append(tempsTrajet);
			stringBuilder.append("</tempsTrajet>");
		}
		stringBuilder.append("<arretId>");
		stringBuilder.append(arret.id);
		stringBuilder.append("</arretId>");
		stringBuilder.append("<adresse>");
		stringBuilder.append(adresse.toXml());
		stringBuilder.append("</adresse>");
		return stringBuilder.toString();
	}
}
