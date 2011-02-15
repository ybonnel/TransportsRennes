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

public class ItineraireReponse {
	private String erreur;
	private Adresse adresseDepart;
	private Adresse adresseArrivee;
	private List<Trajet> trajets;

	public String getErreur() {
		return erreur;
	}

	public void setErreur(final String erreur) {
		this.erreur = erreur;
	}

	public Adresse getAdresseDepart() {
		return adresseDepart;
	}

	public void setAdresseDepart(final Adresse adresseDepart) {
		this.adresseDepart = adresseDepart;
	}

	public Adresse getAdresseArrivee() {
		return adresseArrivee;
	}

	public void setAdresseArrivee(final Adresse adresseArrivee) {
		this.adresseArrivee = adresseArrivee;
	}

	public Collection<Trajet> getTrajets() {
		if (trajets == null) {
			trajets = new ArrayList<Trajet>(3);
		}
		return trajets;
	}

	public String toXml() {
		final StringBuilder stringBuilder = new StringBuilder("<reponse>");
		if (erreur != null) {
			stringBuilder.append("<erreur>");
			stringBuilder.append(erreur);
			stringBuilder.append("</erreur>");
		}
		if (adresseDepart != null) {
			stringBuilder.append("<adresseDepart>");
			stringBuilder.append(adresseDepart.toXml());
			stringBuilder.append("</adresseDepart>");
		}
		if (adresseArrivee != null) {
			stringBuilder.append("<adresseArrivee>");
			stringBuilder.append(adresseArrivee.toXml());
			stringBuilder.append("</adresseArrivee>");
		}
		for (final Trajet trajet : getTrajets()) {
			stringBuilder.append("<trajet>");
			stringBuilder.append(trajet.toXml());
			stringBuilder.append("</trajet>");
		}
		stringBuilder.append("</reponse>");
		return stringBuilder.toString();
	}

}
