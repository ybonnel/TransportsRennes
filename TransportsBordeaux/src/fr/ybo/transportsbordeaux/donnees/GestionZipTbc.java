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

package fr.ybo.transportsbordeaux.donnees;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.res.Resources;
import fr.ybo.moteurcsv.MoteurCsv;
import fr.ybo.moteurcsv.exception.MoteurCsvException;
import fr.ybo.transportsbordeaux.R;
import fr.ybo.transportsbordeaux.TransportsBordeauxApplication;
import fr.ybo.transportsbordeaux.database.DataBaseException;
import fr.ybo.transportsbordeaux.util.LogYbo;

public final class GestionZipTbc {

	private static final LogYbo LOG_YBO = new LogYbo(GestionZipTbc.class);
	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMdd");

	private static final CoupleResourceFichier[] RESOURCES_PRINCIPALE = {new CoupleResourceFichier(R.raw.arrets, "arrets.txt"),
			new CoupleResourceFichier(R.raw.arrets_routes, "arrets_routes.txt"),
			new CoupleResourceFichier(R.raw.directions, "directions.txt"),
			new CoupleResourceFichier(R.raw.lignes, "lignes.txt") };

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
					TransportsBordeauxApplication.getDataBaseHelper().beginTransaction();
					String ligne = bufReader.readLine();
					while (ligne != null) {
						TransportsBordeauxApplication.getDataBaseHelper().insert(moteur.creerObjet(ligne));
						ligne = bufReader.readLine();
					}
				} finally {
					bufReader.close();
					TransportsBordeauxApplication.getDataBaseHelper().endTransaction();
				}
				LOG_YBO.debug("Fin du traitement du fichier " + resource.fichier);
			}
			TransportsBordeauxApplication.getDataBaseHelper().close();
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
			throw new GestionFilesException("Erreur lors de la récupération du fichier last_update", exception);
		}
	}

	private GestionZipTbc() {
	}

}
