package fr.ybo.transportsrenneshelper.gtfs.gestionnaire;

import fr.ybo.transportsrenneshelper.gtfs.modele.*;
import fr.ybo.transportsrenneshelper.moteurcsv.ErreurMoteurCsv;
import fr.ybo.transportsrenneshelper.moteurcsv.MoteurCsv;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestionnaireGtfs {

	private static final List<Class<?>> GTFS_CLASSES = new ArrayList<Class<?>>();

	static {
		GTFS_CLASSES.add(Calendar.class);
		GTFS_CLASSES.add(Route.class);
		GTFS_CLASSES.add(Stop.class);
		GTFS_CLASSES.add(StopTime.class);
		GTFS_CLASSES.add(Trip.class);
	}

	private static GestionnaireGtfs gestionnaire = null;

	private File repertoire;

	private MoteurCsv moteurCsv;

	private GestionnaireGtfs() {
	}

	synchronized public static void initInstance(File repertoireGtfs) {
		gestionnaire = new GestionnaireGtfs();
		gestionnaire.repertoire = repertoireGtfs;
		gestionnaire.moteurCsv = new MoteurCsv(GTFS_CLASSES);
	}

	synchronized public static GestionnaireGtfs getInstance() {
		return gestionnaire;
	}

	private Map<String, Calendar> calendars = null;
	private Map<String, Route> routes = null;
	private Map<String, Stop> stops = null;
	private Map<String, StopTime> stopTimes = null;
	private Map<String, Trip> trips = null;

	public Map<String, Calendar> getMapCalendars() {
		if (calendars == null) {
			calendars = new HashMap<String, Calendar>();
			try {
				for (Calendar calendar : moteurCsv.parseFile(new File(repertoire, "calendar.txt"), Calendar.class)) {
					if (calendars.containsKey(calendar.id)) {
						System.err.println("Calendar présent plusieurs fois");
						System.err.println("Premier : " + calendars.get(calendar.id).toString() );
						System.err.println("Deuxième : " + calendar.toString() );
					}
					calendars.put(calendar.id, calendar);
				}
			} catch (IOException e) {
				throw new ErreurMoteurCsv(e);
			}
		}
		return calendars;
	}

	public Map<String, Route> getMapRoutes() {
		if (routes == null) {
			routes = new HashMap<String, Route>();
			try {
				for (Route route : moteurCsv.parseFile(new File(repertoire, "routes.txt"), Route.class)) {
					if (routes.containsKey(route.id)) {
						System.err.println("Route présente plusieurs fois");
						System.err.println("Première : " + routes.get(route.id).toString() );
						System.err.println("Deuxième : " + route.toString() );
					}
					routes.put(route.id, route);
				}
			} catch (IOException e) {
				throw new ErreurMoteurCsv(e);
			}
		}
		return routes;
	}

	public Map<String, Stop> getMapStops() {
		if (stops == null) {
			stops = new HashMap<String, Stop>();
			try {
				for (Stop stop : moteurCsv.parseFile(new File(repertoire, "stops.txt"), Stop.class)) {
					if (stops.containsKey(stop.id)) {
						System.err.println("Stop présent plusieurs fois");
						System.err.println("Premier : " + stops.get(stop.id).toString() );
						System.err.println("Deuxième : " + stop.toString() );
					}
					stops.put(stop.id, stop);
				}
			} catch (IOException e) {
				throw new ErreurMoteurCsv(e);
			}
		}
		return stops;
	}

	public Map<String, StopTime> getMapStopTimes() {
		if (stopTimes == null) {
			stopTimes = new HashMap<String, StopTime>();
			try {
				for (StopTime stopTime : moteurCsv.parseFile(new File(repertoire, "stop_times.txt"), StopTime.class)) {
					if (stopTimes.containsKey(stopTime.getKey())) {
						System.err.println("StopTime présent plusieurs fois");
						System.err.println("Premier : " + stopTimes.get(stopTime.getKey()).toString() );
						System.err.println("Deuxième : " + stopTime.toString() );
					}
					stopTimes.put(stopTime.getKey(), stopTime);
				}
			} catch (IOException e) {
				throw new ErreurMoteurCsv(e);
			}
		}
		return stopTimes;
	}

	public Map<String, Trip> getMapTrips() {
		if (trips == null) {
			trips = new HashMap<String, Trip>();
			try {
				for (Trip trip : moteurCsv.parseFile(new File(repertoire, "trips.txt"), Trip.class)) {
					if (trips.containsKey(trip.id)) {
						System.err.println("Trip présent plusieurs fois");
						System.err.println("Premier : " + trips.get(trip.id).toString() );
						System.err.println("Deuxième : " + trip.toString() );
					}
					trips.put(trip.id, trip);
				}
			} catch (IOException e) {
				throw new ErreurMoteurCsv(e);
			}
		}
		return trips;
	}

	private Map<String, List<StopTime>> mapStopTimesByTripId = null;

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

	public void resetMapStopTimesByTripId() {
		mapStopTimesByTripId = null;
	}
}
