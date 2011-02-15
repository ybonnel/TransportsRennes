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

package fr.ybo.itineraires.bean;


import fr.ybo.itineraires.modele.Adresse;
import fr.ybo.itineraires.modele.EnumCalendrier;
import fr.ybo.itineraires.schema.ItineraireReponse;
import org.restlet.data.Form;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class ItineraireServerResource extends ServerResource implements ItineraireResource {

	@Get
	public ItineraireReponse calculItineraire() {
		Form form = getRequest().getResourceRef().getQueryAsForm();
		String key = form.getValues("key");
		Adresse adresseDepart = new Adresse(form.getValues("adresseDepart"));
		Adresse adresseArrivee = new Adresse(form.getValues("adresseArrivee"));
		Integer heureDepart = form.getValues("heureDepart") == null ? null : Integer.parseInt(form.getValues("heureDepart"));
		EnumCalendrier calendrier =
				EnumCalendrier.fromNumCalendrier(form.getValues("calendrier") == null ? null : Integer.parseInt(form.getValues("calendrier")));

		return ItineraireBean.calculItineraire(key, adresseDepart, adresseArrivee, heureDepart, calendrier);
	}
}
