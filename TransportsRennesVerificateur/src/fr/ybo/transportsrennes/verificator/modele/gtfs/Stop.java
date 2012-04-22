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
package fr.ybo.transportsrennes.verificator.modele.gtfs;

import fr.ybo.moteurcsv.adapter.AdapterDouble;
import fr.ybo.moteurcsv.annotation.BaliseCsv;
import fr.ybo.moteurcsv.annotation.FichierCsv;

/**
 * Un stop GTFS.
 * @author ybonnel
 *
 */
@FichierCsv
public class Stop {
	// CHECKSTYLE:OFF
	@BaliseCsv(value = "stop_id", ordre = 0, obligatoire = true)
	private String id;
	@BaliseCsv(value = "stop_code", ordre = 1, obligatoire = true)
	private String code;
	@BaliseCsv(value = "stop_name", ordre = 2, obligatoire = true)
	private String nom;
	@BaliseCsv(value = "stop_desc", ordre = 3)
	private String desc;
	@BaliseCsv(value = "stop_lat", adapter = AdapterDouble.class, ordre = 4, obligatoire = true)
	private double latitude;
	@BaliseCsv(value = "stop_lon", adapter = AdapterDouble.class, ordre = 5, obligatoire = true)
	private double longitude;
	@BaliseCsv(value = "zone_id", ordre = 6)
	private String zoneId;
	@BaliseCsv(value = "stop_url", ordre = 7)
	private String url;
	@BaliseCsv(value = "location_type", ordre = 8)
	private String locationType;
	@BaliseCsv(value = "parent_station", ordre = 9)
	private String parentStation;

	public String getId() {
		return id;
	}

	public String getCode() {
		return code;
	}

	public String getNom() {
		return nom;
	}

	public String getDesc() {
		return desc;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public String getZoneId() {
		return zoneId;
	}

	public String getUrl() {
		return url;
	}

	public String getLocationType() {
		return locationType;
	}

	public String getParentStation() {
		return parentStation;
	}

	
}
