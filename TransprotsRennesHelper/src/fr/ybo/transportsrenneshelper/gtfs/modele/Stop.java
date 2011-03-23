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

package fr.ybo.transportsrenneshelper.gtfs.modele;

import fr.ybo.moteurcsv.adapter.AdapterDouble;
import fr.ybo.moteurcsv.annotation.BaliseCsv;
import fr.ybo.moteurcsv.annotation.FichierCsv;

@FichierCsv("stops.txt")
public class Stop {

	@BaliseCsv(value = "stop_id", ordre = 0)
	public String id;
	@BaliseCsv(value = "stop_code", ordre = 1)
	public String code;
	@BaliseCsv(value = "stop_name", ordre = 2)
	public String nom;
	@BaliseCsv(value = "stop_desc", ordre = 3)
	public String desc;
	@BaliseCsv(value = "stop_lat", adapter = AdapterDouble.class, ordre = 4)
	public double latitude;
	@BaliseCsv(value = "stop_lon", adapter = AdapterDouble.class, ordre = 5)
	public double longitude;
	@BaliseCsv(value = "zone_id", ordre = 6)
	public String zoneId;
	@BaliseCsv(value = "stop_url", ordre = 7)
	public String url;
	@BaliseCsv(value = "location_type", ordre = 8)
	public String locationType;
	@BaliseCsv(value = "parent_station", ordre = 9)
	public String parentStation;

	@Override
	public String toString() {
		return "Stop{" + "id='" + id + '\'' + ", nom='" + nom + '\'' + ", latitude=" + latitude + ", longitude=" + longitude + '}';
	}
}
