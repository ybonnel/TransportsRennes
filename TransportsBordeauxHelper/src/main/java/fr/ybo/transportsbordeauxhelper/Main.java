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
package fr.ybo.transportsbordeauxhelper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import fr.ybo.transportsbordeauxhelper.gtfs.GestionnaireGtfs;
import fr.ybo.transportsbordeauxhelper.gtfs.modele.Agency;
import fr.ybo.transportsbordeauxhelper.gtfs.modele.Calendar;
import fr.ybo.transportsbordeauxhelper.gtfs.modele.CalendarDates;
import fr.ybo.transportsbordeauxhelper.gtfs.modele.Route;
import fr.ybo.transportsbordeauxhelper.gtfs.modele.Stop;
import fr.ybo.transportsbordeauxhelper.gtfs.modele.StopTime;
import fr.ybo.transportsbordeauxhelper.gtfs.modele.Trip;
import fr.ybonnel.csvengine.CsvEngine;
import fr.ybonnel.csvengine.model.EngineParameters;

/**
 * Classe réalisant l'enchènement des traitements.
 *
 * @author ybonnel
 */
public final class Main {

    /**
     * Répertoire Data issue de la variable d'environnement YBO_DEV_DATA.
     */
    private static final String YBO_DEV_DATA = System.getenv("YBO_DEV_DATA");
    /**
     * Répertoire des datas par défaut.
     */
    private static final String YBO_DEV_DATA_DEFAULT = "/Users/ybonnel/dev/data";

    /**
     * Répertoire de travail.
     */
    private static final String REPERTOIRE_SORTIE = (YBO_DEV_DATA == null ? YBO_DEV_DATA_DEFAULT : YBO_DEV_DATA)
            + "/GTFSBordeaux";
    /**
     * Répertoire de sortie des fichiers finaux.
     */
    public static final String REPERTOIRE_OUT = REPERTOIRE_SORTIE + "/OUT";

    private static final int NB_JOURS_PREV = 90;

    /**
     * Constructeur privé pour empécher l'instanciation.
     */
    private Main() {
    }

    /**
     * Main.
     *
     * @param args innutile.
     * @throws IOException    problème d'entrée/sortie.
     * @throws ParseException
     */
    public static void main(String[] args) throws IOException, ParseException {
		genereGtfs(true, "20150613");
    }

    /**
     * Traitement principale.
     *
     * @param toGtfs si true, on génère du GTFS, sinon on génère dans l'autre
     *               format.
     * @throws IOException    problème d'entrée/sortie.
     * @throws ParseException
     */
    private static void genereGtfs(boolean toGtfs, String datePublication) throws IOException, ParseException {
        long startTime = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date dateDebut = sdf.parse(datePublication);
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTime(dateDebut);
        calendar.add(java.util.Calendar.DAY_OF_YEAR, NB_JOURS_PREV);
        Date dateDebutMax = calendar.getTime();
        String dateNextPublication = sdf.format(dateDebutMax);
        System.out.println("Date publication : " + dateDebut);
        System.out.println("Date next publication : " + dateDebutMax);
        GestionnaireGtfs.getInstance().optimizeIds();
        System.out.println("Avant compression : ");
        afficheCompteurs();
        if (toGtfs) {
            genereGtfsOptimises();
        } else {
            /*
                * CompressionTripAndCalendar compressionTripAndCalendar = new
                * CompressionTripAndCalendar();
                * compressionTripAndCalendar.compressTripsAndCalendars();
                * compressionTripAndCalendar
                * .replaceTripGenereCalendarAndCompressStopTimes();
                */
            System.out.println("Après compression : ");
            afficheCompteurs();
            Generateur generateur = new Generateur();
            generateur.remplirArrets(); //
            generateur.remplirCalendrier(); //
            generateur.remplirCalendrierException(); //
            generateur.remplirDirections(); //
            generateur.remplirHoraires(); //
            generateur.remplirLignes(); //
            generateur.remplirTrajets(); //
            generateur.remplirArretRoutes(); //
            generateur.priseEnCompteDatePublication(datePublication, dateNextPublication);
            System.out.println("Répertoire de sortie : " + new File(REPERTOIRE_OUT).getAbsolutePath());
            generateur.genererFichiers(new File(REPERTOIRE_OUT));
            generateur.rechercherPointsInterets();
        }
        long timeElapsed = System.currentTimeMillis() - startTime;
        System.out.println("Fin de la génération des fichiers pour le mobile : " + timeElapsed + " ms");

    }

    /**
     * Affiche les compteurs des données GTFS.
     */
    private static void afficheCompteurs() {
        System.out.println("\tNombre de Calendars : " + GestionnaireGtfs.getInstance().getCalendars().size());
        System.out.println("\tNombre de StopTimes : " + GestionnaireGtfs.getInstance().getStopTimes().size());
        System.out.println("\tNombre de Routes : " + GestionnaireGtfs.getInstance().getRoutes().size());
        System.out.println("\tNombre de Stops : " + GestionnaireGtfs.getInstance().getStops().size());
        System.out.println("\tNombre de Trips : " + GestionnaireGtfs.getInstance().getTrips().size());
    }

    /**
     * Génère un GTFS optimisé.
     *
     * @throws IOException problème d'entrée/sortie.
     */
    @SuppressWarnings("unchecked")
    private static void genereGtfsOptimises() throws IOException {

        File repertoireOut = new File(REPERTOIRE_OUT);


        CsvEngine moteurCsv = new CsvEngine(EngineParameters.createBuilder().setAddQuoteCar(false).build(), Agency.class, Calendar.class, CalendarDates.class,
                Route.class, Stop.class, Trip.class, StopTime.class);
        System.out.println("Génération de agency.txt");
        moteurCsv.writeFile(new FileWriter(new File(repertoireOut, "agency.txt")), GestionnaireGtfs.getInstance().getAgencies(),
                Agency.class);
        System.out.println("Génération de calendar_dates.txt");
        moteurCsv.writeFile(new FileWriter(new File(repertoireOut, "calendar_dates.txt")), GestionnaireGtfs.getInstance()
                .getCalendarsDates(), CalendarDates.class);
        System.out.println("Génération de calendar.txt");
        moteurCsv.writeFile(new FileWriter(new File(repertoireOut, "calendar.txt")), GestionnaireGtfs.getInstance().getCalendars()
                .values(), Calendar.class);
        System.out.println("Génération de routes.txt");
        moteurCsv.writeFile(new FileWriter(new File(repertoireOut, "routes.txt")), GestionnaireGtfs.getInstance().getRoutes().values(),
                Route.class);
        System.out.println("Génération de stops.txt");
        moteurCsv.writeFile(new FileWriter(new File(repertoireOut, "stops.txt")), GestionnaireGtfs.getInstance().getStops().values(),
                Stop.class);
        System.out.println("Génération de trips.txt");
        moteurCsv.writeFile(new FileWriter(new File(repertoireOut, "trips.txt")), GestionnaireGtfs.getInstance().getTrips().values(),
                Trip.class);
        System.out.println("Génération de stop_times.txt");
        moteurCsv.writeFile(new FileWriter(new File(repertoireOut, "stop_times.txt")), GestionnaireGtfs.getInstance().getStopTimes()
                .values(), StopTime.class);

    }
}
