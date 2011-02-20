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
import fr.ybo.itineraires.schema.PortionTrajet;

public class JointureCorrespondance extends PortionTrajetPieton {
	private final Arret arretDepart;
	private final Arret arretArrivee;

	public JointureCorrespondance(Arret arretDepart, Arret arretArrivee, double distance) {
		this.arretDepart = arretDepart;
		this.arretArrivee = arretArrivee;
		this.distance = distance;
	}

	@Override
	public String toXml() {
		StringBuilder stringBuilder = new StringBuilder();
		if (tempsTrajet != null) {
			stringBuilder.append("<tempsTrajet>");
			stringBuilder.append(tempsTrajet);
			stringBuilder.append("</tempsTrajet>");
		}
		stringBuilder.append("<arretDepartId>");
		stringBuilder.append(arretDepart.id);
		stringBuilder.append("</arretDepartId>");
		stringBuilder.append("<arretArriveeId>");
		stringBuilder.append(arretArrivee.id);
		stringBuilder.append("</arretArriveeId>");
		return stringBuilder.toString();
	}

    @Override
	public PortionTrajet convert() {
		fr.ybo.itineraires.schema.JointureCorrespondance retour = new fr.ybo.itineraires.schema.JointureCorrespondance();
		remplirXml(retour);
		retour.setArretDepartId(arretDepart.id);
		retour.setArretArriveeId(arretArrivee.id);
        PortionTrajet portionTrajet = new PortionTrajet();
        portionTrajet.setJointureCorrespondance(retour);
		return portionTrajet;
	}
}
