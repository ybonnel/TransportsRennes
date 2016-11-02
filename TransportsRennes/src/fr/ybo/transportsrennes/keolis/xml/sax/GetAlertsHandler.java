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

import fr.ybo.transportsrennes.keolis.modele.bus.Alert;

/**
 * Handler SAX pour la réponse du getdistrict.
 *
 * @author ybonnel
 */
public class GetAlertsHandler extends KeolisHandler<Alert> {


	@Override
	public String getDatasetid() {
		return "tco-busmetro-trafic-alertes-tr";
	}

	@Override
	public Alert fromJson(JSONObject json) throws JSONException {
		Alert alert = new Alert();
		alert.detail = json.getString("description");
		alert.starttime = json.getString("debutvalidite");
		alert.line = json.getString("nomcourtligne");
		alert.endtime = json.getString("finvalidite");
		alert.title = json.getString("titre");
		alert.link = json.getString("url");
		return alert;
	}
}
