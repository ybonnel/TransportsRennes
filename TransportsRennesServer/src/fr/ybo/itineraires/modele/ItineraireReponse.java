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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

public class ItineraireReponse {
	private String erreur;
	private Adresse adresse1;
	private Adresse adresse2;
	private List<Trajet> trajets;

	public String getErreur() {
		return erreur;
	}

	public void setErreur(String erreur) {
		this.erreur = erreur;
	}

	public Adresse getAdresse1() {
		return adresse1;
	}

	public void setAdresse1(Adresse adresse1) {
		this.adresse1 = adresse1;
	}

	public Adresse getAdresse2() {
		return adresse2;
	}

	public void setAdresse2(Adresse adresse2) {
		this.adresse2 = adresse2;
	}

	public List<Trajet> getTrajets() {
		if (trajets == null) {
			trajets = new ArrayList<Trajet>();
		}
		return trajets;
	}

	public String toXml() {
		StringBuilder stringBuilder = new StringBuilder("<reponse>");
		if (erreur != null) {
			stringBuilder.append("<erreur>");
			stringBuilder.append(erreur);
			stringBuilder.append("</erreur>");
		}
		if (adresse1 != null) {
			stringBuilder.append("<adresse1>");
			stringBuilder.append(adresse1.toXml());
			stringBuilder.append("</adresse1>");
		}
		if (adresse2 != null) {
			stringBuilder.append("<adresse2>");
			stringBuilder.append(adresse2.toXml());
			stringBuilder.append("</adresse2>");
		}
		for (Trajet trajet : getTrajets()) {
			stringBuilder.append("<trajet>");
			stringBuilder.append(trajet.toXml());
			stringBuilder.append("</trajet>");
		}
		stringBuilder.append("</reponse>");
		return stringBuilder.toString();
	}

}
