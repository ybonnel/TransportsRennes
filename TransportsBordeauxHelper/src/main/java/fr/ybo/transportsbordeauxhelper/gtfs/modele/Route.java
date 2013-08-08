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


import fr.ybonnel.csvengine.annotation.CsvColumn;
import fr.ybonnel.csvengine.annotation.CsvFile;

/**
 * Une route.
 * @author ybonnel
 *
 */
@CsvFile
public class Route {
	@CsvColumn(value = "route_id", order = 0)
	public String id;
	@CsvColumn(value = "route_short_name", order = 1)
	public String nomCourt;
	@CsvColumn(value = "route_long_name", order = 2)
	public String nomLong;
	@CsvColumn(value = "route_desc", order = 3)
	public String description;
	@CsvColumn(value = "route_type", order = 4)
	public String type;

	@Override
	public String toString() {
		return "Route{" + "nomLong='" + nomLong + '\'' + ", id='" + id + '\'' + ", nomCourt='" + nomCourt + '\'' + '}';
	}
}
