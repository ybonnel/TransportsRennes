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
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
import fr.ybonnel.csvengine.CsvEngine;
import fr.ybonnel.csvengine.exception.CsvErrorsExceededException;
import fr.ybonnel.csvengine.model.EngineParameters;

/**
 * Classe réalisant l'enchènement des traitements.
 * 
 * @author ybonnel
 */
public final class Main {

	private static final boolean OPTIMIZE_CALENDARS = true;

	/**
	 * Constructeur privé pour empécher l'instanciation.
	 */
	private Main() {
	}

	/**
	 * Main.
	 * 
	 * @param args
	 *            innutile.
	 * @throws IOException
	 *             problème d'entrée/sortie.
	 */
	public static void main(String[] args) throws IOException, CsvErrorsExceededException {
		genereGtfs(true, OPTIMIZE_CALENDARS, "20150612");
		// genereParcoursBus("20120912");
	}

	private static void genereParcoursBus(String dateGtfs) {
		long startTime = System.currentTimeMillis();

		GetAndContructZip getAndContructZip = new GetAndContructZip(dateGtfs);
		getAndContructZip.getZipKeolis();
		GestionnaireGtfs.initInstance(new File(
				GetAndContructZip.REPERTOIRE_GTFS));

		GenerateurParcours generateur = new GenerateurParcours();
		generateur.genereParcours();
		System.out.println(generateur.getParcours());

		long timeElapsed = System.currentTimeMillis() - startTime;
		System.out
				.println("Fin de la génération des fichiers pour le mobile : "
						+ timeElapsed + " ms");
	}

	/**
	 * Traitement principale.
	 * 
	 * @param toGtfs
	 *            si true, on génère du GTFS, sinon on génère dans l'autre
	 *            format.
	 * @param date
	 *            date du gtfs.
	 * @throws IOException
	 *             problème d'entrée/sortie.
	 */
	private static void genereGtfs(boolean toGtfs, boolean optimizeCalendars,
			String date) throws IOException, CsvErrorsExceededException {
		long startTime = System.currentTimeMillis();
		GetAndContructZip getAndContructZip = new GetAndContructZip(date);
		getAndContructZip.getZipKeolis();
		GestionnaireGtfs.initInstance(new File(
				GetAndContructZip.REPERTOIRE_GTFS));
		System.out.println("Avant compression : ");
		afficheCompteurs();
		if (!toGtfs && optimizeCalendars) {
			CompressionTripAndCalendar compressionTripAndCalendar = new CompressionTripAndCalendar();
			compressionTripAndCalendar.compressTripsAndCalendars();
			compressionTripAndCalendar
					.replaceTripGenereCalendarAndCompressStopTimes();
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
			generateur.remplirCalendrierExceptions();
			generateur.remplirDirections();
			generateur.remplirHoraires();
			generateur.remplirLignes();
			generateur.remplirTrajets();
			generateur.remplirArretRoutes();
			generateur.remplirCorrespondance();
			generateur.genererFichiers(new File(
					GetAndContructZip.REPERTOIRE_OUT));
			generateur.rechercherPointsInterets();
		}
		System.out
				.println("Fin de la génération des fichiers pour le mobile : "
						+ timeElapsed + " ms");

	}

	/**
	 * Affiche les compteurs des données GTFS.
	 */
	private static void afficheCompteurs() {
		System.out.println("\tNombre de Calendars : "
				+ GestionnaireGtfs.getInstance().getMapCalendars().size());
		System.out.println("\tNombre de StopTimes : "
				+ GestionnaireGtfs.getInstance().getMapStopTimes().size());
		System.out.println("\tNombre de Routes : "
				+ GestionnaireGtfs.getInstance().getMapRoutes().size());
		System.out.println("\tNombre de Stops : "
				+ GestionnaireGtfs.getInstance().getMapStops().size());
		System.out.println("\tNombre de Trips : "
				+ GestionnaireGtfs.getInstance().getMapTrips().size());
	}

	/**
	 * Génère un GTFS optimisé.
	 * 
	 * @throws IOException
	 *             problème d'entrée/sortie.
	 */
	private static void genereGtfsOptimises() throws IOException, CsvErrorsExceededException {
		CsvEngine moteurCsv = GestionnaireGtfs.getInstance().getMoteurCsv();
		// agency on le garde
		// calendars
		// Ajout des données de métro.
		List<Stop> stops = new ArrayList<Stop>(GestionnaireGtfs.getInstance()
				.getMapStops().values());
		List<Calendar> calendars = new ArrayList<Calendar>(GestionnaireGtfs
				.getInstance().getMapCalendars().values());
		List<Route> routes = new ArrayList<Route>(GestionnaireGtfs
				.getInstance().getMapRoutes().values());
		List<StopTime> stopTimes = new ArrayList<StopTime>(GestionnaireGtfs
				.getInstance().getMapStopTimes().values());
		List<Trip> trips = new ArrayList<Trip>(GestionnaireGtfs.getInstance()
				.getMapTrips().values());

		moteurCsv.writeFile(new FileWriter(new File(GetAndContructZip.REPERTOIRE_OUT,
				"calendar.txt")), calendars, Calendar.class);
		// routes
		moteurCsv.writeFile(new FileWriter(new File(GetAndContructZip.REPERTOIRE_OUT,
				"routes.txt")), routes, Route.class);
		// stopTimes
		moteurCsv.writeFile(new FileWriter(new File(GetAndContructZip.REPERTOIRE_OUT,
				"stop_times.txt")), stopTimes, StopTime.class);
		// stops
		moteurCsv.writeFile(new FileWriter(new File(GetAndContructZip.REPERTOIRE_OUT,
				"stops.txt")), stops, Stop.class);
		// stops
		moteurCsv.writeFile(new FileWriter(new File(GetAndContructZip.REPERTOIRE_OUT,
				"trips.txt")), trips, Trip.class);

	}

}
