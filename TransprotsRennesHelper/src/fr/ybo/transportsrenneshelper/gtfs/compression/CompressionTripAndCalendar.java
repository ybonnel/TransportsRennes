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

package fr.ybo.transportsrenneshelper.gtfs.compression;


import fr.ybo.transportsrenneshelper.gtfs.gestionnaire.GestionnaireGtfs;
import fr.ybo.transportsrenneshelper.gtfs.modele.Calendar;
import fr.ybo.transportsrenneshelper.gtfs.modele.StopTime;
import fr.ybo.transportsrenneshelper.gtfs.modele.Trip;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"UseOfSystemOutOrSystemErr"})
public class CompressionTripAndCalendar {

	private class Trajet {

		private Trajet(final List<StopTime> stopTimes) {
			super();
			this.stopTimes = stopTimes;
			key = genereKey();
		}

		private String genereKey() {
			Collections.sort(stopTimes, new Comparator<StopTime>() {
				public int compare(final StopTime o1, final StopTime o2) {
					return o1.stopSequence < o2.stopSequence ? -1 : o1.stopSequence == o2.stopSequence ? 0 : 1;
				}
			});
			final StringBuilder keyBuilder = new StringBuilder();
			for (final StopTime stopTime : stopTimes) {
				keyBuilder.append(stopTime.stopId);
				keyBuilder.append(stopTime.heureDepart);
			}
			return keyBuilder.toString();
		}

		private final String key;

		public final List<StopTime> stopTimes;

		public final List<String> tripIds = new ArrayList<String>(1000);

		public void addTripId(final String tripId, final Calendar calendar) {
			if (this.calendar == null) {
				this.calendar = new Calendar(calendar);
			} else {
				this.calendar.merge(calendar);
			}
			tripIds.add(tripId);
		}

		public Calendar calendar;

		public String getKey() {
			return key;
		}
	}

	private final Map<String, CompressionTripAndCalendar.Trajet> trajets = new HashMap<String, CompressionTripAndCalendar.Trajet>(1000);


	public void compressTripsAndCalendars() {
		for (final Trip tripActuel : GestionnaireGtfs.getInstance().getMapTrips().values()) {
			Trajet trajet = new Trajet(GestionnaireGtfs.getInstance().getStopTimesForOnTrip(tripActuel.id));
			if (!trajets.containsKey(trajet.getKey())) {
				trajets.put(trajet.getKey(), trajet);
			}
			trajets.get(trajet.getKey()).addTripId(tripActuel.id, tripActuel.getCalendar());
		}
	}

	public void replaceTripGenereCalendarAndCompressStopTimes() {
		final Collection<Trip> newTrips = new ArrayList<Trip>(1000);
		final Collection<StopTime> newStopTimes = new ArrayList<StopTime>(1000);
		int calendarId = 1;
		final Map<Calendar, String> newCalendars = new HashMap<Calendar, String>(20);
		int tripId = 1;
		for (final CompressionTripAndCalendar.Trajet trajet : trajets.values()) {
			Trip tripCourant = GestionnaireGtfs.getInstance().getMapTrips().get(trajet.tripIds.get(0));
			tripCourant.id = String.valueOf(tripId);
			tripId++;
			if (!newCalendars.containsKey(trajet.calendar)) {
				trajet.calendar.id = String.valueOf(calendarId);
				calendarId++;
				newCalendars.put(trajet.calendar, trajet.calendar.id);
			}
			tripCourant.serviceId = newCalendars.get(trajet.calendar);
			newTrips.add(tripCourant);
			for (final StopTime stopTime : trajet.stopTimes) {
				stopTime.tripId = tripCourant.id;
				newStopTimes.add(stopTime);
			}
		}
		GestionnaireGtfs.getInstance().getMapTrips().clear();
		for (final Trip trip : newTrips) {
			GestionnaireGtfs.getInstance().getMapTrips().put(trip.id, trip);
		}
		GestionnaireGtfs.getInstance().getMapStopTimes().clear();
		GestionnaireGtfs.getInstance().resetMapStopTimesByTripId();
		for (final StopTime stopTime : newStopTimes) {
			if (GestionnaireGtfs.getInstance().getMapStopTimes().containsKey(stopTime.getKey())) {
				System.err.println("StopTimes présent plusieurs fois après compression :");
				System.err.println("Ancien : " + GestionnaireGtfs.getInstance().getMapStopTimes().get(stopTime.getKey()).toString());
				System.err.println("Nouveau : " + stopTime.toString());
			}
			GestionnaireGtfs.getInstance().getMapStopTimes().put(stopTime.getKey(), stopTime);
		}
		GestionnaireGtfs.getInstance().getMapCalendars().clear();
		for (final Calendar calendar : newCalendars.keySet()) {
			GestionnaireGtfs.getInstance().getMapCalendars().put(calendar.id, calendar);
		}
	}
}
