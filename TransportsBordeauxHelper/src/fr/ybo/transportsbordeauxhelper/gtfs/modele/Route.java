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
package fr.ybo.transportsbordeauxhelper.gtfs.modele;

import fr.ybo.moteurcsv.annotation.BaliseCsv;
import fr.ybo.moteurcsv.annotation.FichierCsv;

/**
 * Une route.
 * @author ybonnel
 *
 */
@FichierCsv("routes.txt")
public class Route {
	@BaliseCsv(value = "route_id", ordre = 0)
	public String id;
	@BaliseCsv(value = "route_short_name", ordre = 1)
	public String nomCourt;
	@BaliseCsv(value = "route_long_name", ordre = 2)
	public String nomLong;
	@BaliseCsv(value = "route_desc", ordre = 3)
	public String description;
	@BaliseCsv(value = "route_type", ordre = 4)
	public String type;

	@Override
	public String toString() {
		return "Route{" + "nomLong='" + nomLong + '\'' + ", id='" + id + '\'' + ", nomCourt='" + nomCourt + '\'' + '}';
	}
}
