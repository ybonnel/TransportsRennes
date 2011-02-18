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

package fr.ybo.transportsrennes.keolis.gtfs.files;

import fr.ybo.transportsrennes.TransportsRennesApplication;
import fr.ybo.transportsrennes.keolis.KeolisException;
import fr.ybo.transportsrennes.keolis.gtfs.database.DataBaseException;
import fr.ybo.transportsrennes.keolis.gtfs.database.DataBaseHelper;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Horaire;
import fr.ybo.transportsrennes.keolis.gtfs.moteur.MoteurCsv;
import fr.ybo.transportsrennes.keolis.gtfs.moteur.MoteurCsvException;
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

	private static final String URL_BASE = "http://yanbonnel.perso.sfr.fr/GTFSRennes/1.2.0/";
	private static final String URL_STOP_TIMES = URL_BASE + "horaires_";
	private static final String URL_LAST_UPDATE = URL_BASE + "last_update.txt";
	private static final String URL_ZIP_PRINCIPALE = URL_BASE + "GTFSRennesPrincipal.zip";

	private static HttpURLConnection openConnectionForStopTime(String ligneId) throws GestionFilesException {

		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(URL_STOP_TIMES + ligneId + ".zip").openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.connect();
			return connection;
		} catch (IOException ioException) {
			throw new GestionFilesException(ioException);
		}
	}

	@SuppressWarnings({"IOResourceOpenedButNotSafelyClosed"})
	public static void chargeLigne(MoteurCsv moteurCsv, String ligneId, DataBaseHelper dataBaseHelper) {
		try {
			HttpURLConnection connection = openConnectionForStopTime(ligneId);
			ZipInputStream zipInputStream = new ZipInputStream(connection.getInputStream());
			zipInputStream.getNextEntry();
			BufferedReader bufReader = new BufferedReader(new InputStreamReader(zipInputStream), 8 << 10);
			moteurCsv.parseFileAndInsert(bufReader, Horaire.class, dataBaseHelper, ligneId);
			connection.disconnect();
		} catch (Exception exception) {
			throw new GestionFilesException(exception);
		}

	}

	@SuppressWarnings({"IOResourceOpenedButNotSafelyClosed"})
	public static void getAndParseZipKeolis(MoteurCsv moteur) throws GestionFilesException, MoteurCsvException, DataBaseException {
		try {
			HttpURLConnection connection = openHttpConnection();
			ZipInputStream zipInputStream = new ZipInputStream(connection.getInputStream());
			ZipEntry zipEntry = zipInputStream.getNextEntry();
			while (zipEntry != null) {
				LOG_YBO.debug("Debut du traitement du fichier " + zipEntry.getName());
				BufferedReader bufReader = new BufferedReader(new InputStreamReader(zipInputStream), 8 << 10);
				try {
					moteur.nouveauFichier(zipEntry.getName(), bufReader.readLine());
					TransportsRennesApplication.getDataBaseHelper().beginTransaction();
					String ligne = bufReader.readLine();
					while (ligne != null) {
						TransportsRennesApplication.getDataBaseHelper().insert(moteur.creerObjet(ligne));
						ligne = bufReader.readLine();
					}
				} finally {
					TransportsRennesApplication.getDataBaseHelper().endTransaction();
				}
				LOG_YBO.debug("Fin du traitement du fichier " + zipEntry.getName());
				zipEntry = zipInputStream.getNextEntry();
			}
			TransportsRennesApplication.getDataBaseHelper().close();
			connection.disconnect();
			LOG_YBO.debug("Fin getAndParseZipKeolis.");
		} catch (IOException e) {
			throw new GestionFilesException(e);
		}
	}


	public static Date getLastUpdate() {
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(URL_LAST_UPDATE).openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.connect();
			Date date;
			BufferedReader bufReader = new BufferedReader(new InputStreamReader(connection.getInputStream()), 100);
			try {
				date = SDF.parse(bufReader.readLine());
			} finally {
				bufReader.close();
			}
			connection.disconnect();
			return date;
		} catch (Exception exception) {
			throw new KeolisException("Erreur lors de la récupération du fichier last_update", exception);
		}
	}

	private static HttpURLConnection openHttpConnection() throws GestionFilesException {
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(URL_ZIP_PRINCIPALE).openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.connect();
			return connection;
		} catch (IOException ioException) {
			throw new GestionFilesException(ioException);
		}
	}

	private GestionZipKeolis() {
	}

}
