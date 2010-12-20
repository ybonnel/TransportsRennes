package fr.ybo.transportsrenneshelper;

import fr.ybo.transportsrennes.keolis.gtfs.modele.ArretRoute;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Route;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Trip;
import fr.ybo.transportsrennes.keolis.gtfs.moteur.ErreurMoteurCsv;
import fr.ybo.transportsrennes.keolis.gtfs.moteur.MoteurCsv;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by IntelliJ IDEA.
 * User: Steph
 * Date: 20/12/10
 * Time: 14:14
 * To change this template use File | Settings | File Templates.
 */
public class GetAndContructZip {
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMdd");

    private static final int NB_JOURS_RECHERCHE = 200;

    private static final String URL_RELATIVE = "fileadmin/OpenDataFiles/GTFS/GTFS-";
    private static final String URL_KEOLIS = "http://data.keolis-rennes.com/";
    private static final String BASE_URL = URL_KEOLIS + URL_RELATIVE;
    private static final String URL_DONNEES_TELECHARGEABLES = URL_KEOLIS + "fr/les-donnees/donnees-telechargeables.html";
    private static final String EXTENSION_URL = ".zip";


    private static BufferedWriter getBufferedWriterFromFile(final File file) throws FileNotFoundException {
        return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)), 8 * 1024);
    }

    public static Date getLastUpdate() throws IOException, ProtocolException, ParseException {

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

    public static void getAndParseZipKeolis() throws ParseException, IOException, ErreurMoteurCsv {
        Date lastUpdate = getLastUpdate();
        final HttpURLConnection connection = openHttpConnection(lastUpdate);
        final ZipInputStream zipInputStream = new ZipInputStream(connection.getInputStream());
        ZipEntry zipEntry;
        String ligne;
        BufferedReader bufReader;
        File repertoire = new File(REPERTOIRE_SORTIE);
        if (repertoire.exists()) {
            for (File file : repertoire.listFiles()) {
                file.delete();
            }
        } else {
            repertoire.mkdir();
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
            bufWriter = new BufferedWriter(new FileWriter(new File(
                    repertoire, "stopTimes" + route.getIdWithoutSpecCar() + ".txt")));
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
        String tripId;
        String stopId;
        String[] champs;
        String routeId;
        String direction;
        Set<String> stops;
        Map<String, Set<String>> mapRoutes = new HashMap<String, Set<String>>();
        ArretRoute arretRoute;
        List<ArretRoute> listeArretsRoutes = new ArrayList<ArretRoute>();
        while ((ligne = bufReader.readLine()) != null) {
            champs = ligne.split(",");
            tripId = champs[0];
            stopId = champs[1];
            if (mapTripIdFichier.containsKey(tripId)) {
                routeId = mapTrips.get(tripId).getRouteId();
                direction = mapTrips.get(tripId).getHeadSign();
                if (!mapRoutes.containsKey(routeId)) {
                    mapRoutes.put(routeId, new HashSet<String>());
                }
                stops = mapRoutes.get(routeId);
                if (!stops.contains(stopId)) {
                    stops.add(stopId);
                    arretRoute = new ArretRoute();
                    arretRoute.setRouteId(routeId);
                    arretRoute.setArretId(stopId);
                    arretRoute.setDirection(direction);
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

        System.out.println("Ecriture du fichier arret_route.txt");

        bufWriter = new BufferedWriter(new FileWriter(new File(repertoire, "arret_route.txt")));

        bufWriter.write("stop_id,route_id,direction\n");

        for (ArretRoute arretRouteToWrite : listeArretsRoutes) {
            bufWriter.write(arretRouteToWrite.getArretId());
            bufWriter.write(',');
            bufWriter.write(arretRouteToWrite.getRouteId());
            bufWriter.write(',');
            bufWriter.write(arretRouteToWrite.getDirection());
            bufWriter.write('\n');
        }

        bufWriter.close();

        System.out.println("Fin de l'ecriture du fichier arret_route.txt");

        bufWriter = new BufferedWriter(new FileWriter(new File(repertoire, "last_update.txt")));

        bufWriter.write(SDF.format(lastUpdate));

        bufWriter.close();
        ;

        System.out.println("Création des zip");
        FileOutputStream dest = new FileOutputStream(new File(repertoire, "GTFSRennesPincipal.zip"));
        ZipOutputStream out = new ZipOutputStream(new
                BufferedOutputStream(dest));

        addFileToZip(new File(repertoire, "agency.txt"), out);
        addFileToZip(new File(repertoire, "arret_route.txt"), out);
        addFileToZip(new File(repertoire, "calendar.txt"), out);
        addFileToZip(new File(repertoire, "routes.txt"), out);
        addFileToZip(new File(repertoire, "stops.txt"), out);
        addFileToZip(new File(repertoire, "trips.txt"), out);
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
            ;
        }


    }

    protected static void addFileToZip(File file, ZipOutputStream out) throws IOException {
        System.out.println("Adding: " + file);
        byte data[] = new byte[2048];
        FileInputStream fi = new
                FileInputStream(file);
        BufferedInputStream origin = new
                BufferedInputStream(fi, 2048);
        ZipEntry entry = new ZipEntry(file.getName());
        out.putNextEntry(entry);
        int count;
        while ((count = origin.read(data, 0,
                2048)) != -1) {
            out.write(data, 0, count);
        }
        origin.close();
    }

    public static void main(String[] args) throws IOException, ParseException, ErreurMoteurCsv {
        GetAndContructZip.getAndParseZipKeolis();
    }

}
