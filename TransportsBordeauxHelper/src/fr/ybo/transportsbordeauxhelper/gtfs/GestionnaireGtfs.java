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
package fr.ybo.transportsbordeauxhelper.gtfs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import fr.ybo.moteurcsv.MoteurCsv;
import fr.ybo.transportsbordeauxhelper.exception.TbcException;
import fr.ybo.transportsbordeauxhelper.gtfs.modele.Agency;
import fr.ybo.transportsbordeauxhelper.gtfs.modele.Calendar;
import fr.ybo.transportsbordeauxhelper.gtfs.modele.CalendarDates;
import fr.ybo.transportsbordeauxhelper.gtfs.modele.Route;
import fr.ybo.transportsbordeauxhelper.gtfs.modele.Stop;
import fr.ybo.transportsbordeauxhelper.gtfs.modele.StopTime;
import fr.ybo.transportsbordeauxhelper.gtfs.modele.Trip;

/**
 * Gestionnaire des fichiers GTFS.
 */
public final class GestionnaireGtfs {

	/**
	 * Liste des classes associées aux fichiers GTFS.
	 */
	private static final List<Class<?>> GTFS_CLASSES = new ArrayList<Class<?>>(7);

	static {
		GTFS_CLASSES.add(Agency.class);
		GTFS_CLASSES.add(Calendar.class);
		GTFS_CLASSES.add(CalendarDates.class);
		GTFS_CLASSES.add(Route.class);
		GTFS_CLASSES.add(Stop.class);
		GTFS_CLASSES.add(StopTime.class);
		GTFS_CLASSES.add(Trip.class);
	}

	/**
	 * Instance (singleton).
	 */
	private static GestionnaireGtfs gestionnaire;

	/**
	 * Moteur CSV.
	 */
	private MoteurCsv moteurCsv;

	/**
	 * Constructeur privé pour empécher l'instanciation en dehors du singleton.
	 */
	private GestionnaireGtfs() {
	}

	/**
	 * @return l'instance.
	 */
	public static synchronized GestionnaireGtfs getInstance() {
		if (gestionnaire == null) {
			gestionnaire = new GestionnaireGtfs();
			gestionnaire.moteurCsv = new MoteurCsv(GTFS_CLASSES);
			gestionnaire.chargeDonnees();
		}
		return gestionnaire;
	}


	public void optimizeIds() {
		System.out.println("Optimisation des ids.");
		Map<String, Integer> calendriersId = new HashMap<String, Integer>();
		int currentId = 1;
		for (Calendar calendar : getCalendars().values()) {
			calendriersId.put(calendar.id, currentId++);
		}
		for (Entry<String, Integer> entry : calendriersId.entrySet()) {
			Calendar calendar = getCalendars().remove(entry.getKey());
			calendar.id = entry.getValue().toString();
			getCalendars().put(calendar.id, calendar);
		}
		for (CalendarDates calendarDates : getCalendarsDates()) {
			calendarDates.serviceId = calendriersId.get(calendarDates.serviceId).toString();
		}
		for (Trip trip : getTrips().values()) {
			trip.serviceId = calendriersId.get(trip.serviceId).toString();
		}

		Map<String, Integer> tripIds = new HashMap<String, Integer>();
		currentId = 1;
		for (Trip trip : getTrips().values()) {
			tripIds.put(trip.id, currentId++);
		}
		for (Entry<String, Integer> entry : tripIds.entrySet()) {
			Trip trip = getTrips().remove(entry.getKey());
			trip.id = entry.getValue().toString();
			getTrips().put(trip.id, trip);
		}
		for (StopTime stopTime : getStopTimes().values()) {
			stopTime.tripId = tripIds.get(stopTime.tripId).toString();
		}
	}

	@SuppressWarnings("unchecked")
	private void chargeDonnees() {
		try {
			Map<Class<?>, List<?>> mapTram = chargeZip("/gtfstbc/keolis_tram.zip");
			Map<Class<?>, List<?>> mapBus = chargeZip("/gtfstbc/keolis_bus.zip");
			System.out.println("Merge agencies");
			for (Agency agency : (List<Agency>) mapTram.get(Agency.class)) {
				if (!agencies.contains(agency)) {
					agencies.add(agency);
				}
			}
			for (Agency agency : (List<Agency>) mapBus.get(Agency.class)) {
				if (!agencies.contains(agency)) {
					agencies.add(agency);
				}
			}
			System.out.println("Merge calandars");
			for (Calendar calendar : (List<Calendar>) mapTram.get(Calendar.class)) {
				if (calendars.containsKey(calendar.id)) {
					System.err.println("Calendar en double " + calendar.id);
				}
				calendars.put(calendar.id, calendar);
			}
			for (Calendar calendar : (List<Calendar>) mapBus.get(Calendar.class)) {
				if (calendars.containsKey(calendar.id)) {
					System.err.println("Calendar en double " + calendar.id);
				}
				calendars.put(calendar.id, calendar);
			}
			System.out.println("Merge calendarDates");
			calendarsDates.addAll((List<CalendarDates>) mapTram.get(CalendarDates.class));
			calendarsDates.addAll((List<CalendarDates>) mapBus.get(CalendarDates.class));
			System.out.println("Merge routes");
			for (Route route : (List<Route>) mapTram.get(Route.class)) {
				routes.put(route.id, route);
			}
			for (Route route : (List<Route>) mapBus.get(Route.class)) {
				routes.put(route.id, route);
			}
			System.out.println("Merge stops");
			for (Stop stop : (List<Stop>) mapTram.get(Stop.class)) {
				if (stops.containsKey(stop.id)) {
					Stop autreStop = stops.get(stop.id);
					if (!stop.equals(autreStop)) {
						System.out.println("Stop déjà connu : " + autreStop + "\n" + stop);
					}
				}
				stops.put(stop.id, stop);
			}
			for (Stop stop : (List<Stop>) mapBus.get(Stop.class)) {
				stop.id = stop.id + "b";
				if (stops.containsKey(stop.id)) {
					Stop autreStop = stops.get(stop.id);
					if (!stop.equals(autreStop)) {
						System.err.println("Stop déjà connu : " + autreStop + "\n" + stop);
					}
				}
				stops.put(stop.id, stop);
			}
			System.out.println("Merge trips");
			for (Trip trip : (List<Trip>) mapTram.get(Trip.class)) {
				if (trips.containsKey(trip.id)) {
					System.err.println("Trip déjà connu : " + trip.id);
				}
				trips.put(trip.id, trip);
			}
			for (Trip trip : (List<Trip>) mapBus.get(Trip.class)) {
				if (trips.containsKey(trip.id)) {
					System.err.println("Trip déjà connu : " + trip.id);
				}
				trips.put(trip.id, trip);
			}
			System.out.println("Merge stopTime");
			for (StopTime stopTime : (List<StopTime>) mapTram.get(StopTime.class)) {
				if (stopTimes.containsKey(stopTime.getKey())) {
					System.err.println("StopTime déjà connu : " + stopTime.getKey());
				}
				stopTimes.put(stopTime.getKey(), stopTime);
			}
			for (StopTime stopTime : (List<StopTime>) mapBus.get(StopTime.class)) {
				stopTime.stopId = stopTime.stopId + "b";
				if (stopTimes.containsKey(stopTime.getKey())) {
					System.err.println("StopTime déjà connu : " + stopTime.getKey());
				}
				stopTimes.put(stopTime.getKey(), stopTime);
			}
		} catch (IOException ioException) {
			throw new TbcException(ioException);
		}
	}

	private List<Agency> agencies = new ArrayList<Agency>();
	private Map<String, Calendar> calendars = new HashMap<String, Calendar>();
	private List<CalendarDates> calendarsDates = new ArrayList<CalendarDates>();
	private Map<String, Route> routes = new HashMap<String, Route>();
	private Map<String, Stop> stops = new HashMap<String, Stop>();
	private Map<String, Trip> trips = new HashMap<String, Trip>();
	private Map<String, StopTime> stopTimes = new HashMap<String, StopTime>();


	private Map<Class<?>, List<?>> chargeZip(String file) throws IOException {
		System.out.println("Lecture du fichier " + file);
		Map<Class<?>, List<?>> mapObjets = new HashMap<Class<?>, List<?>>();
		ZipInputStream zipGtfs = new ZipInputStream(GestionnaireGtfs.class.getResourceAsStream(file));
		ZipEntry zipEntry = zipGtfs.getNextEntry();
		while (zipEntry != null) {
			System.out.println("Chargement du fichier " + zipEntry.getName());
			Class<?> clazz = moteurCsv.getClassByFileName(zipEntry.getName());
			if (clazz != null) {
				mapObjets.put(clazz, moteurCsv.parseInputStream(zipGtfs, clazz));
			}
			zipEntry = zipGtfs.getNextEntry();
		}
		return mapObjets;
	}

	/**
	 * Map des stopTimes par tripId.
	 */
	private Map<String, List<StopTime>> mapStopTimesByTripId;

	/**
	 * @param tripId un tripId;
	 * @return les stopsTimes associés au tripId.
	 */
	public List<StopTime> getStopTimesForOnTrip(String tripId) {
		if (mapStopTimesByTripId == null) {
			mapStopTimesByTripId = new HashMap<String, List<StopTime>>();
			for (StopTime stopTime : getStopTimes().values()) {
				if (!mapStopTimesByTripId.containsKey(stopTime.tripId)) {
					mapStopTimesByTripId.put(stopTime.tripId, new ArrayList<StopTime>());
				}
				mapStopTimesByTripId.get(stopTime.tripId).add(stopTime);
			}
		}

		return mapStopTimesByTripId.get(tripId);
	}

	/**
	 * Reset de la map des stopsTimes par tripId.
	 */
	public void resetMapStopTimesByTripId() {
		mapStopTimesByTripId = null;
	}

	public List<Agency> getAgencies() {
		return agencies;
	}

	public Map<String, Calendar> getCalendars() {
		return calendars;
	}

	public List<CalendarDates> getCalendarsDates() {
		return calendarsDates;
	}

	public Map<String, Route> getRoutes() {
		return routes;
	}

	public Map<String, Stop> getStops() {
		return stops;
	}

	public Map<String, Trip> getTrips() {
		return trips;
	}

	public Map<String, StopTime> getStopTimes() {
		return stopTimes;
	}
}
