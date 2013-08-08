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
package fr.ybo.transportsbordeauxhelper;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.ybo.transportsbordeauxhelper.gtfs.GestionnaireGtfs;
import fr.ybo.transportsbordeauxhelper.gtfs.modele.Calendar;
import fr.ybo.transportsbordeauxhelper.gtfs.modele.StopTime;
import fr.ybo.transportsbordeauxhelper.gtfs.modele.Trip;

/**
 * Compression des trips et calendar (et horaires du coup).
 * @author ybonnel
 *
 */
public class CompressionTripAndCalendar {

	/**
	 * Représente un trajet, avec tous ses horaires associés.
	 * @author ybonnel
	 *
	 */
	private static final class Trajet {

		/**
		 * Constructeur.
		 * @param stopTimes listes des horaires.
		 */
		private Trajet(List<StopTime> stopTimes) {
			this.stopTimes = stopTimes;
			key = genereKey();
		}

		/**
		 * Génération de la clé.
		 * La clé et la suite des stopId avec leurs horaires.
		 * @return la clé.
		 */
		private String genereKey() {
			Collections.sort(stopTimes, new Comparator<StopTime>() {
				public int compare(StopTime o1, StopTime o2) {
					return o1.stopSequence < o2.stopSequence ? -1 : o1.stopSequence == o2.stopSequence ? 0 : 1;
				}
			});
			StringBuilder keyBuilder = new StringBuilder();
			for (StopTime stopTime : stopTimes) {
				keyBuilder.append(stopTime.stopId);
				keyBuilder.append(stopTime.heureDepart);
			}
			return keyBuilder.toString();
		}

		/**
		 * La clé.
		 */
		private String key;

		/**
		 * Liste des horaires.
		 */
		private List<StopTime> stopTimes;

		/**
		 * Identifiants des trips.
		 */
		private List<String> tripIds = new ArrayList<String>();

		/**
		 * Ajout d'un trip.
		 * @param tripId tripId.
		 * @param calendar calendar du trip.
		 */
		public void addTripId(String tripId, Calendar calendar) {
			if (this.calendar == null) {
				this.calendar = new Calendar(calendar);
			} else {
				this.calendar.merge(calendar);
			}
			tripIds.add(tripId);
		}

		/**
		 * Calendar (mergé).
		 */
		private Calendar calendar;

		/**
		 * 
		 * @return la clé.
		 */
		public String getKey() {
			return key;
		}
	}

	/**
	 * Map des trajets (par leurs clé).
	 */
	private final Map<String, CompressionTripAndCalendar.Trajet> trajets =
		new HashMap<String, CompressionTripAndCalendar.Trajet>();


	/**
	 * Compression.
	 */
	public void compressTripsAndCalendars() {
		for (Trip tripActuel : GestionnaireGtfs.getInstance().getTrips().values()) {
			CompressionTripAndCalendar.Trajet trajet = new CompressionTripAndCalendar.Trajet(GestionnaireGtfs
					.getInstance().getStopTimesForOnTrip(tripActuel.id));
			if (!trajets.containsKey(trajet.getKey())) {
				trajets.put(trajet.getKey(), trajet);
			}
			trajets.get(trajet.getKey()).addTripId(tripActuel.id, tripActuel.getCalendar());
		}
	}

	/**
	 * Remplace les anciennes données par les données compressées.
	 */
	public void replaceTripGenereCalendarAndCompressStopTimes() {
		Collection<Trip> newTrips = new ArrayList<Trip>();
		Collection<StopTime> newStopTimes = new ArrayList<StopTime>();
		int calendarId = 1;
		Map<Calendar, String> newCalendars = new HashMap<Calendar, String>();
		int tripId = 1;
		for (CompressionTripAndCalendar.Trajet trajet : trajets.values()) {
			Trip tripCourant = GestionnaireGtfs.getInstance().getTrips().get(trajet.tripIds.get(0));
			tripCourant.id = String.valueOf(tripId);
			tripId++;
			if (!newCalendars.containsKey(trajet.calendar)) {
				trajet.calendar.id = String.valueOf(calendarId);
				calendarId++;
				newCalendars.put(trajet.calendar, trajet.calendar.id);
			}
			tripCourant.serviceId = newCalendars.get(trajet.calendar);
			newTrips.add(tripCourant);
			for (StopTime stopTime : trajet.stopTimes) {
				stopTime.tripId = tripCourant.id;
				newStopTimes.add(stopTime);
			}
		}
		GestionnaireGtfs.getInstance().getTrips().clear();
		for (Trip trip : newTrips) {
			GestionnaireGtfs.getInstance().getTrips().put(trip.id, trip);
		}
		GestionnaireGtfs.getInstance().getStopTimes().clear();
		GestionnaireGtfs.getInstance().resetMapStopTimesByTripId();
		for (StopTime stopTime : newStopTimes) {
			if (GestionnaireGtfs.getInstance().getStopTimes().containsKey(stopTime.getKey())) {
				System.err.println("StopTimes présent plusieurs fois après compression :");
				System.err.println("Ancien : "
						+ GestionnaireGtfs.getInstance().getStopTimes().get(stopTime.getKey()).toString());
				System.err.println("Nouveau : " + stopTime.toString());
			}
			GestionnaireGtfs.getInstance().getStopTimes().put(stopTime.getKey(), stopTime);
		}
		GestionnaireGtfs.getInstance().getCalendars().clear();
		for (Calendar calendar : newCalendars.keySet()) {
			GestionnaireGtfs.getInstance().getCalendars().put(calendar.id, calendar);
		}
	}
}
