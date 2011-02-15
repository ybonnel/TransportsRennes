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

import fr.ybo.transportsrenneshelper.gtfs.modele.Calendar;
import fr.ybo.transportsrenneshelper.gtfs.modele.Route;
import fr.ybo.transportsrenneshelper.gtfs.modele.RouteExtension;
import fr.ybo.transportsrenneshelper.gtfs.modele.Stop;
import fr.ybo.transportsrenneshelper.gtfs.modele.StopExtension;
import fr.ybo.transportsrenneshelper.gtfs.modele.StopTime;
import fr.ybo.transportsrenneshelper.gtfs.modele.Trip;
import fr.ybo.transportsrenneshelper.moteurcsv.MoteurCsvException;
import fr.ybo.transportsrenneshelper.moteurcsv.MoteurCsv;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"UseOfSystemOutOrSystemErr"})
public class GestionnaireGtfs {

	private static final List<Class<?>> GTFS_CLASSES = new ArrayList<Class<?>>();

	static {
		GTFS_CLASSES.add(Calendar.class);
		GTFS_CLASSES.add(Route.class);
		GTFS_CLASSES.add(Stop.class);
		GTFS_CLASSES.add(StopTime.class);
		GTFS_CLASSES.add(Trip.class);
		GTFS_CLASSES.add(StopExtension.class);
		GTFS_CLASSES.add(RouteExtension.class);
	}

	@SuppressWarnings({"StaticNonFinalField"})
	private static GestionnaireGtfs gestionnaire;

	private File repertoire;

	private MoteurCsv moteurCsv;

	private GestionnaireGtfs() {
		super();
	}

	public static synchronized void initInstance(final File repertoireGtfs) {
		gestionnaire = new GestionnaireGtfs();
		gestionnaire.repertoire = repertoireGtfs;
		gestionnaire.moteurCsv = new MoteurCsv(GTFS_CLASSES);
	}

	public static synchronized GestionnaireGtfs getInstance() {
		return gestionnaire;
	}

	private Map<String, Calendar> calendars;
	private Map<String, Route> routes;
	private Map<String, Stop> stops;
	private Map<String, StopTime> stopTimes;
	private Map<String, Trip> trips;
	private Map<String, StopExtension> stopExtensions;
	private Map<String, RouteExtension> routeExtensions;

	public Map<String, StopExtension> getStopExtensions() {
		if (stopExtensions == null) {
			stopExtensions = new HashMap<String, StopExtension>();
			try {
				for (final StopExtension stopExtension : moteurCsv.parseFile(new File(repertoire, "stops_extensions.txt"), StopExtension.class)) {
					stopExtensions.put(stopExtension.stopId, stopExtension);
				}
			} catch (IOException e) {
				throw new MoteurCsvException(e);
			}
		}
		return stopExtensions;
	}

	public Map<String, RouteExtension> getRouteExtensions() {
		if (routeExtensions == null) {
			routeExtensions = new HashMap<String, RouteExtension>();
			try {
				for (final RouteExtension routeExtension : moteurCsv.parseFile(new File(repertoire, "routes_extensions.txt"), RouteExtension.class)) {
					routeExtensions.put(routeExtension.routeId, routeExtension);
				}
			} catch (IOException e) {
				throw new MoteurCsvException(e);
			}
		}
		return routeExtensions;
	}

	public Map<String, Calendar> getMapCalendars() {
		if (calendars == null) {
			calendars = new HashMap<String, Calendar>();
			try {
				for (final Calendar calendar : moteurCsv.parseFile(new File(repertoire, "calendar.txt"), Calendar.class)) {
					if (calendars.containsKey(calendar.id)) {
						System.err.println("Calendar présent plusieurs fois");
						System.err.println("Premier : " + calendars.get(calendar.id).toString() );
						System.err.println("Deuxième : " + calendar.toString() );
					}
					calendars.put(calendar.id, calendar);
				}
			} catch (IOException e) {
				throw new MoteurCsvException(e);
			}
		}
		return calendars;
	}

	public Map<String, Route> getMapRoutes() {
		if (routes == null) {
			routes = new HashMap<String, Route>();
			try {
				for (final Route route : moteurCsv.parseFile(new File(repertoire, "routes.txt"), Route.class)) {
					if (routes.containsKey(route.id)) {
						System.err.println("Route présente plusieurs fois");
						System.err.println("Première : " + routes.get(route.id).toString() );
						System.err.println("Deuxième : " + route.toString() );
					}
					routes.put(route.id, route);
				}
			} catch (IOException e) {
				throw new MoteurCsvException(e);
			}
		}
		return routes;
	}

	public Map<String, Stop> getMapStops() {
		if (stops == null) {
			stops = new HashMap<String, Stop>();
			try {
				for (final Stop stop : moteurCsv.parseFile(new File(repertoire, "stops.txt"), Stop.class)) {
					if (stops.containsKey(stop.id)) {
						System.err.println("Stop présent plusieurs fois");
						System.err.println("Premier : " + stops.get(stop.id).toString() );
						System.err.println("Deuxième : " + stop.toString() );
					}
					stops.put(stop.id, stop);
				}
			} catch (IOException e) {
				throw new MoteurCsvException(e);
			}
		}
		return stops;
	}

	public Map<String, StopTime> getMapStopTimes() {
		if (stopTimes == null) {
			stopTimes = new HashMap<String, StopTime>();
			try {
				for (final StopTime stopTime : moteurCsv.parseFile(new File(repertoire, "stop_times.txt"), StopTime.class)) {
					if (stopTimes.containsKey(stopTime.getKey())) {
						System.err.println("StopTime présent plusieurs fois");
						System.err.println("Premier : " + stopTimes.get(stopTime.getKey()).toString() );
						System.err.println("Deuxième : " + stopTime.toString() );
					}
					stopTimes.put(stopTime.getKey(), stopTime);
				}
			} catch (IOException e) {
				throw new MoteurCsvException(e);
			}
		}
		return stopTimes;
	}

	public Map<String, Trip> getMapTrips() {
		if (trips == null) {
			trips = new HashMap<String, Trip>();
			try {
				for (final Trip trip : moteurCsv.parseFile(new File(repertoire, "trips.txt"), Trip.class)) {
					if (trips.containsKey(trip.id)) {
						System.err.println("Trip présent plusieurs fois");
						System.err.println("Premier : " + trips.get(trip.id).toString() );
						System.err.println("Deuxième : " + trip.toString() );
					}
					trips.put(trip.id, trip);
				}
			} catch (IOException e) {
				throw new MoteurCsvException(e);
			}
		}
		return trips;
	}

	private Map<String, List<StopTime>> mapStopTimesByTripId;

	public List<StopTime> getStopTimesForOnTrip(final String tripId) {
		if (mapStopTimesByTripId == null) {
			mapStopTimesByTripId = new HashMap<String, List<StopTime>>();
			for (final StopTime stopTime : getMapStopTimes().values()) {
				if (!mapStopTimesByTripId.containsKey(stopTime.tripId)) {
					mapStopTimesByTripId.put(stopTime.tripId, new ArrayList<StopTime>());
				}
				mapStopTimesByTripId.get(stopTime.tripId).add(stopTime);
			}
		}

		return mapStopTimesByTripId.get(tripId);
	}

	public void resetMapStopTimesByTripId() {
		mapStopTimesByTripId = null;
	}
}
