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

import fr.ybo.transportsrennes.keolis.modele.velos.Station;

/**
 * Handler SAX pour l'api getstation.
 *
 * @author ybonnel
 */
public class GetStationHandler extends KeolisHandler<Station> {

	@Override
	public String getDatasetid() {
		return "vls-stations-etat-tr";
	}

	@Override
	public Station fromJson(JSONObject json) throws JSONException {
		Station station = new Station();
		station.state = json.getJSONArray("etat").length() >= 1
				&& json.getJSONArray("etat").getString(0).equals("En fonctionnement");
		station.name = json.getString("nom");
		station.bikesavailable = json.getInt("nombrevelosdisponibles");
		station.slotsavailable = json.getInt("nombreemplacementsdisponibles");
		station.lastupdate = json.getString("lastupdate");
		station.latitude = json.getJSONObject("coordonnees").getDouble("lat");
		station.longitude = json.getJSONObject("coordonnees").getDouble("lon");
		station.number = json.getInt("idstation");
		return station;
	}
}
