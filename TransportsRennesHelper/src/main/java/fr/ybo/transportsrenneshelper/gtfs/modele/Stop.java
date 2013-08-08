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
package fr.ybo.transportsrenneshelper.gtfs.modele;


import fr.ybonnel.csvengine.adapter.AdapterBoolean;
import fr.ybonnel.csvengine.adapter.AdapterDouble;
import fr.ybonnel.csvengine.annotation.CsvColumn;
import fr.ybonnel.csvengine.annotation.CsvFile;

/**
 * Un stop GTFS.
 * @author ybonnel
 *
 */
@CsvFile
public class Stop {
	// CHECKSTYLE:OFF
	@CsvColumn(value = "stop_id", order = 0)
	public String id;
	@CsvColumn(value = "stop_code", order = 1)
	public String code;
	@CsvColumn(value = "stop_name", order = 2)
	public String nom;
	@CsvColumn(value = "stop_desc", order = 3)
	public String desc;
	@CsvColumn(value = "stop_lat", adapter = AdapterDouble.class, order = 4)
	public double latitude;
	@CsvColumn(value = "stop_lon", adapter = AdapterDouble.class, order = 5)
	public double longitude;
	@CsvColumn(value = "zone_id", order = 6)
	public String zoneId;
	@CsvColumn(value = "stop_url", order = 7)
	public String url;
	@CsvColumn(value = "location_type", order = 8)
	public String locationType;
	@CsvColumn(value = "parent_station", order = 9)
	public String parentStation;
	@CsvColumn(value = "wheelchair_boarding", order = 10, adapter = AdapterBoolean.class)
	public Boolean accessible;

	@Override
	public String toString() {
		return "Stop{" + "id='" + id + '\'' + ", nom='" + nom + '\'' + ", latitude=" + latitude + ", longitude=" + longitude + '}';
	}
}
