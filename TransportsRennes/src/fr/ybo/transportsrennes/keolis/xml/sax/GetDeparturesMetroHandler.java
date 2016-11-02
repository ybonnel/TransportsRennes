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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import fr.ybo.transportsrennes.keolis.modele.bus.Departure;
import fr.ybo.transportsrennes.keolis.modele.bus.DepartureMetro;

public class GetDeparturesMetroHandler extends KeolisHandler<Departure> {

    @Override
    public String getDatasetid() {
        return "tco-metro-circulation-passages-tr";
    }

    @Override
    public Departure fromJson(JSONObject json) throws JSONException {
        Departure departure = new Departure();
        departure.setAccurate(json.getJSONArray("precision").getString(0).equals("Temps réel"));
        departure.setHeadSign(json.getString("destination"));
        try {
            departure.setTime(toCalendar(json.getString("depart")));
        } catch (ParseException e) {
            throw new JSONException(e.getMessage());
        }
        return departure;
    }
}
