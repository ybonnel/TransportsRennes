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
import fr.ybo.transportsbordeauxhelper.gtfs.GestionnaireGtfs;

/**
 * Un Trip GTFS.
 * @author ybonnel
 *
 */
@FichierCsv("trips.txt")
public class Trip {
	// CHECKSTYLE:OFF
	@BaliseCsv(value = "route_id", ordre = 0)
	public String routeId;
	@BaliseCsv(value = "service_id", ordre = 1)
	public String serviceId;
	@BaliseCsv(value = "trip_id", ordre = 2)
	public String id;
	@BaliseCsv(value = "trip_headsign", ordre = 3)
	public String headSign;
	@BaliseCsv(value = "block_id", ordre = 4)
	public String blockId;

	public Calendar getCalendar() {
		return GestionnaireGtfs.getInstance().getCalendars().get(serviceId);
	}

	public Route getRoute() {
		return GestionnaireGtfs.getInstance().getRoutes().get(routeId);
	}

	@Override
	public String toString() {
		return "Trip{" + "id='" + id + '\'' + ", serviceId='" + serviceId + '\'' + ", routeId='" + routeId + '\'' + ", headSign='" + headSign + '\'' +
				'}';
	}
}
