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
package fr.ybo.transportscommun.donnees.manager.gtfs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.res.Resources;
import android.database.DatabaseUtils.InsertHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDiskIOException;
import fr.ybo.database.DataBaseException;
import fr.ybo.database.DataBaseHelper;
import fr.ybo.database.modele.Table;
import fr.ybo.moteurcsv.MoteurCsv;
import fr.ybo.moteurcsv.exception.MoteurCsvException;
import fr.ybo.transportscommun.AbstractTransportsApplication;
import fr.ybo.transportscommun.donnees.manager.LigneInexistanteException;
import fr.ybo.transportscommun.donnees.modele.Horaire;
import fr.ybo.transportscommun.util.LoadingInfo;
import fr.ybo.transportscommun.util.LogYbo;
import fr.ybo.transportscommun.util.NoSpaceLeftException;

public final class GestionZipKeolis {

    private static final LogYbo LOG_YBO = new LogYbo(GestionZipKeolis.class);
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMdd");

    private static final String URL_STOP_TIMES = "horaires_";

	private static List<CoupleResourceFichier> getResourceForStopTime(Class<?> rawClass, String ligneId)
			throws LigneInexistanteException {

        try {
			List<CoupleResourceFichier> retour = new ArrayList<CoupleResourceFichier>();
            String nomResource = URL_STOP_TIMES + ligneId.toLowerCase();
			int resourceId = rawClass.getDeclaredField(nomResource).getInt(null);
			retour.add(new CoupleResourceFichier(resourceId, nomResource + ".txt"));
			int count = 1;
			boolean continu = true;
			while (continu) {
				String nomResourceAlternatif = new StringBuilder(nomResource).append('_').append(count).toString();
				try {
					int resourceAlternatifId = rawClass.getDeclaredField(nomResourceAlternatif).getInt(null);
					retour.add(new CoupleResourceFichier(resourceAlternatifId, nomResourceAlternatif + ".txt"));
				} catch (NoSuchFieldException noSuchField) {
					continu = false;
				}
				count++;
			}
			return retour;
        } catch (NoSuchFieldException noSuchFieldException) {
            throw new LigneInexistanteException();
        } catch (Exception exception) {
            throw new GestionFilesException(exception);
        }
    }

	public static void chargeLigne(Class<?> rawClass, MoteurCsv moteurCsv, String ligneId,
			final DataBaseHelper dataBaseHelper, Resources resources) throws LigneInexistanteException,
			NoSpaceLeftException {
		try {
			UpdateDataBase.setMajDatabaseEncours(true);
			LOG_YBO.debug("Début chargeLigne");
			final Table table = dataBaseHelper.getBase().getTable(Horaire.class);
			table.addSuffixeToTableName(ligneId);
			final SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
			LOG_YBO.debug("Suppression de la table");
			table.dropTable(db);
			LOG_YBO.debug("Création de la table");
			table.createTable(db);
			for (CoupleResourceFichier coupleResourceFichier : getResourceForStopTime(rawClass, ligneId)) {
				LOG_YBO.debug("Mise en base du fichier " + coupleResourceFichier.resourceId);
				BufferedReader bufReader = new BufferedReader(new InputStreamReader(
						resources.openRawResource(coupleResourceFichier.resourceId)), 8 << 10);
				dataBaseHelper.beginTransaction();
				final InsertHelper ih = new InsertHelper(db, table.getName());
				try {

                    // Get the numeric indexes for each of the columns that
					// we're updating
					final int arretIdCol = ih.getColumnIndex("arretId");
					final int trajetIdCol = ih.getColumnIndex("trajetId");
					final int heureDepartCol = ih.getColumnIndex("heureDepart");
					final int stopSequenceCol = ih.getColumnIndex("stopSequence");
					final int terminusCol = ih.getColumnIndex("terminus");

                    LOG_YBO.debug("Début du parse du fichier");
					moteurCsv.parseFileAndInsert(bufReader, Horaire.class, new MoteurCsv.InsertObject<Horaire>() {

                        private int countLigne = 0;

                        public void insertObject(Horaire objet) {
							countLigne++;
							// Get the InsertHelper ready to insert a
							// single row
							ih.prepareForInsert();

                            // Add the data for each column
							ih.bind(arretIdCol, objet.arretId);
							ih.bind(trajetIdCol, objet.trajetId);
							ih.bind(heureDepartCol, objet.heureDepart);
							ih.bind(stopSequenceCol, objet.stopSequence);
							ih.bind(terminusCol, objet.terminus);
							if (objet.arretId != null && objet.trajetId != null) {
								// Insert the row into the database.
								ih.execute();
							}
							if (countLigne > 10000) {
								LOG_YBO.debug("Commit");
								countLigne = 0;
								dataBaseHelper.endTransaction();
								dataBaseHelper.beginTransaction();
							}
                        }
					});
					LOG_YBO.debug("Fin de parse du fichier");
				} finally {
					bufReader.close();
					dataBaseHelper.endTransaction();
					ih.close();
				}
            }
			LOG_YBO.debug("Fin chargeLigne");
		} catch (SQLiteDiskIOException ioException) {
			throw new NoSpaceLeftException();
        } catch (Exception exception) {
            throw new GestionFilesException(exception);
		} finally {
			UpdateDataBase.setMajDatabaseEncours(false);
        }

    }

    public static void getAndParseZipKeolis(MoteurCsv moteur, Resources resources, LoadingInfo info)
			throws GestionFilesException, MoteurCsvException, DataBaseException, NoSpaceLeftException {
        try {
			for (CoupleResourceFichier resource : AbstractTransportsApplication.getResourcesPrincipale()) {
                LOG_YBO.debug("Début du traitement du fichier " + resource.fichier);
                BufferedReader bufReader = new BufferedReader(new InputStreamReader(resources.openRawResource(resource.resourceId)), 8 << 10);
                try {
                    moteur.nouveauFichier(resource.fichier, bufReader.readLine());
					AbstractTransportsApplication.getDataBaseHelper().beginTransaction();
                    String ligne = bufReader.readLine();
                    while (ligne != null) {
						AbstractTransportsApplication.getDataBaseHelper().insert(moteur.creerObjet(ligne));
                        ligne = bufReader.readLine();
                    }
                } finally {
                    bufReader.close();
					AbstractTransportsApplication.getDataBaseHelper().endTransaction();
                }
				info.etapeSuivante();
                LOG_YBO.debug("Fin du traitement du fichier " + resource.fichier);
            }
			AbstractTransportsApplication.getDataBaseHelper().close();
            LOG_YBO.debug("Fin getAndParseZipKeolis.");
		} catch (SQLiteDiskIOException diskException) {
			throw new NoSpaceLeftException();
        } catch (IOException e) {
            throw new GestionFilesException(e);
        }
    }


	public static Date getLastUpdate(Resources resources, int lastUpdate) {
        try {
			BufferedReader bufReader = new BufferedReader(new InputStreamReader(resources.openRawResource(lastUpdate)),
					100);
            try {
                return SDF.parse(bufReader.readLine());
            } finally {
                bufReader.close();
            }
        } catch (Exception exception) {
			throw new GestionFilesException("Erreur lors de la récupération du fichier last_update", exception);
        }
    }

    private GestionZipKeolis() {
    }

}
