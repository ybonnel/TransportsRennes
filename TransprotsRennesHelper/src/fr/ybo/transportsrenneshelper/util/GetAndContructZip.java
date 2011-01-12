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

package fr.ybo.transportsrenneshelper.util;


import fr.ybo.transportsrenneshelper.moteurcsv.ErreurMoteurCsv;

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

public class GetAndContructZip {
	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMdd");

	private static final int NB_JOURS_RECHERCHE = 200;

	private static final String URL_RELATIVE = "fileadmin/OpenDataFiles/GTFS/GTFS-";
	private static final String URL_KEOLIS = "http://data.keolis-rennes.com/";
	private static final String BASE_URL = URL_KEOLIS + URL_RELATIVE;
	private static final String URL_DONNEES_TELECHARGEABLES = URL_KEOLIS + "fr/les-donnees/donnees-telechargeables.html";
	private static final String EXTENSION_URL = ".zip";


	public Date getLastUpdate() {
		try {
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
		} catch (Exception exception) {
			throw new ErreurMoteurCsv(exception);
		}
	}

	public Date getLastUpdateBruteForce() {
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

	private URL getUrlKeolisFromDate(final Date date) throws MalformedURLException {
		return new URL(BASE_URL + SDF.format(date) + EXTENSION_URL);
	}

	private HttpURLConnection openHttpConnection(final Date dateFileKeolis) {
		try {
			final HttpURLConnection connection = (HttpURLConnection) getUrlKeolisFromDate(dateFileKeolis).openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.connect();
			return connection;
		} catch (Exception exception) {
			throw new ErreurMoteurCsv(exception);
		}
	}

	public final static String REPERTOIRE_SORTIE = "C:/ybonnel/GTFSRennes";
	public final static String REPERTOIRE_GTFS = REPERTOIRE_SORTIE + "/GTFS";
	public final static String REPERTOIRE_OUT = REPERTOIRE_SORTIE + "/OUT";


	public void getZipKeolis() {
		try {
			Date lastUpdate = getLastUpdate();
			HttpURLConnection connection = openHttpConnection(lastUpdate);
			ZipInputStream zipInputStream = new ZipInputStream(connection.getInputStream());
			ZipEntry zipEntry;
			String ligne;
			BufferedReader bufReader;
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
			while ((zipEntry = zipInputStream.getNextEntry()) != null) {
				System.out.println("Copie du fichier " + zipEntry.getName());
				bufReader = new BufferedReader(new InputStreamReader(zipInputStream), 8 * 1024);
				File file = new File(repertoire, zipEntry.getName());
				BufferedWriter bufWriter = new BufferedWriter(new FileWriter(file));
				while ((ligne = bufReader.readLine()) != null) {
					bufWriter.write(ligne);
					bufWriter.write('\n');
				}
				bufWriter.close();
				System.out.println("Fin de la copie du fichier " + zipEntry.getName());
			}
		} catch (Exception exception) {
			throw new ErreurMoteurCsv(exception);
		}


	}

	public static void addFileToZip(File file, ZipOutputStream out) {
		try {
			byte data[] = new byte[2048];
			FileInputStream fi = new FileInputStream(file);
			BufferedInputStream origin = new BufferedInputStream(fi, 2048);
			ZipEntry entry = new ZipEntry(file.getName());
			out.putNextEntry(entry);
			int count;
			while ((count = origin.read(data, 0, 2048)) != -1) {
				out.write(data, 0, count);
			}
			origin.close();
		} catch (IOException ioException) {
			throw new ErreurMoteurCsv(ioException);
		}
	}

}
