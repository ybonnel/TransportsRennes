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
package fr.ybo.transportsrennes.verificator.modele.gtfs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.ybo.moteurcsv.MoteurCsv;
import fr.ybo.moteurcsv.exception.NombreErreurDepasseException;
import fr.ybo.moteurcsv.modele.ParametresMoteur;
import fr.ybo.transportsrennes.verificator.modele.FournisseurDonnees;

/**
 * Gestionnaire des fichiers GTFS.
 */
public final class GestionnaireGtfs implements FournisseurDonnees {


    /**
	 * 
	 */
	private static final String TRIPS_TXT = "/fr/ybo/transportsrennes/verificator/modele/gtfs/files/trips.txt";

	/**
	 * 
	 */
	private static final String STOP_TIMES_TXT = "/fr/ybo/transportsrennes/verificator/modele/gtfs/files/stop_times.txt";

	/**
	 * 
	 */
	private static final String STOPS_TXT = "/fr/ybo/transportsrennes/verificator/modele/gtfs/files/stops.txt";

	/**
	 * 
	 */
	private static final String ROUTES_TXT = "/fr/ybo/transportsrennes/verificator/modele/gtfs/files/routes.txt";

	/**
	 * 
	 */
	private static final String CALENDAR_TXT = "/fr/ybo/transportsrennes/verificator/modele/gtfs/files/calendar.txt";

	/**
     * Instance (singleton).
     */
    private static GestionnaireGtfs gestionnaire;

    /**
     * Moteur CSV.
     */
	private MoteurCsv moteurCsv = new MoteurCsv( //
			ParametresMoteur.createBuilder() //
					.setNbLinesWithErrorsToStop(1) //
					.setValidation(true).build(), //
			Calendar.class, Route.class, Stop.class, StopTime.class, Trip.class);

    /**
     * Constructeur privé pour empécher l'instanciation en dehors du singleton.
     */
    private GestionnaireGtfs() {
    }

    /**
     * @return l'instance.
     */
    public static synchronized GestionnaireGtfs getInstance() {
        return gestionnaire;
    }

    /**
     * Calendars (par id).
     */
    private Map<String, Calendar> calendars;
    /**
     * Routes (par id).
     */
    private Map<String, Route> routes;
    /**
     * Stops (par id).
     */
    private Map<String, Stop> stops;
    /**
     * StopTimes (par clé : tripId + stopId).
     */
    private Map<String, StopTime> stopTimes;
    /**
     * Trips (par id).
     */
    private Map<String, Trip> trips;

	/**
	 * @return les calendars (par id).
	 * @throws NombreErreurDepasseException
	 */
    public Map<String, Calendar> getMapCalendars() throws NombreErreurDepasseException {
        if (calendars == null) {
            calendars = new HashMap<String, Calendar>();
			for (Calendar calendar : getMoteurCsv().parseInputStream(
					GestionnaireGtfs.class.getResourceAsStream(CALENDAR_TXT), Calendar.class).getObjets()) {
				if (calendars.containsKey(calendar.getId())) {
					System.err.println("Calendar présent plusieurs fois");
					System.err.println("Premier : " + calendars.get(calendar.getId()).toString());
					System.err.println("Deuxième : " + calendar.toString());
				}
				calendars.put(calendar.getId(), calendar);
			}
        }
        return calendars;
    }

	/**
	 * @return Les routes (par routeId).
	 * @throws NombreErreurDepasseException
	 */
    public Map<String, Route> getMapRoutes() throws NombreErreurDepasseException {
        if (routes == null) {
            routes = new HashMap<String, Route>();
			for (Route route : getMoteurCsv().parseInputStream(GestionnaireGtfs.class.getResourceAsStream(ROUTES_TXT),
					Route.class).getObjets()) {
				if (routes.containsKey(route.getId())) {
					System.err.println("Route présente plusieurs fois");
					System.err.println("Première : " + routes.get(route.getId()).toString());
					System.err.println("Deuxième : " + route.toString());
				}
				routes.put(route.getId(), route);
			}
        }
        return routes;
    }

	/**
	 * @return les stops (par id).
	 * @throws NombreErreurDepasseException
	 */
    public Map<String, Stop> getMapStops() throws NombreErreurDepasseException {
        if (stops == null) {
			stops = new HashMap<String, Stop>();
			for (Stop stop : getMoteurCsv().parseInputStream(GestionnaireGtfs.class.getResourceAsStream(STOPS_TXT),
					Stop.class).getObjets()) {
				if (stops.containsKey(stop.getId())) {
					System.err.println("Stop présent plusieurs fois");
					System.err.println("Premier : " + stops.get(stop.getId()).toString());
					System.err.println("Deuxième : " + stop.toString());
				}
				stops.put(stop.getId(), stop);
			}
        }
        return stops;
    }

	/**
	 * @return les stopsTimes (par clé -> tripId + stopId).
	 * @throws NombreErreurDepasseException
	 */
    public Map<String, StopTime> getMapStopTimes() throws NombreErreurDepasseException {
        if (stopTimes == null) {
            stopTimes = new HashMap<String, StopTime>();
			for (StopTime stopTime : getMoteurCsv().parseInputStream(
					GestionnaireGtfs.class.getResourceAsStream(STOP_TIMES_TXT), StopTime.class).getObjets()) {
				if (stopTimes.containsKey(stopTime.getKey())) {
					System.err.println("StopTime présent plusieurs fois");
					System.err.println("Premier : " + stopTimes.get(stopTime.getKey()).toString());
					System.err.println("Deuxième : " + stopTime.toString());
				}
				stopTimes.put(stopTime.getKey(), stopTime);
			}
        }
        return stopTimes;
    }

	/**
	 * @return les trips (par id).
	 * @throws NombreErreurDepasseException
	 */
    public Map<String, Trip> getMapTrips() throws NombreErreurDepasseException {
        if (trips == null) {
            trips = new HashMap<String, Trip>();
			for (Trip trip : getMoteurCsv().parseInputStream(GestionnaireGtfs.class.getResourceAsStream(TRIPS_TXT),
					Trip.class).getObjets()) {
				if (trips.containsKey(trip.getId())) {
					System.err.println("Trip présent plusieurs fois");
					System.err.println("Premier : " + trips.get(trip.getId()).toString());
					System.err.println("Deuxième : " + trip.toString());
				}
				trips.put(trip.getId(), trip);
			}
        }
        return trips;
    }

    /**
     * Map des stopTimes par tripId.
     */
    private Map<String, List<StopTime>> mapStopTimesByTripId;

	/**
	 * @param tripId
	 *            un tripId;
	 * @return les stopsTimes associés au tripId.
	 * @throws NombreErreurDepasseException
	 */
    public List<StopTime> getStopTimesForOnTrip(String tripId) throws NombreErreurDepasseException {
        if (mapStopTimesByTripId == null) {
            mapStopTimesByTripId = new HashMap<String, List<StopTime>>();
            for (StopTime stopTime : getMapStopTimes().values()) {
				if (!mapStopTimesByTripId.containsKey(stopTime.getTripId())) {
					mapStopTimesByTripId.put(stopTime.getTripId(), new ArrayList<StopTime>());
                }
				mapStopTimesByTripId.get(stopTime.getTripId()).add(stopTime);
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

    /**
     * @return le moteur csv.
     */
    public MoteurCsv getMoteurCsv() {
        return moteurCsv;
    }
}
