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
import fr.ybonnel.csvengine.adapter.AdapterInteger;
import fr.ybonnel.csvengine.adapter.AdapterTime;
import fr.ybonnel.csvengine.annotation.CsvColumn;
import fr.ybonnel.csvengine.annotation.CsvFile;

/**
 * StopTime GTFS.
 * @author ybonnel
 *
 */
@CsvFile
public class StopTime {
	// CHECKSTYLE:OFF
	@CsvColumn(value = "trip_id", order = 0)
	public String tripId;
	@CsvColumn(value = "stop_id", order = 1)
	public String stopId;
	@CsvColumn(value = "stop_sequence", adapter = AdapterInteger.class, order = 2)
	public int stopSequence;
	@CsvColumn(value = "arrival_time", adapter = AdapterTime.class, order = 3)
	public int heureArrivee;
	@CsvColumn(value = "departure_time", adapter = AdapterTime.class, order = 4)
	public int heureDepart;
	@CsvColumn(value = "stop_headsign", order = 5)
	public String headSign;
	@CsvColumn(value = "pickup_type", order = 6)
	public String pickupType;
	@CsvColumn(value = "drop_off_type", order = 7)
	public String dropOffType;
	@CsvColumn(value = "shape_dist_traveled", order = 8)
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
