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

package fr.ybo.transportsrenneshelper.util;


import fr.ybo.transportsrenneshelper.moteurcsv.MoteurCsvException;

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

@SuppressWarnings({"UseOfSystemOutOrSystemErr"})
public class GetAndContructZip {
	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMdd");

	private static final int NB_JOURS_RECHERCHE = 200;

	private static final String URL_RELATIVE = "fileadmin/OpenDataFiles/GTFS/GTFS-";
	private static final String URL_KEOLIS = "http://data.keolis-rennes.com/";
	private static final String BASE_URL = URL_KEOLIS + URL_RELATIVE;
	private static final String URL_DONNEES_TELECHARGEABLES = URL_KEOLIS + "fr/les-donnees/donnees-telechargeables.html";
	private static final String EXTENSION_URL = ".zip";


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
						String chaineDate = ligne.substring(ligne.indexOf(chaineRecherchee) + chaineRecherchee.length(),
								ligne.indexOf(chaineRecherchee) + chaineRecherchee.length() + 8);
						return SDF.parse(chaineDate);
					}
					ligne = bufReader.readLine();
				}
			} finally {
				bufReader.close();
			}
			return getLastUpdateBruteForce();
		} catch (Exception exception) {
			throw new MoteurCsvException(exception);
		}
	}

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

	private URL getUrlKeolisFromDate(Date date) throws MalformedURLException {
		return new URL(BASE_URL + SDF.format(date) + EXTENSION_URL);
	}

	private HttpURLConnection openHttpConnection(Date dateFileKeolis) {
		try {
			HttpURLConnection connection = (HttpURLConnection) getUrlKeolisFromDate(dateFileKeolis).openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.connect();
			return connection;
		} catch (Exception exception) {
			throw new MoteurCsvException(exception);
		}
	}

	private static final String REPERTOIRE_SORTIE = "/home/ybonnel/data/GTFSRennes";
	public static final String REPERTOIRE_GTFS = REPERTOIRE_SORTIE + "/GTFS";
	public static final String REPERTOIRE_OUT = REPERTOIRE_SORTIE + "/OUT";


	public void getZipKeolis() {
		try {
			Date lastUpdate = getLastUpdate();
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
			throw new MoteurCsvException(exception);
		}
	}

	@SuppressWarnings({"IOResourceOpenedButNotSafelyClosed"})
	private void copieFichierZip(ZipInputStream zipInputStream, File repertoire) throws IOException {
		ZipEntry zipEntry = zipInputStream.getNextEntry();
		while (zipEntry != null) {
			System.out.println("Copie du fichier " + zipEntry.getName());
			File file = new File(repertoire, zipEntry.getName());
			BufferedReader bufReader = new BufferedReader(new InputStreamReader(zipInputStream), 8 << 10);
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

	public static void addFileToZip(File file, ZipOutputStream out) {
		try {
			FileInputStream fi = new FileInputStream(file);
			BufferedInputStream origin = new BufferedInputStream(fi, 2048);
			try {
				ZipEntry entry = new ZipEntry(file.getName());
				out.putNextEntry(entry);
				byte[] data = new byte[2048];
				int count = origin.read(data, 0, 2048);
				while (count != -1) {
					out.write(data, 0, count);
					count = origin.read(data, 0, 2048);
				}
			} finally {
				origin.close();
			}
		} catch (IOException ioException) {
			throw new MoteurCsvException(ioException);
		}
	}

}
