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
 * 
 * Contributors:
 *     ybonnel - initial API and implementation
 */
package fr.ybo.transportsrennes.keolis.xml.sax;

import org.json.JSONException;
import org.json.JSONObject;

import fr.ybo.transportsrennes.keolis.modele.bus.PointDeVente;

/**
 * Handler permettant de rÃ©cupÃ©rer les points de vente.
 *
 * @author ybonnel
 */
public class GetPointDeVenteHandler extends KeolisHandler<PointDeVente> {


	@Override
	public String getDatasetid() {
		return "mkt-titres-pointsvente-partenaires-td";
	}

	/*
	"nom": "Le Nerval",
          "nomcommune": "Rennes",
          "horairesjeudi": "07:30-20:00",
          "horairesmercredi": "07:30-20:00",
          "horairesvendredi": "07:30-20:00",
          "adressenumero": "123",
          "coordonnees": {
            "lat": 48.119807,
            "lon": -1.680941
          },
          "horairesdimanche": "Fermé",
          "adressevoie": "Rue de Dinan",
          "horairessamedi": "08:00-20:00",
          "horairesmardi": "07:30-20:00",
          "horaireslundi": "07:30-20:00",
          "type": [
            "Dépositaire"
          ],
          "id": "1013",
          "codeinseecommune": "35238"
	 */

	@Override
	public PointDeVente fromJson(JSONObject json) throws JSONException {
		PointDeVente pointDeVente = new PointDeVente();
		pointDeVente.adresse = (json.getString("adressenumero") == null ? "" : (json.getString("adressenumero") + " ")) + json.getString("adressevoie");
		pointDeVente.latitude = json.getJSONObject("coordonnees").getDouble("lat");
		pointDeVente.longitude = json.getJSONObject("coordonnees").getDouble("lon");
		pointDeVente.name = json.getString("nom");
		pointDeVente.schedule = "Lundi : " + json.getString("horaireslundi") + "\n" +
				"Mardi : " + json.getString("horairesmardi") + "\n" +
				"Mercredi : " + json.getString("horairesmercredi") + "\n" +
				"Jeudi : " + json.getString("horairesjeudi") + "\n" +
				"Vendredi : " + json.getString("horairesvendredi") + "\n" +
				"Samedi : " + json.getString("horairessamedi") + "\n" +
				"Dimanche : " + json.getString("horairesdimanche");
		pointDeVente.type = json.getJSONArray("type").getString(0);
		pointDeVente.ville = json.getString("nomcommune");
		return pointDeVente;
	}
}
