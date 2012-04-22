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
import fr.ybo.moteurcsv.adapter.AdapterTime;
import fr.ybo.moteurcsv.annotation.BaliseCsv;
import fr.ybo.moteurcsv.annotation.FichierCsv;

/**
 * StopTime GTFS.
 * @author ybonnel
 *
 */
@FichierCsv
public class StopTime {
	// CHECKSTYLE:OFF
	@BaliseCsv(value = "trip_id", ordre = 0, obligatoire = true)
	private String tripId;
	@BaliseCsv(value = "stop_id", ordre = 1, obligatoire = true)
	private String stopId;
	@BaliseCsv(value = "stop_sequence", adapter = AdapterInteger.class, ordre = 2, obligatoire = true)
	private int stopSequence;
	@BaliseCsv(value = "arrival_time", adapter = AdapterTime.class, ordre = 3, obligatoire = true)
	private int heureArrivee;
	@BaliseCsv(value = "departure_time", adapter = AdapterTime.class, ordre = 4, obligatoire = true)
	private int heureDepart;
	@BaliseCsv(value = "stop_headsign", ordre = 5, obligatoire = true)
	private String headSign;
	@BaliseCsv(value = "pickup_type", ordre = 6)
	private String pickupType;
	@BaliseCsv(value = "drop_off_type", ordre = 7)
	private String dropOffType;
	@BaliseCsv(value = "shape_dist_traveled", ordre = 8)
	private String shapDistTraveled;

	public String getKey() {
		return tripId + stopId;
	}

	public String getTripId() {
		return tripId;
	}

	public String getStopId() {
		return stopId;
	}

	public int getStopSequence() {
		return stopSequence;
	}

	public int getHeureArrivee() {
		return heureArrivee;
	}

	public int getHeureDepart() {
		return heureDepart;
	}

	public String getHeadSign() {
		return headSign;
	}

	public String getPickupType() {
		return pickupType;
	}

	public String getDropOffType() {
		return dropOffType;
	}

	public String getShapDistTraveled() {
		return shapDistTraveled;
	}

	
}
