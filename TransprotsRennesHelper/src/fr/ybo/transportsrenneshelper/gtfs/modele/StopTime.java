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

import fr.ybo.transportsrenneshelper.annotation.BaliseCsv;
import fr.ybo.transportsrenneshelper.annotation.FichierCsv;
import fr.ybo.transportsrenneshelper.gtfs.gestionnaire.GestionnaireGtfs;
import fr.ybo.transportsrenneshelper.moteurcsv.adapter.AdapterInteger;
import fr.ybo.transportsrenneshelper.moteurcsv.adapter.AdapterTime;

@SuppressWarnings("UnusedDeclaration")
@FichierCsv("stop_times.txt")
public class StopTime {
	@BaliseCsv("trip_id")
	public String tripId;
	@BaliseCsv("stop_id")
	public String stopId;
	@BaliseCsv(value = "departure_time", adapter = AdapterTime.class)
	public int heureDepart;
	@BaliseCsv(value = "stop_sequence", adapter = AdapterInteger.class)
	public int stopSequence;

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
}
