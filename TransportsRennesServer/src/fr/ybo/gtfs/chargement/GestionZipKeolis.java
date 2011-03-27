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
import java.io.InputStreamReader;
import java.util.Collection;

import fr.ybo.gtfs.csv.moteur.MoteurCsv;
import fr.ybo.gtfs.modele.Correspondance;
import fr.ybo.gtfs.modele.Horaire;

public final class GestionZipKeolis {

	private static final String URL_BASE = "/gtfs/";
	private static final String URL_STOP_TIMES = URL_BASE + "horaires_";

	public static Iterable<Correspondance> getCorrespondances(MoteurCsv moteurCsv) {
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(GestionZipKeolis.class.getResourceAsStream("/gtfs/correspondances.txt")), 8 << 10);
			try {
				return moteurCsv.parseFile(bufferedReader, Correspondance.class);
			} finally {
				bufferedReader.close();
			}
		} catch (Exception exception) {
			throw new GestionFilesException(exception);
		}
	}

	public static Collection<Horaire> chargeLigne(MoteurCsv moteurCsv, String ligneId) {
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
					GestionZipKeolis.class.getResourceAsStream(URL_STOP_TIMES + ligneId.toLowerCase() + ".txt")),
					8 << 10);
			try {
				return moteurCsv.parseFile(bufferedReader, Horaire.class);
			} finally {
				bufferedReader.close();
			}
		} catch (Exception exception) {
			throw new GestionFilesException(exception);
		}
	}

	public static <ObjetKeolis> Collection<ObjetKeolis> getAndParseKeolis(MoteurCsv moteur, String file, Class<ObjetKeolis> clazz) {
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(GestionZipKeolis.class.getResourceAsStream(URL_BASE + file), "utf-8"), 8 << 10);
			try {
				return moteur.parseFile(bufferedReader, clazz);
			} finally {
				bufferedReader.close();
			}
		} catch (Exception exception) {
			throw new GestionFilesException(exception);
		}
	}

	private GestionZipKeolis() {
	}

}
