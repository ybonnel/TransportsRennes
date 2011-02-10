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

public class ItineraireReponse {
	private String erreur;
	private List<Adresse> adresses1;
	private List<Adresse> adresses2;
	private List<Trajet> trajets;

	public String getErreur() {
		return erreur;
	}

	public void setErreur(String erreur) {
		this.erreur = erreur;
	}

	public List<Adresse> getAdresses1() {
		if (adresses1 == null) {
			adresses1 = new ArrayList<Adresse>();
		}
		return adresses1;
	}

	public List<Adresse> getAdresses2() {
		if (adresses2 == null) {
			adresses2 = new ArrayList<Adresse>();
		}
		return adresses2;
	}

	public List<Trajet> getTrajets() {
		if (trajets == null) {
			trajets = new ArrayList<Trajet>();
		}
		return trajets;
	}

}
