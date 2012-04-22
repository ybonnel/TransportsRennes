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

import fr.ybo.moteurcsv.adapter.AdapterInteger;
import fr.ybo.moteurcsv.annotation.BaliseCsv;
import fr.ybo.moteurcsv.annotation.FichierCsv;

/**
 * Un Trip GTFS.
 * @author ybonnel
 *
 */
@FichierCsv
public class Trip {
	// CHECKSTYLE:OFF
	@BaliseCsv(value = "trip_id", ordre = 0, obligatoire = true)
	private String id;
	@BaliseCsv(value = "service_id", ordre = 1, obligatoire = true)
	private String serviceId;
	@BaliseCsv(value = "route_id", ordre = 2, obligatoire = true)
	private String routeId;
	@BaliseCsv(value = "trip_headsign", ordre = 3, obligatoire = true)
	private String headSign;
	@BaliseCsv(value = "direction_id", adapter = AdapterInteger.class, ordre = 4, obligatoire = true)
	private Integer directionId;
	@BaliseCsv(value = "block_id", ordre = 5)
	private String blockId;

	public String getId() {
		return id;
	}

	public String getServiceId() {
		return serviceId;
	}

	public String getRouteId() {
		return routeId;
	}

	public String getHeadSign() {
		return headSign;
	}

	public Integer getDirectionId() {
		return directionId;
	}

	public String getBlockId() {
		return blockId;
	}

	
}
