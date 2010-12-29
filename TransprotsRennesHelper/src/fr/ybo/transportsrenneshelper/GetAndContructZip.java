package fr.ybo.transportsrenneshelper;


import fr.ybo.transportsrenneshelper.keolis.gtfs.modele.*;
import fr.ybo.transportsrenneshelper.keolis.gtfs.moteur.ErreurMoteurCsv;
import fr.ybo.transportsrenneshelper.keolis.gtfs.moteur.MoteurCsv;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class GetAndContructZip {
	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMdd");

	private static final int NB_JOURS_RECHERCHE = 200;

	private static final String URL_RELATIVE = "fileadmin/OpenDataFiles/GTFS/GTFS-";
	private static final String URL_KEOLIS = "http://data.keolis-rennes.com/";
	private static final String BASE_URL = URL_KEOLIS + URL_RELATIVE;
	private static final String URL_DONNEES_TELECHARGEABLES = URL_KEOLIS + "fr/les-donnees/donnees-telechargeables.html";
	private static final String EXTENSION_URL = ".zip";


	public static Date getLastUpdate() throws IOException, ParseException {

		final HttpURLConnection connection = (HttpURLConnection) new URL(URL_DONNEES_TELECHARGEABLES).openConnection();
		connection.setRequestMethod("GET");
		connection.setDoOutput(true);
		connection.connect();
		final BufferedReader bufReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		final String chaineRecherchee = URL_RELATIVE;
		String ligne;
		while ((ligne = bufReader.readLine()) != null) {
			if (ligne.contains(chaineRecherchee)) {
				final String chaineDate = ligne.substring(ligne.indexOf(chaineRecherchee) + chaineRecherchee.length(),
						ligne.indexOf(chaineRecherchee) + chaineRecherchee.length() + 8);
				return SDF.parse(chaineDate);
			}
		}
		return getLastUpdateBruteForce();
	}

	public static Date getLastUpdateBruteForce() {
		final GregorianCalendar calendar = new GregorianCalendar();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		HttpURLConnection connection;
		int nbJours = 0;
		while (nbJours < NB_JOURS_RECHERCHE) {
			try {
				connection = openHttpConnection(calendar.getTime());
				connection.getInputStream();
				return calendar.getTime();
			} catch (final IOException ioEx) {
				calendar.add(Calendar.DAY_OF_YEAR, -1);
				nbJours++;
			}
		}
		return null;
	}

	private static URL getUrlKeolisFromDate(final Date date) throws MalformedURLException {
		return new URL(BASE_URL + SDF.format(date) + EXTENSION_URL);
	}

	private static HttpURLConnection openHttpConnection(final Date dateFileKeolis) throws IOException {
		final HttpURLConnection connection = (HttpURLConnection) getUrlKeolisFromDate(dateFileKeolis).openConnection();
		connection.setRequestMethod("GET");
		connection.setDoOutput(true);
		connection.connect();
		return connection;
	}

	private final static String REPERTOIRE_SORTIE = "C:/ybonnel/GTFSRennes/";

	private static class StopIdWithSequence {
		private StopIdWithSequence(String stopId, int stopSequence) {
			this.stopId = stopId;
			this.stopSequence = stopSequence;
		}

		private String stopId;
		private int stopSequence;

		public String getStopId() {
			return stopId;
		}

		public int getStopSequence() {
			return stopSequence;
		}
	}

	protected static String genereChaineOfListe(List<StopIdWithSequence> stops) {
		Collections.sort(stops, new Comparator<StopIdWithSequence>() {
			public int compare(StopIdWithSequence o1, StopIdWithSequence o2) {
				return new Integer(o1.getStopSequence()).compareTo(o2.getStopSequence());
			}
		});
		StringBuilder stringBuilder = new StringBuilder();
		for (StopIdWithSequence stop : stops) {
			stringBuilder.append(stop.getStopId());
			stringBuilder.append(',');
		}
		return stringBuilder.toString();
	}

	public static void getAndParseZipKeolis() throws ParseException, IOException, IllegalAccessException, ErreurMoteurCsv {
		Date lastUpdate = getLastUpdate();
		final HttpURLConnection connection = openHttpConnection(lastUpdate);
		final ZipInputStream zipInputStream = new ZipInputStream(connection.getInputStream());
		ZipEntry zipEntry;
		String ligne;
		BufferedReader bufReader;
		File repertoire = new File(REPERTOIRE_SORTIE);
		if (repertoire.exists()) {
			for (File file : repertoire.listFiles()) {
				if (!file.delete()) {
					System.err.println("Le fichier " + file.getName() + "n'a pas pu être effacé");
				}
			}
		} else {
			if (!repertoire.mkdir()) {
				System.err.println("Le répertoire " + repertoire.getName() + "n'a pas pu être créé");
			}
		}
		while ((zipEntry = zipInputStream.getNextEntry()) != null) {
			System.out.println("Debut du traitement du fichier " + zipEntry.getName());
			bufReader = new BufferedReader(new InputStreamReader(zipInputStream), 8 * 1024);
			File file = new File(repertoire, zipEntry.getName());
			BufferedWriter bufWriter = new BufferedWriter(new FileWriter(file));
			while ((ligne = bufReader.readLine()) != null) {
				bufWriter.write(ligne);
				bufWriter.write('\n');
			}
			bufWriter.close();
			System.out.println("Fin du traitement du fichier " + zipEntry.getName());
		}
		connection.disconnect();
		System.out.println("Debut du traitement du fichier stopTimes.txt");
		System.out.println("Mise en place des map de refs");
		List<Class<?>> listeClassCsv = new ArrayList<Class<?>>();
		listeClassCsv.add(Route.class);
		listeClassCsv.add(Trip.class);
		listeClassCsv.add(Calendrier.class);
		listeClassCsv.add(HeuresArrets.class);
		listeClassCsv.add(ArretRoute.class);
		listeClassCsv.add(Arret.class);
		MoteurCsv moteurCsv = new MoteurCsv(listeClassCsv);

		Map<String, BufferedWriter> mapTripIdFichier = new HashMap<String, BufferedWriter>();
		List<Route> routes = moteurCsv.parseFile(new File(repertoire, "routes.txt"), Route.class);
		List<BufferedWriter> routesFiles = new ArrayList<BufferedWriter>();
		Map<String, List<Trip>> mapTripsByRoute = new HashMap<String, List<Trip>>();
		for (Trip trip : moteurCsv.parseFile(new File(repertoire, "trips.txt"), Trip.class)) {
			if (!mapTripsByRoute.containsKey(trip.getRouteId())) {
				mapTripsByRoute.put(trip.getRouteId(), new ArrayList<Trip>());
			}
			mapTripsByRoute.get(trip.getRouteId()).add(trip);
		}
		Map<String, Trip> mapTrips = new HashMap<String, Trip>();
		BufferedWriter bufWriter;
		for (Route route : routes) {
			bufWriter = new BufferedWriter(new FileWriter(new File(repertoire, "stopTimes" + route.getIdWithoutSpecCar() + ".txt")));
			routesFiles.add(bufWriter);
			for (Trip trip : mapTripsByRoute.get(route.getId())) {
				mapTrips.put(trip.getId(), trip);
				mapTripIdFichier.put(trip.getId(), bufWriter);
			}
		}
		System.out.println("Lecture du fichier et construction des relations Arret - Route.");
		bufReader = new BufferedReader(new FileReader(new File(repertoire, "stop_times.txt")), 8 * 1024);
		final String entete = bufReader.readLine();
		for (final BufferedWriter writer : routesFiles) {
			writer.write(entete);
			writer.write('\n');
		}

		Map<String, Set<String>> mapRoutes = new HashMap<String, Set<String>>();
		List<ArretRoute> listeArretsRoutes = new ArrayList<ArretRoute>();

		Map<String, Map<String, List<StopIdWithSequence>>> mapStopsByTripsIdAndRouteId = new HashMap<String, Map<String, List<StopIdWithSequence>>>();
		while ((ligne = bufReader.readLine()) != null) {
			String[] champs = ligne.split(",");
			String tripId = champs[0];
			String stopId = champs[1];
			int stopSequence = Integer.parseInt(champs[2]);
			if (mapTripIdFichier.containsKey(tripId)) {
				String routeId = mapTrips.get(tripId).getRouteId();
				if (!mapStopsByTripsIdAndRouteId.containsKey(routeId)) {
					mapStopsByTripsIdAndRouteId.put(routeId, new HashMap<String, List<StopIdWithSequence>>());
				}
				if (!mapStopsByTripsIdAndRouteId.get(routeId).containsKey(tripId)) {
					mapStopsByTripsIdAndRouteId.get(routeId).put(tripId, new ArrayList<StopIdWithSequence>());
				}
				mapStopsByTripsIdAndRouteId.get(routeId).get(tripId).add(new StopIdWithSequence(stopId, stopSequence));
				if (!mapRoutes.containsKey(routeId)) {
					mapRoutes.put(routeId, new HashSet<String>());
				}
				Set<String> stops = mapRoutes.get(routeId);
				if (!stops.contains(stopId)) {
					stops.add(stopId);
					ArretRoute arretRoute = new ArretRoute();
					arretRoute.setRouteId(routeId);
					arretRoute.setArretId(stopId);
					listeArretsRoutes.add(arretRoute);
				}
				mapTripIdFichier.get(tripId).write(ligne);
				mapTripIdFichier.get(tripId).write('\n');
			} else {
				System.err.println("Le trip " + tripId + " est inconnu.");
			}
		}
		System.out.println("Fin du traitement du fichier stopTimes.txt");

		for (final BufferedWriter bufToClose : routesFiles) {
			bufToClose.close();
		}

		System.out.println("Compression des données (regroupement de calendrier)");


		Map<String, Calendrier> calendrierActuels = new HashMap<String, Calendrier>();
		for (Calendrier calendrier : moteurCsv.parseFile(new File(repertoire, "calendar.txt"), Calendrier.class)) {
			calendrierActuels.put(calendrier.getId(), calendrier);
		}

		System.out.println("Lectures des fichiers stopTimes.txt");

		Map<Route, List<HeuresArrets>> mapStopTimes = new HashMap<Route, List<HeuresArrets>>();
		for (Route route : routes) {
			mapStopTimes
					.put(route, moteurCsv.parseFile(new File(repertoire, "stopTimes" + route.getIdWithoutSpecCar() + ".txt"), HeuresArrets.class));
		}

		System.out.println("Parcours des horaires pour compressions");


		Map<Route, Map<IdentifiantHeureArret, HeuresArrets>> mapHeuresCompressees = new HashMap<Route, Map<IdentifiantHeureArret, HeuresArrets>>();

		for (Map.Entry<Route, List<HeuresArrets>> heures : mapStopTimes.entrySet()) {
			mapHeuresCompressees.put(heures.getKey(), new HashMap<IdentifiantHeureArret, HeuresArrets>());
			Map<IdentifiantHeureArret, HeuresArrets> mapHeures = mapHeuresCompressees.get(heures.getKey());
			for (HeuresArrets heure : heures.getValue()) {
				heure.setRouteId(heures.getKey().getId());
				IdentifiantHeureArret idHeureArret = new IdentifiantHeureArret(heure.getRouteId(), heure.getStopId(), heure.getHeureDepart());
				Calendrier calendrierActuel = calendrierActuels.get(mapTrips.get(heure.getTripId()).getServiceId());
				if (!mapHeures.containsKey(idHeureArret)) {
					heure.setCalendrier(new Calendrier(calendrierActuel));
					mapHeures.put(idHeureArret, heure);
				} else {
					mapHeures.get(idHeureArret).getCalendrier().merge(calendrierActuel);
				}
			}
		}

		List<Calendrier> newCalendriers = new ArrayList<Calendrier>();

		int idCalendrier = 1;

		System.out.println("Création des calendriers");
		for (Map<IdentifiantHeureArret, HeuresArrets> heures : mapHeuresCompressees.values()) {
			for (HeuresArrets heure : heures.values()) {
				Calendrier newCalendrier = rechercherCalendrier(newCalendriers, heure.getCalendrier());
				if (newCalendrier == null) {
					heure.getCalendrier().setId(Integer.toString(idCalendrier));
					newCalendriers.add(heure.getCalendrier());
					idCalendrier++;
				} else {
					heure.setCalendrier(newCalendrier);
				}
				heure.setServiceId(heure.getCalendrier().getId());
			}
		}

		System.out.println("Ecriture du fichier calendar.txt");
		File fileCalendar = new File(repertoire, "calendar.txt");
		moteurCsv.writeFile(fileCalendar, newCalendriers, Calendrier.class);

		List<HeuresArrets> allHeures = new ArrayList<HeuresArrets>();

		System.out.println("Ecriture des fichiers stopTimes");
		for (Map.Entry<Route, Map<IdentifiantHeureArret, HeuresArrets>> entryHeures : mapHeuresCompressees.entrySet()) {
			File fileStopTimes = new File(repertoire, "stopTimes" + entryHeures.getKey().getIdWithoutSpecCar() + ".txt");
			List<HeuresArrets> heures = new ArrayList<HeuresArrets>();
			heures.addAll(entryHeures.getValue().values());
			allHeures.addAll(heures);
			moteurCsv.writeFile(fileStopTimes, heures, HeuresArrets.class, Collections.singleton("trip_id"));
		}

		System.out.println("Ecriture du fichier principal stop_times.txt");


		moteurCsv.writeFile(new File(repertoire, "stop_times.txt"), allHeures, HeuresArrets.class, Collections.singleton("trip_id"));

		System.out.println("Ajout de l'ordre dans route.txt");
		int maxLength = 0;
		for (Route route : routes) {
			if (route.getNomCourt().length() > maxLength) {
				maxLength = route.getNomCourt().length();
			}
		}
		for (Route route : routes) {
			route.setNomCourtFormatte(route.getNomCourt());
			while (route.getNomCourtFormatte().length() < maxLength) {
				route.setNomCourtFormatte("0" + route.getNomCourtFormatte());
			}
		}
		Collections.sort(routes, new Comparator<Route>() {
			public int compare(Route o1, Route o2) {
				return o1.getNomCourtFormatte().compareTo(o2.getNomCourtFormatte());
			}
		});

		int ordre = 1;
		for (Route route : routes) {
			route.setOrdre(ordre++);
		}

		System.out.println("Ecriture du fichier routes.txt");
		moteurCsv.writeFile(new File(repertoire, "routes.txt"), routes, Route.class);


		System.out.println("Ajout des informations complémentaires à arretRoute");
		Map<String, Arret> mapArrets = new HashMap<String, Arret>();
		for (Arret arret : moteurCsv.parseFile(new File(repertoire, "stops.txt"), Arret.class)) {
			mapArrets.put(arret.getId(), arret);
		}
		for (String routeId : mapStopsByTripsIdAndRouteId.keySet()) {
			Map<String, Integer> mapCompteurTrip = new HashMap<String, Integer>();
			for (Map.Entry<String, List<StopIdWithSequence>> entry : mapStopsByTripsIdAndRouteId.get(routeId).entrySet()) {
				List<StopIdWithSequence> stops = new ArrayList<StopIdWithSequence>();
				for (StopIdWithSequence stop : entry.getValue()) {
					stops.add(stop);
				}
				String stopsChaine = genereChaineOfListe(stops);
				if (!mapCompteurTrip.containsKey(stopsChaine)) {
					mapCompteurTrip.put(stopsChaine, 0);
				}
				mapCompteurTrip.put(stopsChaine, mapCompteurTrip.get(stopsChaine) + 1);
			}
			Map<String, List<ArretRoute>> mapArretRouteByDirection = new HashMap<String, List<ArretRoute>>();
			for (ArretRoute arretRoute : listeArretsRoutes) {
				if (arretRoute.getRouteId().equals(routeId)) {
					int max = 0;
					String chaineBonChemin = null;
					for (Map.Entry<String, Integer> entry : mapCompteurTrip.entrySet()) {
						if ((entry.getKey().startsWith(arretRoute.getArretId() + ",") ||
								entry.getKey().contains("," + arretRoute.getArretId() + ",")) && entry.getValue() > max) {
							max = entry.getValue();
							chaineBonChemin = entry.getKey();
						}
					}
					if (chaineBonChemin != null) {
						String lastStop = null;
						int stopSequence = 0;
						for (String stop : chaineBonChemin.split(",")) {
							if (stop != null && !"".equals(stop)) {
								lastStop = stop;
								if (stop.equals(arretRoute.getArretId())) {
									arretRoute.setSequence(stopSequence);
								}
								stopSequence++;
							}
						}
						arretRoute.setDirection(mapArrets.get(lastStop).getNom());
						if (!mapArretRouteByDirection.containsKey(arretRoute.getDirection())) {
							mapArretRouteByDirection.put(arretRoute.getDirection(), new ArrayList<ArretRoute>());
						}
						mapArretRouteByDirection.get(arretRoute.getDirection()).add(arretRoute);
					}
				}
			}
			System.out.println("Compte de la route " + routeId);
			for (Map.Entry<String, Integer> entry : mapCompteurTrip.entrySet()) {
				System.out.println(entry.getValue() + " : " + entry.getKey());
			}
			System.out.println("Arrets de la route " + routeId);
			for (Map.Entry<String, List<ArretRoute>> entry : mapArretRouteByDirection.entrySet()) {
				System.out.println("\tDirection " + entry.getKey());
				Collections.sort(entry.getValue(), new Comparator<ArretRoute>() {
					public int compare(ArretRoute o1, ArretRoute o2) {
						return o1.getSequence().compareTo(o2.getSequence());
					}
				});
				System.out.print("\t\t");
				for (ArretRoute arretRoute : entry.getValue()) {
					System.out.print(arretRoute.getArretId() + "(" + arretRoute.getSequence() + "), ");
				}
				System.out.println();
			}
		}

		System.out.println("Recherche d'arretRoute sans direction");
		for (ArretRoute arretRoute : listeArretsRoutes) {
			if (arretRoute.getDirection() == null || arretRoute.getSequence() == null) {
				System.err.println("ArretRoute sans direction ou sequence : ArretRoute[routeId=" + arretRoute.getRouteId() + ",arretId=" +
						arretRoute.getArretId() + ",direction=" + arretRoute.getDirection() + ",sequence=" + arretRoute.getSequence() + "]");
			}
		}

		System.out.println("Ecriture du fichier ArretRoute");
		moteurCsv.writeFile(new File(repertoire, "arret_route.txt"), listeArretsRoutes, ArretRoute.class);

		bufWriter = new BufferedWriter(new FileWriter(new File(repertoire, "last_update.txt")));

		bufWriter.write(SDF.format(lastUpdate));

		bufWriter.close();

		System.out.println("Création des zip");
		FileOutputStream dest = new FileOutputStream(new File(repertoire, "GTFSRennesPrincipal.zip"));
		ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));

		addFileToZip(new File(repertoire, "arret_route.txt"), out);
		addFileToZip(new File(repertoire, "calendar.txt"), out);
		addFileToZip(new File(repertoire, "routes.txt"), out);
		addFileToZip(new File(repertoire, "stops.txt"), out);
		out.close();

		for (File fileStopTime : repertoire.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.startsWith("stopTimes");
			}
		})) {
			String name = fileStopTime.getName();
			String newName = name.split("\\.")[0] + ".zip";
			dest = new FileOutputStream(new File(repertoire, newName));
			out = new ZipOutputStream(new BufferedOutputStream(dest));
			addFileToZip(fileStopTime, out);
			out.close();
		}


	}

	private static Calendrier rechercherCalendrier(List<Calendrier> calendriers, Calendrier calendrier) {
		int index = calendriers.indexOf(calendrier);
		if (index == -1) {
			return null;
		}
		return calendriers.get(index);
	}

	private static class IdentifiantHeureArret {
		private String routeId;
		private String stopId;
		private Integer heureDepart;

		private IdentifiantHeureArret(String routeId, String stopId, Integer heureDepart) {
			this.routeId = routeId;
			this.stopId = stopId;
			this.heureDepart = heureDepart;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			IdentifiantHeureArret that = (IdentifiantHeureArret) o;

			if (heureDepart != null ? !heureDepart.equals(that.heureDepart) : that.heureDepart != null) {
				return false;
			}
			if (routeId != null ? !routeId.equals(that.routeId) : that.routeId != null) {
				return false;
			}
			if (stopId != null ? !stopId.equals(that.stopId) : that.stopId != null) {
				return false;
			}

			return true;
		}

		@Override
		public int hashCode() {
			int result = routeId != null ? routeId.hashCode() : 0;
			result = 31 * result + (stopId != null ? stopId.hashCode() : 0);
			result = 31 * result + (heureDepart != null ? heureDepart.hashCode() : 0);
			return result;
		}
	}

	protected static void addFileToZip(File file, ZipOutputStream out) throws IOException {
		System.out.println("Adding: " + file);
		byte data[] = new byte[2048];
		FileInputStream fi = new FileInputStream(file);
		BufferedInputStream origin = new BufferedInputStream(fi, 2048);
		ZipEntry entry = new ZipEntry(file.getName());
		out.putNextEntry(entry);
		int count;
		while ((count = origin.read(data, 0, 2048)) != -1) {
			out.write(data, 0, count);
		}
		origin.close();
	}

	public static void main(String[] args) throws IOException, ParseException, ErreurMoteurCsv, IllegalAccessException {
		GetAndContructZip.getAndParseZipKeolis();
	}

}
