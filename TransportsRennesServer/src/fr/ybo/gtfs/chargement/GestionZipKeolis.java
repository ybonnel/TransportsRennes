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

package fr.ybo.gtfs.chargement;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import fr.ybo.gtfs.csv.moteur.ErreurMoteurCsv;
import fr.ybo.gtfs.csv.moteur.MoteurCsv;
import fr.ybo.gtfs.modele.Horaire;

public final class GestionZipKeolis {

	private final static String URL_BASE = "/gtfs/";
	private final static String URL_STOP_TIMES = URL_BASE + "horaires_";
	private final static String URL_ZIP_PRINCIPALE = URL_BASE
			+ "GTFSRennesPrincipal.zip";

	public static List<Horaire> chargeLigne(MoteurCsv moteurCsv, String ligneId) {
		try {
			BufferedReader bufReader = null;
			ZipInputStream zipInputStream = new ZipInputStream(
					GestionZipKeolis.class.getResourceAsStream(URL_STOP_TIMES
							+ ligneId + ".zip"));
			zipInputStream.getNextEntry();
			bufReader = new BufferedReader(
					new InputStreamReader(zipInputStream), 8 * 1024);
			return moteurCsv.parseFile(bufReader, Horaire.class);
		} catch (Exception exception) {
			throw new ErreurGestionFiles(exception);
		}
	}

	public static Map<Class<?>, List<?>> getAndParseZipKeolis(
			final MoteurCsv moteur) {
		final ZipInputStream zipInputStream = new ZipInputStream(
				GestionZipKeolis.class.getResourceAsStream(URL_ZIP_PRINCIPALE));
		ZipEntry zipEntry;
		String ligne;
		BufferedReader bufReader;
		Map<Class<?>, List<?>> retour = new HashMap<Class<?>, List<?>>();
		try {
			try {
				while ((zipEntry = zipInputStream.getNextEntry()) != null) {
					bufReader = new BufferedReader(new InputStreamReader(
							zipInputStream), 8 * 1024);
					List<Object> objects = new ArrayList<Object>();
					retour.put(
							moteur.nouveauFichier(zipEntry.getName(),
									bufReader.readLine()), objects);
					while ((ligne = bufReader.readLine()) != null) {
						objects.add(moteur.creerObjet(ligne));
					}
				}
			} finally {
				zipInputStream.close();
			}
		} catch (IOException ioException) {
			throw new ErreurMoteurCsv(ioException);
		}
		return retour;
	}

	private GestionZipKeolis() {
	}

}
