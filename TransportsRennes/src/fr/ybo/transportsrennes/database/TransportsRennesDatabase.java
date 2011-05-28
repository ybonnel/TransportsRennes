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

package fr.ybo.transportsrennes.database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import fr.ybo.database.DataBaseHelper;
import fr.ybo.moteurcsv.MoteurCsv;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.keolis.ConstantesKeolis;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Arret;
import fr.ybo.transportsrennes.keolis.gtfs.modele.ArretFavori;
import fr.ybo.transportsrennes.keolis.gtfs.modele.ArretRoute;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Calendrier;
import fr.ybo.transportsrennes.keolis.gtfs.modele.DernierMiseAJour;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Direction;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Ligne;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Trajet;
import fr.ybo.transportsrennes.keolis.gtfs.modele.VeloFavori;

public class TransportsRennesDatabase extends DataBaseHelper {

	private static final String DATABASE_NAME = "keolis.db";
	private static final int DATABASE_VERSION = 10;

	private Context context;

	public TransportsRennesDatabase(Context context) {
		super(context, ConstantesKeolis.LIST_CLASSES_DATABASE, DATABASE_NAME, DATABASE_VERSION);
		this.context = context;
	}

	private Map<Integer, UpgradeDatabase> mapUpgrades;

	protected Map<Integer, UpgradeDatabase> getUpgrades() {
		if (mapUpgrades == null) {
			mapUpgrades = new HashMap<Integer, UpgradeDatabase>(10);
			mapUpgrades.put(2, new UpgradeDatabase() {
				public void upgrade(SQLiteDatabase db) {
					getBase().getTable(VeloFavori.class).createTable(db);
				}
			});
			mapUpgrades.put(3, new UpgradeDatabase() {
				public void upgrade(SQLiteDatabase db) {
					getBase().dropDataBase(db);
					getBase().createDataBase(db);
				}
			});
			mapUpgrades.put(4, new UpgradeDatabase() {
				public void upgrade(SQLiteDatabase db) {
					db.execSQL("ALTER TABLE ArretFavori ADD COLUMN ordre INTEGER");
				}
			});
			mapUpgrades.put(5, new UpgradeDatabase() {
				public void upgrade(SQLiteDatabase db) {
					Cursor cursor =
							db.query("sqlite_master", Collections.singleton("name").toArray(new String[1]), " type = 'table'", null, null, null,
									null);
					while (cursor.moveToNext()) {
						String tableName = cursor.getString(0);
						if (!"android_metadata".equals(tableName) && !"VeloFavori".equals(tableName) && !"ArretFavori".equals(tableName)) {
							db.execSQL("DROP TABLE " + tableName);
						}
					}
					cursor.close();
					getBase().getTable(Direction.class).createTable(db);
					getBase().getTable(ArretRoute.class).createTable(db);
					getBase().getTable(DernierMiseAJour.class).createTable(db);
					getBase().getTable(Ligne.class).createTable(db);
					getBase().getTable(Arret.class).createTable(db);
					getBase().getTable(Calendrier.class).createTable(db);
					getBase().getTable(Trajet.class).createTable(db);
					// Gestion des favoris.
					db.execSQL("ALTER TABLE ArretFavori RENAME TO ArretFavori_tmp");
					getBase().getTable(ArretFavori.class).createTable(db);
					List<String> columns = new ArrayList<String>(7);
					columns.add("stopId");
					columns.add("routeId");
					columns.add("nomArret");
					columns.add("direction");
					columns.add("routeNomCourt");
					columns.add("routeNomLong");
					columns.add("ordre");
					Cursor arretFavoriTmp = db.query("ArretFavori_tmp", columns.toArray(new String[7]), null, null, null, null, null);
					int arretIdIndex = arretFavoriTmp.getColumnIndex("stopId");
					int ligneIdIndex = arretFavoriTmp.getColumnIndex("routeId");
					int nomArretIndex = arretFavoriTmp.getColumnIndex("nomArret");
					int directionIndex = arretFavoriTmp.getColumnIndex("direction");
					int nomCourtIndex = arretFavoriTmp.getColumnIndex("routeNomCourt");
					int nomLongIndex = arretFavoriTmp.getColumnIndex("routeNomLong");
					int ordreIndex = arretFavoriTmp.getColumnIndex("ordre");
					ArretFavori favori = new ArretFavori();
					int count = 1;
					while (arretFavoriTmp.moveToNext()) {
						favori.arretId = arretFavoriTmp.getString(arretIdIndex);
						favori.ligneId = arretFavoriTmp.getString(ligneIdIndex);
						favori.nomArret = arretFavoriTmp.getString(nomArretIndex);
						favori.direction = arretFavoriTmp.getString(directionIndex);
						favori.nomCourt = arretFavoriTmp.getString(nomCourtIndex);
						favori.nomLong = arretFavoriTmp.getString(nomLongIndex);
						favori.ordre = arretFavoriTmp.isNull(ordreIndex) ? count : arretFavoriTmp.getInt(ordreIndex);
						count++;
						getBase().insert(db, favori);
					}
					db.execSQL("DROP TABLE ArretFavori_tmp");
				}
			});
			mapUpgrades.put(6, new UpgradeDatabase() {
				public void upgrade(SQLiteDatabase db) {
					Cursor cursor =
							db.query("sqlite_master", Collections.singleton("name").toArray(new String[1]), " type = 'table'", null, null, null,
									null);
					while (cursor.moveToNext()) {
						String tableName = cursor.getString(0);
						if (!"android_metadata".equals(tableName) && !"VeloFavori".equals(tableName) && !"ArretFavori".equals(tableName)) {
							db.execSQL("DROP TABLE " + tableName);
						}
					}
					cursor.close();
					getBase().getTable(Direction.class).createTable(db);
					getBase().getTable(ArretRoute.class).createTable(db);
					getBase().getTable(DernierMiseAJour.class).createTable(db);
					getBase().getTable(Ligne.class).createTable(db);
					getBase().getTable(Arret.class).createTable(db);
					getBase().getTable(Calendrier.class).createTable(db);
					getBase().getTable(Trajet.class).createTable(db);
				}
			});
			mapUpgrades.put(7, new UpgradeDatabase() {
				public void upgrade(SQLiteDatabase db) {
					db.execSQL("DELETE FROM DernierMiseAJour");
				}
			});
			mapUpgrades.put(9, new UpgradeDatabase() {
				public void upgrade(SQLiteDatabase db) {
					// Gestion des favoris.
					db.execSQL("ALTER TABLE ArretFavori RENAME TO ArretFavori_tmp");
					getBase().getTable(ArretFavori.class).createTable(db);
					List<String> columns = new ArrayList<String>(7);
					columns.add("arretId");
					columns.add("ligneId");
					columns.add("nomArret");
					columns.add("direction");
					columns.add("nomCourt");
					columns.add("nomLong");
					columns.add("ordre");
					Cursor arretFavoriTmp = db.query("ArretFavori_tmp", columns.toArray(new String[7]), null, null, null, null, null);
					int arretIdIndex = arretFavoriTmp.getColumnIndex("arretId");
					int ligneIdIndex = arretFavoriTmp.getColumnIndex("ligneId");
					int nomArretIndex = arretFavoriTmp.getColumnIndex("nomArret");
					int directionIndex = arretFavoriTmp.getColumnIndex("direction");
					int nomCourtIndex = arretFavoriTmp.getColumnIndex("nomCourt");
					int nomLongIndex = arretFavoriTmp.getColumnIndex("nomLong");
					int ordreIndex = arretFavoriTmp.getColumnIndex("ordre");
					ArretFavori favori = new ArretFavori();
					while (arretFavoriTmp.moveToNext()) {
						favori.arretId = arretFavoriTmp.getString(arretIdIndex);
						favori.ligneId = arretFavoriTmp.getString(ligneIdIndex);
						favori.nomArret = arretFavoriTmp.getString(nomArretIndex);
						favori.direction = arretFavoriTmp.getString(directionIndex);
						favori.nomCourt = arretFavoriTmp.getString(nomCourtIndex);
						favori.nomLong = arretFavoriTmp.getString(nomLongIndex);
						favori.ordre = arretFavoriTmp.getInt(ordreIndex);
						getBase().insert(db, favori);
					}
					db.execSQL("DROP TABLE ArretFavori_tmp");
					// Verrue pour la ligne 67 et sa boucle.
					Cursor ligne67 = db.query("Ligne", new String[]{"Chargee"}, "id = 67", null, null, null, null);
					if (ligne67.moveToFirst() && !ligne67.isNull(0) && ligne67.getInt(0) == 1) {
						db.execSQL("UPDATE Horaire_67 SET terminus = 1 WHERE arretId = 'repto1' AND stopSequence > 3");
					}
				}
			});
			mapUpgrades.put(10, new UpgradeDatabase() {
				public void upgrade(SQLiteDatabase db) {
					Cursor cursor = db.query("sqlite_master", Collections.singleton("name").toArray(new String[1]),
							" type = 'table'", null, null, null, null);
					while (cursor.moveToNext()) {
						String tableName = cursor.getString(0);
						if (!"android_metadata".equals(tableName) && !"VeloFavori".equals(tableName)
								&& !"ArretFavori".equals(tableName)) {
							db.execSQL("DROP TABLE " + tableName);
						}
					}
					cursor.close();
					getBase().getTable(Direction.class).createTable(db);
					getBase().getTable(ArretRoute.class).createTable(db);
					getBase().getTable(DernierMiseAJour.class).createTable(db);
					getBase().getTable(Ligne.class).createTable(db);
					getBase().getTable(Arret.class).createTable(db);
					getBase().getTable(Calendrier.class).createTable(db);
					getBase().getTable(Trajet.class).createTable(db);
					// Gestion des favoris.
					db.execSQL("ALTER TABLE ArretFavori RENAME TO ArretFavori_tmp");
					getBase().getTable(ArretFavori.class).createTable(db);
					List<String> columns = new ArrayList<String>(7);
					columns.add("arretId");
					columns.add("ligneId");
					columns.add("nomArret");
					columns.add("direction");
					columns.add("nomCourt");
					columns.add("nomLong");
					columns.add("ordre");
					Cursor arretFavoriTmp = db.query("ArretFavori_tmp", columns.toArray(new String[7]), null, null,
							null, null, null);
					int arretIdIndex = arretFavoriTmp.getColumnIndex("arretId");
					int ligneIdIndex = arretFavoriTmp.getColumnIndex("ligneId");
					int nomArretIndex = arretFavoriTmp.getColumnIndex("nomArret");
					int directionIndex = arretFavoriTmp.getColumnIndex("direction");
					int nomCourtIndex = arretFavoriTmp.getColumnIndex("nomCourt");
					int nomLongIndex = arretFavoriTmp.getColumnIndex("nomLong");
					int ordreIndex = arretFavoriTmp.getColumnIndex("ordre");
					ArretFavori favori = new ArretFavori();
					List<Class<?>> classCsv = new ArrayList<Class<?>>();
					classCsv.add(ArretRoute.class);
					classCsv.add(Direction.class);
					MoteurCsv moteur = new MoteurCsv(classCsv);
					Map<String, Map<String, List<ArretRoute>>> mapArretsRoutes = new HashMap<String, Map<String, List<ArretRoute>>>();
					for (ArretRoute arretRoute : moteur.parseInputStream(
							context.getResources().openRawResource(R.raw.arrets_routes), ArretRoute.class)) {
						if (!mapArretsRoutes.containsKey(arretRoute.ligneId)) {
							mapArretsRoutes.put(arretRoute.ligneId, new HashMap<String, List<ArretRoute>>());
						}
						if (!mapArretsRoutes.get(arretRoute.ligneId).containsKey(arretRoute.arretId)) {
							mapArretsRoutes.get(arretRoute.ligneId)
									.put(arretRoute.arretId, new ArrayList<ArretRoute>());
						}
						mapArretsRoutes.get(arretRoute.ligneId).get(arretRoute.arretId).add(arretRoute);
					}
					Map<Integer, String> directions = new HashMap<Integer, String>();

					for (Direction direction : moteur.parseInputStream(
							context.getResources().openRawResource(R.raw.directions), Direction.class)) {
						directions.put(direction.id, direction.direction);
					}
					while (arretFavoriTmp.moveToNext()) {
						favori.arretId = arretFavoriTmp.getString(arretIdIndex);
						favori.ligneId = arretFavoriTmp.getString(ligneIdIndex);
						favori.nomArret = arretFavoriTmp.getString(nomArretIndex);
						favori.direction = arretFavoriTmp.getString(directionIndex);
						favori.nomCourt = arretFavoriTmp.getString(nomCourtIndex);
						favori.nomLong = arretFavoriTmp.getString(nomLongIndex);
						favori.ordre = arretFavoriTmp.getInt(ordreIndex);
						if (mapArretsRoutes.containsKey(favori.ligneId)
								&& mapArretsRoutes.get(favori.ligneId).containsKey(favori.arretId)) {
							for (ArretRoute arretRoute : mapArretsRoutes.get(favori.ligneId).get(favori.arretId)) {
								favori.macroDirection = arretRoute.macroDirection;
							}
						}
						if (favori.macroDirection != null) {
							getBase().insert(db, favori);
						}
					}
					db.execSQL("DROP TABLE ArretFavori_tmp");
				}
			});
		}
		return mapUpgrades;
	}
}
