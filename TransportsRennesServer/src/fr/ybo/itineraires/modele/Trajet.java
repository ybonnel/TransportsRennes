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
import java.util.List;


public class Trajet {
	private List<PortionTrajet> portionsTrajet = null;

	public List<PortionTrajet> getPortionsTrajet() {
		if (portionsTrajet == null) {
			portionsTrajet = new ArrayList<PortionTrajet>();
		}
		return portionsTrajet;
	}

	private Integer tempsTrajet = null;

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
}
