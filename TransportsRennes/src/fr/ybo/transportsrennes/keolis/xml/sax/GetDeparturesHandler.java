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

import java.text.ParseException;

import fr.ybo.transportsrennes.keolis.modele.bus.Departure;

public class GetDeparturesHandler extends KeolisHandler<Departure> {

	@Override
	public String getDatasetid() {
		return "tco-bus-circulation-passages-tr";
	}

	@Override
	public Departure fromJson(JSONObject json) throws JSONException {
		Departure departure = new Departure();
		try {
			departure.setTime(toCalendar(json.getString("depart")));
		} catch (ParseException e) {
			throw new JSONException(e.getMessage());
		}
		departure.setHeadSign(json.getString("destination"));
		departure.setAccurate(json.getString("precision").equals("Temps réel"));

		return departure;
	}
}
