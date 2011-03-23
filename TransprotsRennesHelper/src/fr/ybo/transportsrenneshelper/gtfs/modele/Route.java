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

import fr.ybo.moteurcsv.annotation.BaliseCsv;
import fr.ybo.moteurcsv.annotation.FichierCsv;

@FichierCsv("routes.txt")
public class Route {

	@BaliseCsv(value = "route_id", ordre = 0)
	public String id;
	@BaliseCsv(value = "agency_id", ordre = 1)
	public String agencyId;
	@BaliseCsv(value = "route_short_name", ordre = 2)
	public String nomCourt;
	@BaliseCsv(value = "route_long_name", ordre = 3)
	public String nomLong;
	@BaliseCsv(value = "route_desc", ordre = 4)
	public String description;
	@BaliseCsv(value = "route_type", ordre = 5)
	public String type;
	@BaliseCsv(value = "route_url", ordre = 6)
	public String url;
	@BaliseCsv(value = "route_color", ordre = 7)
	public String color;
	@BaliseCsv(value = "route_text_color", ordre = 8)
	public String textColor;

	public String nomCourtFormatte;

	@Override
	public String toString() {
		return "Route{" + "nomLong='" + nomLong + '\'' + ", id='" + id + '\'' + ", nomCourt='" + nomCourt + '\'' + '}';
	}
}
