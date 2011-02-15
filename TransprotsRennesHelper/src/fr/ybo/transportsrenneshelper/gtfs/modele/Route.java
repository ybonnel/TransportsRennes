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

@SuppressWarnings("UnusedDeclaration")
@FichierCsv("routes.txt")
public class Route {

	@BaliseCsv("route_id")
	public String id;
	@BaliseCsv("route_short_name")
	public String nomCourt;
	@BaliseCsv("route_long_name")
	public String nomLong;

	public String nomCourtFormatte;

	@Override
	public String toString() {
		return "Route{" + "nomLong='" + nomLong + '\'' + ", id='" + id + '\'' + ", nomCourt='" + nomCourt + '\'' + '}';
	}
}
