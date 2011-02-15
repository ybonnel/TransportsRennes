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

import fr.ybo.gtfs.csv.moteur.MoteurCsv;
import fr.ybo.gtfs.csv.moteur.MoteurCsvException;
import fr.ybo.gtfs.modele.Correspondance;
import fr.ybo.gtfs.modele.Horaire;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class GestionZipKeolis {

	private static final String URL_BASE = "/gtfs/";
	private static final String URL_STOP_TIMES = URL_BASE + "horaires_";
	private static final String URL_ZIP_PRINCIPALE = URL_BASE + "GTFSRennesPrincipal.zip";

	public static Iterable<Correspondance> getCorrespondances(MoteurCsv moteurCsv) {
		try {
			ZipInputStream zipInputStream = new ZipInputStream(GestionZipKeolis.class.getResourceAsStream("/gtfs/correspondances.zip"));
			zipInputStream.getNextEntry();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(zipInputStream), 8 << 10);
			try {
				return moteurCsv.parseFile(bufferedReader, Correspondance.class);
			} finally {
				bufferedReader.close();
			}
		} catch (Exception exception) {
			throw new GestionFilesException(exception);
		}
	}

	public static Iterable<Horaire> chargeLigne(MoteurCsv moteurCsv, String ligneId) {
		try {
			ZipInputStream zipInputStream = new ZipInputStream(GestionZipKeolis.class.getResourceAsStream(URL_STOP_TIMES + ligneId + ".zip"));
			zipInputStream.getNextEntry();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(zipInputStream), 8 << 10);
			try {
				return moteurCsv.parseFile(bufferedReader, Horaire.class);
			} finally {
				bufferedReader.close();
			}
		} catch (Exception exception) {
			throw new GestionFilesException(exception);
		}
	}

	public static Map<Class<?>, List<?>> getAndParseZipKeolis(MoteurCsv moteur) {
		ZipInputStream zipInputStream = new ZipInputStream(GestionZipKeolis.class.getResourceAsStream(URL_ZIP_PRINCIPALE));
		Map<Class<?>, List<?>> retour = new HashMap<Class<?>, List<?>>(10);
		try {
			try {
				ZipEntry zipEntry = zipInputStream.getNextEntry();
				while (zipEntry != null) {
					BufferedReader bufReader = new BufferedReader(new InputStreamReader(zipInputStream), 8 << 10);
					try {
						List<Object> objects = new ArrayList<Object>(500);
						retour.put(moteur.nouveauFichier(zipEntry.getName(), bufReader.readLine()), objects);
						String ligne = bufReader.readLine();
						while (ligne != null) {
							objects.add(moteur.creerObjet(ligne));
							ligne = bufReader.readLine();
						}
					} finally {
						bufReader.close();
					}
					zipEntry = zipInputStream.getNextEntry();
				}
			} finally {
				zipInputStream.close();
			}
		} catch (IOException ioException) {
			throw new MoteurCsvException(ioException);
		}
		return retour;
	}

	private GestionZipKeolis() {
	}

}
