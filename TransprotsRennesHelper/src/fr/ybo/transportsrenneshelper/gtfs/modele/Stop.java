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

import fr.ybo.transportsrenneshelper.annotation.BaliseCsv;
import fr.ybo.transportsrenneshelper.annotation.FichierCsv;
import fr.ybo.transportsrenneshelper.moteurcsv.adapter.AdapterDouble;

@FichierCsv("stops.txt")
public class Stop {

	@BaliseCsv("stop_id")
	public String id;
	@BaliseCsv("stop_name")
	public String nom;
	@BaliseCsv(value = "stop_lat", adapter = AdapterDouble.class)
	public double latitude;
	@BaliseCsv(value = "stop_lon", adapter = AdapterDouble.class)
	public double longitude;

	@Override
	public String toString() {
		return "Stop{" + "id='" + id + '\'' + ", nom='" + nom + '\'' + ", latitude=" + latitude + ", longitude=" + longitude + '}';
	}
}
