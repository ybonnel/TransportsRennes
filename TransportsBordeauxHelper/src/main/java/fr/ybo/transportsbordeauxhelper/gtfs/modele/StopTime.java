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
	@CsvColumn(value = "arrival_time", adapter = AdapterTime.class, order = 1)
	public int heureArrivee;
	@CsvColumn(value = "departure_time", adapter = AdapterTime.class, order = 2)
	public int heureDepart;
	@CsvColumn(value = "stop_id", order = 3)
	public String stopId;
	@CsvColumn(value = "stop_sequence", adapter = AdapterInteger.class, order = 4)
	public int stopSequence;
	@CsvColumn(value = "pickup_type", order = 5)
	public String pickupType;
	@CsvColumn(value = "drop_off_type", order = 6)
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
