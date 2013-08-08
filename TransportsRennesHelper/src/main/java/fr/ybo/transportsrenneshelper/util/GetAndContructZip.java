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
 * 
 * Contributors:
 *     ybonnel - initial API and implementation
 */
package fr.ybo.transportsrenneshelper.util;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


/**
 * Gestion du zip Keolis.
 * @author ybonnel
 *
 */
public class GetAndContructZip {

	private String dateDemandee;

	public GetAndContructZip(String dateDemandee) {
		this.dateDemandee = dateDemandee;
	}

	/**
	 * Format de la date dans le nom du fichier.
	 */
	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMdd");
	
	/**
	 * Taille du format de la date.
	 */
	private static final int TRAILLE_FORMAT_DATE = 8;

	/**
	 * Nombre de jours de recherche en mode brute-force.
	 */
	private static final int NB_JOURS_RECHERCHE = 200;

	/**
	 * Chemin de fichier GTFS.
	 */
	private static final String URL_RELATIVE = "fileadmin/OpenDataFiles/GTFS/GTFS-";
	/**
	 * Url du site Keolis.
	 */
	private static final String URL_KEOLIS = "http://data.keolis-rennes.com/";
	/**
	 * Url de base pour le fichier.
	 */
	private static final String BASE_URL = URL_KEOLIS + URL_RELATIVE;
	/**
	 * URL de la page des données téléchargeables.
	 */
	private static final String URL_DONNEES_TELECHARGEABLES = URL_KEOLIS
			+ "fr/les-donnees/donnees-telechargeables.html";
	/**
	 * Extension du fichier.
	 */
	private static final String EXTENSION_URL = ".zip";


	/**
	 * Récupère la date de dernière mise à jour du fichier Keolis.
	 * @return la date trouvée.
	 */
	private Date getLastUpdate() {
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(URL_DONNEES_TELECHARGEABLES).openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.connect();
			BufferedReader bufReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			try {
				String ligne = bufReader.readLine();
				String chaineRecherchee = URL_RELATIVE;
				while (ligne != null) {
					if (ligne.contains(chaineRecherchee)) {
						String chaineDate = ligne.substring(
								ligne.indexOf(chaineRecherchee) + chaineRecherchee.length(),
								ligne.indexOf(chaineRecherchee) + chaineRecherchee.length() + TRAILLE_FORMAT_DATE);
						return SDF.parse(chaineDate);
					}
					ligne = bufReader.readLine();
				}
			} finally {
				bufReader.close();
			}
			return getLastUpdateBruteForce();
		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	/**
	 * Récupère la date de dernière mise à jour Keolis en mode brute-force.
	 * @return la date trouvée.
	 */
	private Date getLastUpdateBruteForce() {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		int nbJours = 0;
		while (nbJours < NB_JOURS_RECHERCHE) {
			try {
				HttpURLConnection connection = openHttpConnection(calendar.getTime());
				connection.getInputStream();
				return calendar.getTime();
			} catch (IOException ignore) {
				calendar.add(Calendar.DAY_OF_YEAR, -1);
				nbJours++;
			}
		}
		return null;
	}

	/**
	 * Récupère l'URL du fichier Keolis à partir d'une date donnée.
	 * @param date la date.
	 * @return l'url.
	 * @throws MalformedURLException ne doit pas arriver.
	 */
	private URL getUrlKeolisFromDate(Date date) throws MalformedURLException {
		return new URL(BASE_URL + SDF.format(date) + EXTENSION_URL);
	}

	/**
	 * Ouvre un connection http vers le fichier Keolis pour une date donnée.
	 * @param dateFileKeolis la date.
	 * @return la connection http.
	 */
	private HttpURLConnection openHttpConnection(Date dateFileKeolis) {
		try {
			HttpURLConnection connection = (HttpURLConnection) getUrlKeolisFromDate(dateFileKeolis).openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.connect();
			return connection;
		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

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
			+ "/GTFSRennes";
	/**
	 * Répertoire des fichiers gtfs.
	 */
	public static final String REPERTOIRE_GTFS = REPERTOIRE_SORTIE + "/GTFS";
	/**
	 * Répertoire de sortie des fichiers finaux.
	 */
	public static final String REPERTOIRE_OUT = REPERTOIRE_SORTIE + "/OUT";


	/**
	 * Récupère le zip Keolis et l'extrait dans le repertoire GTFS.
	 */
	public void getZipKeolis() {
		try {
			Date lastUpdate = dateDemandee == null ? getLastUpdate() : SDF.parse(dateDemandee);
			System.out.println("Date du fichier : " + SDF.format(lastUpdate));
			HttpURLConnection connection = openHttpConnection(lastUpdate);
			ZipInputStream zipInputStream = new ZipInputStream(connection.getInputStream());
			//ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream("C:/ybonnel/GTFS-20110118.zip"));
			try {
				File repertoire = new File(REPERTOIRE_GTFS);
				if (repertoire.exists()) {
					for (File file : repertoire.listFiles()) {
						if (!file.delete()) {
							System.err.println("Le fichier " + file.getName() + "n'a pas pu être effacé");
						}
					}
				} else {
					if (!repertoire.mkdirs()) {
						System.err.println("Le répertoire " + repertoire.getName() + "n'a pas pu être créé");
					}
				}
				copieFichierZip(zipInputStream, repertoire);
			} finally {
				zipInputStream.close();
			}

		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	/**
	 * Copie les fichiers d'un zip dans un répertoire.
	 * @param zipInputStream le zip.
	 * @param repertoire le répertoire de sortie.
	 * @throws IOException problème d'entrée/sortie.
	 */
	private void copieFichierZip(ZipInputStream zipInputStream, File repertoire) throws IOException {
		ZipEntry zipEntry = zipInputStream.getNextEntry();
		while (zipEntry != null) {
			System.out.println("Copie du fichier " + zipEntry.getName());
			File file = new File(repertoire, zipEntry.getName());
			BufferedReader bufReader = new BufferedReader(new InputStreamReader(zipInputStream));
			BufferedWriter bufWriter = new BufferedWriter(new FileWriter(file));
			try {
				String ligne = bufReader.readLine();
				while (ligne != null) {
					bufWriter.write(ligne);
					bufWriter.write('\n');
					ligne = bufReader.readLine();
				}
			} finally {
				bufWriter.close();
			}
			System.out.println("Fin de la copie du fichier " + zipEntry.getName());
			zipEntry = zipInputStream.getNextEntry();
		}
	}
	
	/**
	 * Taille du buffer.
	 */
	private static final int TRAILLE_BUFFER = 2048;

	/**
	 * Ajoute un fichier à un zip.
	 * @param file le fichier.
	 * @param out le zip.
	 */
	public static void addFileToZip(File file, ZipOutputStream out) {
		try {
			FileInputStream fi = new FileInputStream(file);
			BufferedInputStream origin = new BufferedInputStream(fi, TRAILLE_BUFFER);
			try {
				ZipEntry entry = new ZipEntry(file.getName());
				out.putNextEntry(entry);
				byte[] data = new byte[TRAILLE_BUFFER];
				int count = origin.read(data, 0, TRAILLE_BUFFER);
				while (count != -1) {
					out.write(data, 0, count);
					count = origin.read(data, 0, TRAILLE_BUFFER);
				}
			} finally {
				origin.close();
			}
		} catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

}
