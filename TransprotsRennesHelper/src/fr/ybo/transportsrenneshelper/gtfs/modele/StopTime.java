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

import fr.ybo.moteurcsv.adapter.AdapterInteger;
import fr.ybo.moteurcsv.adapter.AdapterTime;
import fr.ybo.moteurcsv.annotation.BaliseCsv;
import fr.ybo.moteurcsv.annotation.FichierCsv;
import fr.ybo.transportsrenneshelper.gtfs.gestionnaire.GestionnaireGtfs;

@FichierCsv("stop_times.txt")
public class StopTime {
	@BaliseCsv(value = "trip_id", ordre = 0)
	public String tripId;
	@BaliseCsv(value = "stop_id", ordre = 1)
	public String stopId;
	@BaliseCsv(value = "stop_sequence", adapter = AdapterInteger.class, ordre = 2)
	public int stopSequence;
	@BaliseCsv(value = "arrival_time", adapter = AdapterTime.class, ordre = 3)
	public int heureArrivee;
	@BaliseCsv(value = "departure_time", adapter = AdapterTime.class, ordre = 4)
	public int heureDepart;
	@BaliseCsv(value = "stop_headsign", ordre = 5)
	public String headSign;
	@BaliseCsv(value = "pickup_type", ordre = 6)
	public String pickupType;
	@BaliseCsv(value = "drop_off_type", ordre = 7)
	public String dropOffType;
	@BaliseCsv(value = "shape_dist_traveled", ordre = 8)
	public String shapDistTraveled;

	public String getKey() {
		return tripId + stopId;
	}

	public Trip getTrip() {
		return GestionnaireGtfs.getInstance().getMapTrips().get(tripId);
	}

	public Stop getStop() {
		return GestionnaireGtfs.getInstance().getMapStops().get(stopId);
	}

	@Override
	public String toString() {
		return "StopTime{" + "tripId='" + tripId + '\'' + ", stopId='" + stopId + '\'' + ", heureDepart=" + heureDepart + ", stopSequence=" +
				stopSequence + '}';
	}

	public Trip trip;
}
