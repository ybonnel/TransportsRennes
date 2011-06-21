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

import fr.ybo.moteurcsv.adapter.AdapterInteger;
import fr.ybo.moteurcsv.adapter.AdapterTime;
import fr.ybo.moteurcsv.annotation.BaliseCsv;
import fr.ybo.moteurcsv.annotation.FichierCsv;

/**
 * StopTime GTFS.
 * @author ybonnel
 *
 */
@FichierCsv("stop_times.txt")
public class StopTime {
	// CHECKSTYLE:OFF
	@BaliseCsv(value = "trip_id", ordre = 0)
	public String tripId;
	@BaliseCsv(value = "arrival_time", adapter = AdapterTime.class, ordre = 1)
	public int heureArrivee;
	@BaliseCsv(value = "departure_time", adapter = AdapterTime.class, ordre = 2)
	public int heureDepart;
	@BaliseCsv(value = "stop_id", ordre = 3)
	public String stopId;
	@BaliseCsv(value = "stop_sequence", adapter = AdapterInteger.class, ordre = 4)
	public int stopSequence;
	@BaliseCsv(value = "pickup_type", ordre = 5)
	public String pickupType;
	@BaliseCsv(value = "drop_off_type", ordre = 6)
	public String dropOffType;

	public String getKey() {
		String tripId = this.tripId;
		while (tripId.length() < 8) {
			tripId = "0" + tripId;
		}
		return tripId + stopId;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StopTime other = (StopTime) obj;
		if (this.stopId == null) {
			if (other.stopId != null)
				return false;
		} else if (!this.stopId.equals(other.stopId))
			return false;
		if (this.tripId == null) {
			if (other.tripId != null)
				return false;
		} else if (!this.tripId.equals(other.tripId))
			return false;
		return true;
	}



	@Override
	public String toString() {
		return "StopTime{" + "tripId='" + tripId + '\'' + ", stopId='" + stopId + '\'' + ", heureDepart=" + heureDepart + ", stopSequence=" +
				stopSequence + '}';
	}
}
