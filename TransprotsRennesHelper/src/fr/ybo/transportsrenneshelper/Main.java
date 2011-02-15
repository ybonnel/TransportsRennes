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


import fr.ybo.transportsrenneshelper.generateurmodele.Generateur;
import fr.ybo.transportsrenneshelper.gtfs.compression.CompressionTripAndCalendar;
import fr.ybo.transportsrenneshelper.gtfs.gestionnaire.GestionnaireGtfs;
import fr.ybo.transportsrenneshelper.util.GetAndContructZip;

import java.io.File;

@SuppressWarnings({"UseOfSystemOutOrSystemErr", "WeakerAccess"})
public class Main {

	private Main() {
	}

	@SuppressWarnings({"UnusedParameters"})
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		GetAndContructZip getAndContructZip = new GetAndContructZip();
		getAndContructZip.getZipKeolis();
		GestionnaireGtfs.initInstance(new File(GetAndContructZip.REPERTOIRE_GTFS));
		System.out.println("Avant compression : ");
		afficheCompteurs();
		CompressionTripAndCalendar compressionTripAndCalendar = new CompressionTripAndCalendar();
		compressionTripAndCalendar.compressTripsAndCalendars();
		compressionTripAndCalendar.replaceTripGenereCalendarAndCompressStopTimes();
		System.out.println("Après compression : ");
		afficheCompteurs();
		long timeElapsed = System.currentTimeMillis() - startTime;
		Generateur generateur = new Generateur();
		generateur.remplirArrets();
		generateur.remplirCalendrier();
		generateur.remplirDirections();
		generateur.remplirHoraires();
		generateur.remplirLignes();
		generateur.remplirTrajets();
		generateur.remplirArretRoutes();
		generateur.remplirCorrespondance();
		generateur.genererFichiers(new File(GetAndContructZip.REPERTOIRE_OUT));
		generateur.rechercherPointsInterets();
		System.out.println("Fin de la génération des fichiers pour le mobile : " + timeElapsed + " ms");
	}

	private static void afficheCompteurs() {
		System.out.println("\tNombre de Calendars : " + GestionnaireGtfs.getInstance().getMapCalendars().size());
		System.out.println("\tNombre de StopTimes : " + GestionnaireGtfs.getInstance().getMapStopTimes().size());
		System.out.println("\tNombre de Routes : " + GestionnaireGtfs.getInstance().getMapRoutes().size());
		System.out.println("\tNombre de Stops : " + GestionnaireGtfs.getInstance().getMapStops().size());
		System.out.println("\tNombre de Trips : " + GestionnaireGtfs.getInstance().getMapTrips().size());
	}

}
