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
package fr.ybo.transportsrenneshelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.ybo.moteurcsv.MoteurCsv;
import fr.ybo.transportsrenneshelper.generateurmodele.Generateur;
import fr.ybo.transportsrenneshelper.generateurmodele.modele.HoraireMetro;
import fr.ybo.transportsrenneshelper.gtfs.compression.CompressionTripAndCalendar;
import fr.ybo.transportsrenneshelper.gtfs.gestionnaire.GestionnaireGtfs;
import fr.ybo.transportsrenneshelper.gtfs.modele.Calendar;
import fr.ybo.transportsrenneshelper.gtfs.modele.Route;
import fr.ybo.transportsrenneshelper.gtfs.modele.Stop;
import fr.ybo.transportsrenneshelper.gtfs.modele.StopTime;
import fr.ybo.transportsrenneshelper.gtfs.modele.Trip;
import fr.ybo.transportsrenneshelper.keolis.GetMetro;
import fr.ybo.transportsrenneshelper.keolis.modele.MetroStation;
import fr.ybo.transportsrenneshelper.parcours.GenerateurParcours;
import fr.ybo.transportsrenneshelper.util.GetAndContructZip;

/**
 * Classe réalisant l'enchènement des traitements.
 *
 * @author ybonnel
 */
public final class Main {

    /**
     * Constructeur privé pour empécher l'instanciation.
     */
    private Main() {
    }

    /**
     * Main.
     *
     * @param args innutile.
     * @throws IOException problème d'entrée/sortie.
     */
    public static void main(String[] args) throws IOException {
		genereGtfs(false, "20121018");
		//genereParcoursBus("20120912");
    }
    
    private static void genereParcoursBus(String dateGtfs) {
    	long startTime = System.currentTimeMillis();
    	
        GetAndContructZip getAndContructZip = new GetAndContructZip(dateGtfs);
		getAndContructZip.getZipKeolis();
        GestionnaireGtfs.initInstance(new File(GetAndContructZip.REPERTOIRE_GTFS));
        
        GenerateurParcours generateur = new GenerateurParcours();
        generateur.genereParcours();
        System.out.println(generateur.getParcours());

        long timeElapsed = System.currentTimeMillis() - startTime;
        System.out.println("Fin de la génération des fichiers pour le mobile : " + timeElapsed + " ms");
    }

    /**
     * Traitement principale.
     *
     * @param toGtfs si true, on génère du GTFS, sinon on génère dans l'autre format.
     * @param date   date du gtfs.
     * @throws IOException problème d'entrée/sortie.
     */
    private static void genereGtfs(boolean toGtfs, String date) throws IOException {
        long startTime = System.currentTimeMillis();
        GetAndContructZip getAndContructZip = new GetAndContructZip(date);
		getAndContructZip.getZipKeolis();
        GestionnaireGtfs.initInstance(new File(GetAndContructZip.REPERTOIRE_GTFS));
        System.out.println("Avant compression : ");
        afficheCompteurs();
        if (!toGtfs) {
            CompressionTripAndCalendar compressionTripAndCalendar = new CompressionTripAndCalendar();
            compressionTripAndCalendar.compressTripsAndCalendars();
            compressionTripAndCalendar.replaceTripGenereCalendarAndCompressStopTimes();
        }

        System.out.println("Après compression : ");
        afficheCompteurs();
        long timeElapsed = System.currentTimeMillis() - startTime;
        if (toGtfs) {
            genereGtfsOptimises();
        } else {
            Generateur generateur = new Generateur();
            generateur.remplirArrets();
            generateur.remplirCalendrier();
            generateur.remplirDirections();
            generateur.remplirHoraires();
            generateur.remplirLignes();
            generateur.remplirTrajets();
            generateur.remplirArretRoutes();
            generateur.remplirCorrespondance();
            generateur.ajoutDonnesMetro();
            generateur.genererFichiers(new File(GetAndContructZip.REPERTOIRE_OUT));
            generateur.rechercherPointsInterets();
        }
        System.out.println("Fin de la génération des fichiers pour le mobile : " + timeElapsed + " ms");

    }

    /**
     * Affiche les compteurs des données GTFS.
     */
    private static void afficheCompteurs() {
        System.out.println("\tNombre de Calendars : " + GestionnaireGtfs.getInstance().getMapCalendars().size());
        System.out.println("\tNombre de StopTimes : " + GestionnaireGtfs.getInstance().getMapStopTimes().size());
        System.out.println("\tNombre de Routes : " + GestionnaireGtfs.getInstance().getMapRoutes().size());
        System.out.println("\tNombre de Stops : " + GestionnaireGtfs.getInstance().getMapStops().size());
        System.out.println("\tNombre de Trips : " + GestionnaireGtfs.getInstance().getMapTrips().size());
    }

    /**
     * @param calendar un calendar.
     * @return true si c'est un calendar "semaine" (tous les jours sauf le dimanche).
     */
    private static boolean isSemaine(Calendar calendar) {
        return (calendar.lundi && calendar.mardi && calendar.mercredi && calendar.jeudi && calendar.vendredi
                && calendar.samedi && !calendar.dimanche);
    }

    /**
     * @param calendar un calendar.
     * @return true si c'est un calendar "semaine" (tous les jours sauf le
     *         dimanche).
     */
    private static boolean isDimanche(Calendar calendar) {
        return (!calendar.lundi && !calendar.mardi && !calendar.mercredi && !calendar.jeudi && !calendar.vendredi
                && !calendar.samedi && calendar.dimanche);
    }

    /**
     * Génère un GTFS optimisé.
     *
     * @throws IOException problème d'entrée/sortie.
     */
    private static void genereGtfsOptimises() throws IOException {
        MoteurCsv moteurCsv = GestionnaireGtfs.getInstance().getMoteurCsv();
        // agency on le garde
        // calendars
        // Ajout des données de métro.
        List<Stop> stops = new ArrayList<Stop>(GestionnaireGtfs.getInstance().getMapStops().values());
        List<Calendar> calendars = new ArrayList<Calendar>(GestionnaireGtfs.getInstance().getMapCalendars().values());
        List<Route> routes = new ArrayList<Route>(GestionnaireGtfs.getInstance().getMapRoutes().values());
        List<StopTime> stopTimes = new ArrayList<StopTime>(GestionnaireGtfs.getInstance().getMapStopTimes().values());
        List<Trip> trips = new ArrayList<Trip>(GestionnaireGtfs.getInstance().getMapTrips().values());
        String headSign1 = "J.F. Kennedy";
        String headSign2 = "La Poterie";
        /* Ajout des stops */
        for (MetroStation station : GetMetro.getStations()) {
            Stop stop1 = new Stop();
            Stop stop2 = new Stop();
            stop1.id = station.getId() + "1";
            stop2.id = station.getId() + "2";
            stop1.nom = station.getName();
            stop2.nom = station.getName();
            stop1.latitude = station.getLatitude();
            stop2.latitude = station.getLatitude();
            stop1.longitude = station.getLongitude();
            stop2.longitude = station.getLongitude();
            stops.add(stop1);
            stops.add(stop2);
        }
        int semaineId = 0;
        int dimancheId = 0;
        int maxCalendarId = 0;
        // Ajout des calendrier.
        for (Calendar calendar : calendars) {
            if (isSemaine(calendar)) {
                semaineId = Integer.parseInt(calendar.id);
            }
            if (isDimanche(calendar)) {
                dimancheId = Integer.parseInt(calendar.id);
            }
            if (Integer.parseInt(calendar.id) > maxCalendarId) {
                maxCalendarId = Integer.parseInt(calendar.id);
            }
        }
        // Calendrier pour le métro.
        // Si les calendrier n'ont pas été trouvé on les crée.
        if (semaineId == 0) {
            Calendar calendrier = new Calendar();
            calendrier.id = Integer.toString(++maxCalendarId);
            calendrier.lundi = true;
            calendrier.mardi = true;
            calendrier.mercredi = true;
            calendrier.jeudi = true;
            calendrier.vendredi = true;
            calendrier.samedi = true;
            calendrier.dimanche = false;
            calendrier.startDate = calendars.get(0).startDate;
            calendrier.endDate = calendars.get(0).endDate;
            calendars.add(calendrier);
            semaineId = Integer.parseInt(calendrier.id);
        }
        if (dimancheId == 0) {
            Calendar calendrier = new Calendar();
            calendrier.id = Integer.toString(++maxCalendarId);
            calendrier.lundi = false;
            calendrier.mardi = false;
            calendrier.mercredi = false;
            calendrier.jeudi = false;
            calendrier.vendredi = false;
            calendrier.samedi = false;
            calendrier.dimanche = true;
            calendrier.startDate = calendars.get(0).startDate;
            calendrier.endDate = calendars.get(0).endDate;
            calendars.add(calendrier);
            dimancheId = Integer.parseInt(calendrier.id);
        }
        // Ajout de la Route

        Route ligneMetro = new Route();
        ligneMetro.id = "a";
        ligneMetro.nomCourt = "a";
        ligneMetro.nomLong = "La Poterie <> J.F. Kennedy";
        ligneMetro.agencyId = "1";
        ligneMetro.type = "1";
        routes.add(ligneMetro);

        int tripIdMax = 0;
        for (Trip trip : trips) {
            if (tripIdMax < Integer.parseInt(trip.id)) {
                tripIdMax = Integer.parseInt(trip.id);
            }
        }
        tripIdMax++;

        Map<String, Trip> trajetMetro = new HashMap<String, Trip>();
        List<Class<?>> clazz = new ArrayList<Class<?>>();
        clazz.add(HoraireMetro.class);
        MoteurCsv moteurMetro = new MoteurCsv(clazz);
        List<StopTime> horairesMetro = new ArrayList<StopTime>();
        for (HoraireMetro horaireMetro : moteurMetro.parseInputStream(
                Main.class.getResourceAsStream("/fr/ybo/transportsrenneshelper/gtfs/horaires_metro_semaine.txt"),
                HoraireMetro.class)) {
            for (StopTime horaire : horaireMetro.getStopTime(tripIdMax, semaineId, headSign1, headSign2)) {
                horairesMetro.add(horaire);
                if (!trajetMetro.containsKey(horaire.tripId)) {
                    trajetMetro.put(horaire.tripId, horaire.trip);
                }
            }
            tripIdMax += 2;
        }

        for (HoraireMetro horaireMetro : moteurMetro.parseInputStream(Generateur.class
                .getResourceAsStream("/fr/ybo/transportsrenneshelper/gtfs/horaires_metro_dimanche" + ".txt"),
                HoraireMetro.class)) {
            for (StopTime horaire : horaireMetro.getStopTime(tripIdMax, dimancheId, headSign1, headSign2)) {
                horairesMetro.add(horaire);
                if (!trajetMetro.containsKey(horaire.tripId)) {
                    trajetMetro.put(horaire.tripId, horaire.trip);
                }
            }
            tripIdMax += 2;
        }
        trips.addAll(trajetMetro.values());
        stopTimes.addAll(horairesMetro);

        moteurCsv.writeFile(new File(GetAndContructZip.REPERTOIRE_OUT, "calendar.txt"), calendars, Calendar.class);
        // routes
        moteurCsv.writeFile(new File(GetAndContructZip.REPERTOIRE_OUT, "routes.txt"), routes, Route.class);
        // stopTimes
        moteurCsv.writeFile(new File(GetAndContructZip.REPERTOIRE_OUT, "stop_times.txt"), stopTimes, StopTime.class);
        // stops
        moteurCsv.writeFile(new File(GetAndContructZip.REPERTOIRE_OUT, "stops.txt"), stops, Stop.class);
        // stops
        moteurCsv.writeFile(new File(GetAndContructZip.REPERTOIRE_OUT, "trips.txt"), trips, Trip.class);

    }

}
