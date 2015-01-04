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

import fr.ybo.transportsrenneshelper.gtfs.gestionnaire.GestionnaireGtfs;
import fr.ybonnel.csvengine.adapter.AdapterBoolean;
import fr.ybonnel.csvengine.adapter.AdapterInteger;
import fr.ybonnel.csvengine.annotation.CsvColumn;
import fr.ybonnel.csvengine.annotation.CsvFile;

/**
 * Un Trip GTFS.
 * @author ybonnel
 *
 */
@CsvFile
public class Trip {
	// CHECKSTYLE:OFF
	@CsvColumn(value = "trip_id", order = 0)
	public String id;
	@CsvColumn(value = "service_id", order = 1)
	public String serviceId;
	@CsvColumn(value = "route_id", order = 2)
	public String routeId;
	@CsvColumn(value = "trip_headsign", order = 3)
	public String headSign;
	@CsvColumn(value = "direction_id", adapter = AdapterInteger.class, order = 4)
	public Integer directionId;
	@CsvColumn(value = "block_id", order = 5)
	public String blockId;

	@CsvColumn(value = "wheelchair_accessible", order = 6, adapter = AdapterBoolean.class)
	public boolean accessible;

	public Calendar getCalendar() {
		return GestionnaireGtfs.getInstance().getMapCalendars().get(serviceId);
	}

	public Route getRoute() {
		return GestionnaireGtfs.getInstance().getMapRoutes().get(routeId);
	}

	@Override
	public String toString() {
		return "Trip{" + "id='" + id + '\'' + ", serviceId='" + serviceId + '\'' + ", routeId='" + routeId + '\'' + ", headSign='" + headSign + '\'' +
				'}';
	}
}
