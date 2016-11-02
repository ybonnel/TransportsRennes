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

import fr.ybo.transportsrennes.keolis.modele.bus.ParkRelai;

/**
 * Handler pour rÃ©cupÃ©rer les parks relais.
 *
 * @author ybonnel
 */
public class GetParkRelaiHandler extends KeolisHandler<ParkRelai> {

	@Override
	public String getDatasetid() {
		return "tco-parcsrelais-etat-tr";
	}

	@Override
	public ParkRelai fromJson(JSONObject json) throws JSONException {
		ParkRelai parkRelai = new ParkRelai();
		parkRelai.isOpen = json.getJSONArray("etat").length() >= 1
				&& json.getJSONArray("etat").getString(0).equals("Ouvert");
		parkRelai.name = json.getString("nom");
		parkRelai.carParkAvailable = json.getInt("nombreplacesdisponibles");
		parkRelai.carParkCapacity = json.getInt("capaciteactuelle");
		parkRelai.lastupdate = json.getString("lastupdate");
		parkRelai.latitude = json.getJSONObject("coordonnees").getDouble("lat");
		parkRelai.longitude = json.getJSONObject("coordonnees").getDouble("lon");
		return parkRelai;
	}
}
