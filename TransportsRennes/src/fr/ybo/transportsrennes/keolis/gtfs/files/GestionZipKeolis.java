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

import android.content.res.Resources;
import fr.ybo.transportsrennes.R;
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
import java.text.SimpleDateFormat;
import java.util.Date;

public final class GestionZipKeolis {

	private static final LogYbo LOG_YBO = new LogYbo(GestionZipKeolis.class);
	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMdd");

	private static final String URL_STOP_TIMES = "horaires_";

	private static CoupleResourceFichier getResourceForStopTime(String ligneId) throws GestionFilesException {

		try {
			String nomResource = URL_STOP_TIMES + ligneId.toLowerCase();
			int resourceId = R.raw.class.getDeclaredField(nomResource).getInt(null);
			return new CoupleResourceFichier(resourceId, nomResource + ".txt");
		} catch (Exception exception) {
			throw new GestionFilesException(exception);
		}
	}

	public static void chargeLigne(MoteurCsv moteurCsv, String ligneId, DataBaseHelper dataBaseHelper, Resources resources) {
		try {
			BufferedReader bufReader = new BufferedReader(new InputStreamReader(resources.openRawResource(getResourceForStopTime(ligneId).resourceId)), 8 << 10);
			try {
				moteurCsv.parseFileAndInsert(bufReader, Horaire.class, dataBaseHelper, ligneId);
			} finally {
				bufReader.close();
			}
		} catch (Exception exception) {
			throw new GestionFilesException(exception);
		}

	}

	private static final CoupleResourceFichier[] RESOURCES_PRINCIPALE = {new CoupleResourceFichier(R.raw.arrets, "arrets.txt"),
			new CoupleResourceFichier(R.raw.arrets_routes, "arrets_routes.txt"),
			new CoupleResourceFichier(R.raw.calendriers, "calendriers.txt"),
			new CoupleResourceFichier(R.raw.directions, "directions.txt"),
			new CoupleResourceFichier(R.raw.lignes, "lignes.txt"),
			new CoupleResourceFichier(R.raw.trajets, "trajets.txt")};

	private static class CoupleResourceFichier {
		private final int resourceId;
		private final String fichier;

		private CoupleResourceFichier(int resourceId, String fichier) {
			this.resourceId = resourceId;
			this.fichier = fichier;
		}
	}

	public static void getAndParseZipKeolis(MoteurCsv moteur, Resources resources) throws GestionFilesException, MoteurCsvException, DataBaseException {
		try {
			for (CoupleResourceFichier resource : RESOURCES_PRINCIPALE) {
				LOG_YBO.debug("Début du traitement du fichier " + resource.fichier);
				BufferedReader bufReader = new BufferedReader(new InputStreamReader(resources.openRawResource(resource.resourceId)), 8 << 10);
				try {
					moteur.nouveauFichier(resource.fichier, bufReader.readLine());
					TransportsRennesApplication.getDataBaseHelper().beginTransaction();
					String ligne = bufReader.readLine();
					while (ligne != null) {
						TransportsRennesApplication.getDataBaseHelper().insert(moteur.creerObjet(ligne));
						ligne = bufReader.readLine();
					}
				} finally {
					bufReader.close();
					TransportsRennesApplication.getDataBaseHelper().endTransaction();
				}
				LOG_YBO.debug("Fin du traitement du fichier " + resource.fichier);
			}
			TransportsRennesApplication.getDataBaseHelper().close();
			LOG_YBO.debug("Fin getAndParseZipKeolis.");
		} catch (IOException e) {
			throw new GestionFilesException(e);
		}
	}


	public static Date getLastUpdate(Resources resources) {
		try {
			BufferedReader bufReader = new BufferedReader(new InputStreamReader(resources.openRawResource(R.raw.last_update)), 100);
			try {
				return SDF.parse(bufReader.readLine());
			} finally {
				bufReader.close();
			}
		} catch (Exception exception) {
			throw new KeolisException("Erreur lors de la récupération du fichier last_update", exception);
		}
	}

	private GestionZipKeolis() {
	}

}
