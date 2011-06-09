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
import java.io.IOException;

import fr.ybo.transportsbordeauxhelper.gtfs.GestionnaireGtfs;

/**
 * Classe réalisant l'enchènement des traitements.
 * @author ybonnel
 *
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

	/**
	 * Constructeur privé pour empécher l'instanciation.
	 */
	private Main() {
	}

	/**
	 * Main.
	 * @param args innutile.
	 * @throws IOException problème d'entrée/sortie.
	 */
	public static void main(String[] args) throws IOException {
		genereGtfs(false);
	}
	/**
	 * Traitement principale.
	 * @param toGtfs si true, on génère du GTFS, sinon on génère dans l'autre format.
	 * @throws IOException problème d'entrée/sortie.
	 */
	private static void genereGtfs(boolean toGtfs) throws IOException {
		long startTime = System.currentTimeMillis();
		GestionnaireGtfs.getInstance().optimizeIds();
		System.out.println("Volume : ");
		afficheCompteurs();
		if (toGtfs) {
			genereGtfsOptimises();
		} else {
			Generateur generateur = new Generateur();
			generateur.remplirArrets();
			generateur.remplirCalendrier();
			generateur.remplirCalendrierException();
			generateur.remplirDirections();
			generateur.remplirHoraires();
			generateur.remplirLignes();
			generateur.remplirTrajets();
			generateur.remplirArretRoutes();
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
	 * @throws IOException problème d'entrée/sortie.
	 */
	private static void genereGtfsOptimises() throws IOException {

	}

}
