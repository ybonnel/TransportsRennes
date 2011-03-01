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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class Trajet {
	private List<PortionTrajet> portionsTrajet;

	public Collection<PortionTrajet> getPortionsTrajet() {
		if (portionsTrajet == null) {
			portionsTrajet = new ArrayList<PortionTrajet>(5);
		}
		return portionsTrajet;
	}

	private Integer tempsTrajet;

	public int calculTempsTrajet(int heureDepart) {
		if (tempsTrajet == null) {
			int heureCourante = heureDepart;
			for (PortionTrajet portionTrajet : getPortionsTrajet()) {
				heureCourante = portionTrajet.calculHeureArrivee(heureCourante);
			}
			tempsTrajet = heureCourante - heureDepart;
		}
		return tempsTrajet;
	}


	private Integer tempsTrajetPieton = null;

	public int calculTempsTrajetPieton() {
		if (tempsTrajetPieton == null) {
			tempsTrajetPieton = 0;
			for (PortionTrajet portionTrajet : getPortionsTrajet()) {
				if (portionTrajet instanceof PortionTrajetPieton) {
					tempsTrajetPieton += ((PortionTrajetPieton) portionTrajet).tempsTrajet;
				}
			}
		}
		return tempsTrajetPieton;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder("Trajet (");
		stringBuilder.append(tempsTrajet);
		stringBuilder.append(" minutes):\n");
		for (PortionTrajet portion : getPortionsTrajet()) {
			stringBuilder.append(portion.toString());
			stringBuilder.append('\n');
		}
		return stringBuilder.toString();
	}

	public String toXml() {
		StringBuilder stringBuilder = new StringBuilder();
		if (tempsTrajet != null) {
			stringBuilder.append("<tempsTrajet>");
			stringBuilder.append(tempsTrajet);
			stringBuilder.append("</tempsTrajet>");
		}
		for (PortionTrajet portion : getPortionsTrajet()) {
			stringBuilder.append("<portion type=\"");
			stringBuilder.append(portion.getClass().getSimpleName());
			stringBuilder.append("\">");
			stringBuilder.append(portion.toXml());
			stringBuilder.append("</portion>");
		}
		return stringBuilder.toString();
	}
}
