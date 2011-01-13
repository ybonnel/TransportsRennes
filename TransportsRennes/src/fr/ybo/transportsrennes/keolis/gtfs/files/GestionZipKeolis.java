/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package fr.ybo.transportsrennes.keolis.gtfs.files;

import fr.ybo.transportsrennes.TransportsRennesApplication;
import fr.ybo.transportsrennes.keolis.ErreurKeolis;
import fr.ybo.transportsrennes.keolis.gtfs.database.DataBaseException;
import fr.ybo.transportsrennes.keolis.gtfs.database.DataBaseHelper;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Horaire;
import fr.ybo.transportsrennes.keolis.gtfs.moteur.ErreurMoteurCsv;
import fr.ybo.transportsrennes.keolis.gtfs.moteur.MoteurCsv;
import fr.ybo.transportsrennes.util.LogYbo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class GestionZipKeolis {

	private static final LogYbo LOG_YBO = new LogYbo(GestionZipKeolis.class);
	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMdd");

	private final static String URL_BASE = "http://yanbonnel.perso.sfr.fr/GTFSRennes/1.0.8/";
	private final static String URL_STOP_TIMES = URL_BASE + "horaires_";
	private final static String URL_LAST_UPDATE = URL_BASE + "last_update.txt";
	private final static String URL_ZIP_PRINCIPALE = URL_BASE + "GTFSRennesPrincipal.zip";

	private static HttpURLConnection openConnectionForStopTime(String ligneId) throws ErreurGestionFiles {

		try {
			final HttpURLConnection connection = (HttpURLConnection) new URL(URL_STOP_TIMES + ligneId + ".zip").openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.connect();
			return connection;
		} catch (final IOException ioException) {
			throw new ErreurGestionFiles(ioException);
		}
	}

	public static void chargeLigne(MoteurCsv moteurCsv, String ligneId, DataBaseHelper dataBaseHelper) {
		try {
			HttpURLConnection connection = openConnectionForStopTime(ligneId);
			ZipInputStream zipInputStream = new ZipInputStream(connection.getInputStream());
			zipInputStream.getNextEntry();
			BufferedReader bufReader = new BufferedReader(new InputStreamReader(zipInputStream), 8 * 1024);
			moteurCsv.parseFileAndInsert(bufReader, Horaire.class, dataBaseHelper, ligneId);

		} catch (Exception exception) {
			throw new ErreurGestionFiles(exception);
		}

	}

	public static void getAndParseZipKeolis(final MoteurCsv moteur)
			throws ErreurGestionFiles, ErreurMoteurCsv, DataBaseException {
		try {
			final HttpURLConnection connection = openHttpConnection();
			final ZipInputStream zipInputStream = new ZipInputStream(connection.getInputStream());
			ZipEntry zipEntry;
			String ligne;
			BufferedReader bufReader;
			while ((zipEntry = zipInputStream.getNextEntry()) != null) {
				LOG_YBO.debug("Debut du traitement du fichier " + zipEntry.getName());
				bufReader = new BufferedReader(new InputStreamReader(zipInputStream), 8 * 1024);
				moteur.nouveauFichier(zipEntry.getName(), bufReader.readLine());
				try {
					TransportsRennesApplication.getDataBaseHelper().beginTransaction();
					while ((ligne = bufReader.readLine()) != null) {
						TransportsRennesApplication.getDataBaseHelper().insert(moteur.creerObjet(ligne));
					}
				} finally {
					TransportsRennesApplication.getDataBaseHelper().endTransaction();
				}
				LOG_YBO.debug("Fin du traitement du fichier " + zipEntry.getName());
			}
			TransportsRennesApplication.getDataBaseHelper().close();
			connection.disconnect();
			LOG_YBO.debug("Fin getAndParseZipKeolis.");
		} catch (final IOException e) {
			throw new ErreurGestionFiles(e);
		}
	}


	public static Date getLastUpdate() {
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(URL_LAST_UPDATE).openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.connect();
			BufferedReader bufReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			Date date = SDF.parse(bufReader.readLine());
			bufReader.close();
			connection.disconnect();
			return date;
		} catch (Exception exception) {
			throw new ErreurKeolis("Erreur lors de la récupération du fichier last_update", exception);
		}
	}

	private static HttpURLConnection openHttpConnection() throws ErreurGestionFiles {
		try {
			final HttpURLConnection connection = (HttpURLConnection) new URL(URL_ZIP_PRINCIPALE).openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.connect();
			return connection;
		} catch (final IOException ioException) {
			throw new ErreurGestionFiles(ioException);
		}
	}

	private GestionZipKeolis() {
	}

}
