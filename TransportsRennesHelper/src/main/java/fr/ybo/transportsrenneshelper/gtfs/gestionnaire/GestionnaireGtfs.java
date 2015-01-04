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
package fr.ybo.transportsrenneshelper.gtfs.gestionnaire;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;


import fr.ybo.transportsrenneshelper.gtfs.modele.Calendar;
import fr.ybo.transportsrenneshelper.gtfs.modele.CalendarDates;
import fr.ybo.transportsrenneshelper.gtfs.modele.Route;
import fr.ybo.transportsrenneshelper.gtfs.modele.Stop;
import fr.ybo.transportsrenneshelper.gtfs.modele.StopTime;
import fr.ybo.transportsrenneshelper.gtfs.modele.Trip;
import fr.ybonnel.csvengine.CsvEngine;
import fr.ybonnel.csvengine.exception.CsvErrorsExceededException;
import fr.ybonnel.csvengine.model.EngineParameters;

/**
 * Gestionnaire des fichiers GTFS.
 */
public final class GestionnaireGtfs {

    /**
     * Liste des classes associées aux fichiers GTFS.
     */
    private static final Class<?>[] GTFS_CLASSES = {
        Calendar.class,
        CalendarDates.class,
        Route.class,
        Stop.class,
        StopTime.class,
        Trip.class
    };

    /**
     * Instance (singleton).
     */
    private static GestionnaireGtfs gestionnaire;

    /**
     * Répertoire de lecture.
     */
    private File repertoire;

    /**
     * Moteur CSV.
     */
    private CsvEngine moteurCsv;

    /**
     * Constructeur privé pour empécher l'instanciation en dehors du singleton.
     */
    private GestionnaireGtfs() {
    }

    /**
     * Création de l'instance.
     *
     * @param repertoireGtfs répertoire de lecture.
     */
    public static synchronized void initInstance(File repertoireGtfs) {
        gestionnaire = new GestionnaireGtfs();
        gestionnaire.repertoire = repertoireGtfs;
        gestionnaire.moteurCsv = new CsvEngine(EngineParameters.createBuilder().setAddQuoteCar(false).build(), GTFS_CLASSES);
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
    
    private List<CalendarDates> calendarsDates;

    /**
     * @return les calendars (par id).
     */
    public Map<String, Calendar> getMapCalendars() {
        if (calendars == null) {
            calendars = new HashMap<String, Calendar>();
            try {
                for (Calendar calendar : getMoteurCsv()
                        .parseInputStream(new FileInputStream(new File(repertoire, "calendar.txt")), Calendar.class).getObjects()) {
                    if (calendars.containsKey(calendar.id)) {
                        System.err.println("Calendar présent plusieurs fois");
                        System.err.println("Premier : " + calendars.get(calendar.id).toString());
                        System.err.println("Deuxième : " + calendar.toString());
                    }
                    calendars.put(calendar.id, calendar);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (CsvErrorsExceededException e) {
                throw new RuntimeException(e);
            }
        }
        return calendars;
    }
    
    public List<CalendarDates> getCalendarsDates() {
    	if (calendarsDates == null) {
    		try {
				calendarsDates = getMoteurCsv().parseInputStream(new FileInputStream(new File(repertoire, "calendar_dates.txt")), CalendarDates.class).getObjects();
			} catch (FileNotFoundException e) {
                throw new RuntimeException(e);
			} catch (CsvErrorsExceededException e) {
                throw new RuntimeException(e);
            }
    	}
		return calendarsDates;
	}

    /**
     * @return Les routes (par routeId).
     */
    public Map<String, Route> getMapRoutes() {
        if (routes == null) {
            routes = new LinkedHashMap<String, Route>();
            try {
                for (Route route : getMoteurCsv()
                        .parseInputStream(new FileInputStream(new File(repertoire, "routes.txt")), Route.class).getObjects()) {
                    if (routes.containsKey(route.id)) {
                        System.err.println("Route présente plusieurs fois");
                        System.err.println("Première : " + routes.get(route.id).toString());
                        System.err.println("Deuxième : " + route.toString());
                    }
                    routes.put(route.id, route);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (CsvErrorsExceededException e) {
                throw new RuntimeException(e);
            }
        }
        return routes;
    }

    /**
     * @return les stops (par id).
     */
    public Map<String, Stop> getMapStops() {
        if (stops == null) {
            stops = new HashMap<String, Stop>();
            try {
                for (Stop stop : getMoteurCsv()
                        .parseInputStream(new FileInputStream(new File(repertoire, "stops.txt")), Stop.class).getObjects()) {
                    if (stops.containsKey(stop.id)) {
                        System.err.println("Stop présent plusieurs fois");
                        System.err.println("Premier : " + stops.get(stop.id).toString());
                        System.err.println("Deuxième : " + stop.toString());
                    }
                    if (stop.code == null || stop.code.length() == 0) {
                    	System.err.println("Le stop " + stop.id + "(" + stop.nom + ") est écarté car il n'a pas de stop_code.");
                    } else {
                        stops.put(stop.id, stop);                    	
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (CsvErrorsExceededException e) {
                throw new RuntimeException(e);
            }
        }
        return stops;
    }

    /**
     * @return les stopsTimes (par clé -> tripId + stopId).
     */
    public Map<String, StopTime> getMapStopTimes() {
        if (stopTimes == null) {
            stopTimes = new HashMap<String, StopTime>();
            try {
                for (StopTime stopTime : getMoteurCsv()
                        .parseInputStream(new FileInputStream(new File(repertoire, "stop_times.txt")),
                                StopTime.class).getObjects()) {
                    if (stopTimes.containsKey(stopTime.getKey())) {
                        System.err.println("StopTime présent plusieurs fois");
                        System.err.println("Premier : " + stopTimes.get(stopTime.getKey()).toString());
                        System.err.println("Deuxième : " + stopTime.toString());
                    }
                    if (getMapStops().containsKey(stopTime.stopId)) {
                    	stopTimes.put(stopTime.getKey(), stopTime);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (CsvErrorsExceededException e) {
                throw new RuntimeException(e);
            }
        }
        return stopTimes;
    }

    /**
     * @return les trips (par id).
     */
    public Map<String, Trip> getMapTrips() {
        if (trips == null) {
            trips = new HashMap<String, Trip>();
            try {
                for (Trip trip : getMoteurCsv()
                        .parseInputStream(new FileInputStream(new File(repertoire, "trips.txt")), Trip.class).getObjects()) {
                    if (trips.containsKey(trip.id)) {
                        System.err.println("Trip présent plusieurs fois");
                        System.err.println("Premier : " + trips.get(trip.id).toString());
                        System.err.println("Deuxième : " + trip.toString());
                    }
					// Verrue pour le problème sur la ligne 50.
					if (trip.routeId.equals("0050") && trip.directionId == 0
							&& trip.headSign.equals("50 | Thorigné-Fouillard")) {
						trip.headSign = "50 | Rennes République";
						System.err.println("Application de la verrue pour le trip " + trip.id);
					}
                    if (trip.headSign.equals("8 St Grég via Pon")) {
                        trip.headSign = "8 | Saint Grégoire via Pontay";
                    }
                    trips.put(trip.id, trip);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (CsvErrorsExceededException e) {
                throw new RuntimeException(e);
            }
        }
        return trips;
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
            for (StopTime stopTime : getMapStopTimes().values()) {
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

    /**
     * @return le moteur csv.
     */
    public CsvEngine getMoteurCsv() {
        return moteurCsv;
    }
}
