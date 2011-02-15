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

package fr.ybo.transportsrennes.keolis.gtfs.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import fr.ybo.transportsrennes.keolis.gtfs.database.modele.Base;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Arret;
import fr.ybo.transportsrennes.keolis.gtfs.modele.ArretFavori;
import fr.ybo.transportsrennes.keolis.gtfs.modele.ArretRoute;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Calendrier;
import fr.ybo.transportsrennes.keolis.gtfs.modele.DernierMiseAJour;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Direction;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Ligne;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Trajet;
import fr.ybo.transportsrennes.keolis.gtfs.modele.VeloFavori;
import fr.ybo.transportsrennes.util.LogYbo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataBaseHelper extends SQLiteOpenHelper {

	private static final LogYbo LOG_YBO = new LogYbo(DataBaseHelper.class);
	private static final String DATABASE_NAME = "keolis.db";
	private static final int DATABASE_VERSION = 6;

	private final Base base;

	private boolean transactionOpen;

	public DataBaseHelper(final Context context, final List<Class<?>> classes) throws DataBaseException {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		base = new Base(classes);
	}

	public void beginTransaction() {
		endTransaction();
		getWritableDatabase().beginTransaction();
		transactionOpen = true;
	}

	public <Entite> void delete(final Entite entite) throws DataBaseException {
		base.delete(getWritableDatabase(), entite);
	}

	public <Entite> void deleteAll(final Class<Entite> clazz) throws DataBaseException {
		base.deleteAll(getWritableDatabase(), clazz);
	}

	public void endTransaction() {
		if (transactionOpen) {
			getWritableDatabase().setTransactionSuccessful();
			getWritableDatabase().endTransaction();
		}
		transactionOpen = false;
	}

	public Cursor executeSelectQuery(final String query, final List<String> selectionArgs) {

		return getReadableDatabase().rawQuery(query, selectionArgs == null ? null : selectionArgs.toArray(new String[selectionArgs.size()]));
	}

	public Base getBase() {
		return base;
	}

	public <Entite> void insert(final Entite entite) throws DataBaseException {
		base.insert(getWritableDatabase(), entite);
	}

	@Override
	public void onCreate(final SQLiteDatabase db) {
		base.createDataBase(db);
	}

	private interface UpgradeDatabase {
		void upagrade(SQLiteDatabase db);
	}

	private Map<Integer, DataBaseHelper.UpgradeDatabase> mapUpgrades;

	private Map<Integer, DataBaseHelper.UpgradeDatabase> getUpgrades() {
		if (mapUpgrades == null) {
			mapUpgrades = new HashMap<Integer, DataBaseHelper.UpgradeDatabase>(10);
			mapUpgrades.put(2, new DataBaseHelper.UpgradeDatabase() {
				public void upagrade(final SQLiteDatabase db) {
					base.getTable(VeloFavori.class).createTable(db);
				}
			});
			mapUpgrades.put(3, new DataBaseHelper.UpgradeDatabase() {
				public void upagrade(final SQLiteDatabase db) {
					base.dropDataBase(db);
					base.createDataBase(db);
				}
			});
			mapUpgrades.put(4, new DataBaseHelper.UpgradeDatabase() {
				public void upagrade(final SQLiteDatabase db) {
					db.execSQL("ALTER TABLE ArretFavori ADD COLUMN ordre INTEGER");
				}
			});
			mapUpgrades.put(5, new DataBaseHelper.UpgradeDatabase() {
				public void upagrade(final SQLiteDatabase db) {
					Cursor cursor =
							db.query("sqlite_master", Collections.singleton("name").toArray(new String[1]), " type = 'table'", null, null, null,
									null);
					while (cursor.moveToNext()) {
						final String tableName = cursor.getString(0);
						if (!"android_metadata".equals(tableName) && !"VeloFavori".equals(tableName) && !"ArretFavori".equals(tableName)) {
							db.execSQL("DROP TABLE " + tableName);
						}
					}
					cursor.close();
					base.getTable(Direction.class).createTable(db);
					base.getTable(ArretRoute.class).createTable(db);
					base.getTable(DernierMiseAJour.class).createTable(db);
					base.getTable(Ligne.class).createTable(db);
					base.getTable(Arret.class).createTable(db);
					base.getTable(Calendrier.class).createTable(db);
					base.getTable(Trajet.class).createTable(db);
					// Gestion des favoris.
					db.execSQL("ALTER TABLE ArretFavori RENAME TO ArretFavori_tmp");
					base.getTable(ArretFavori.class).createTable(db);
					final List<String> columns = new ArrayList<String>(7);
					columns.add("stopId");
					columns.add("routeId");
					columns.add("nomArret");
					columns.add("direction");
					columns.add("routeNomCourt");
					columns.add("routeNomLong");
					columns.add("ordre");
					Cursor arretFavori_tmp = db.query("ArretFavori_tmp", columns.toArray(new String[7]), null, null, null, null, null);
					final int arretIdIndex = arretFavori_tmp.getColumnIndex("stopId");
					final int ligneIdIndex = arretFavori_tmp.getColumnIndex("routeId");
					final int nomArretIndex = arretFavori_tmp.getColumnIndex("nomArret");
					final int directionIndex = arretFavori_tmp.getColumnIndex("direction");
					final int nomCourtIndex = arretFavori_tmp.getColumnIndex("routeNomCourt");
					final int nomLongIndex = arretFavori_tmp.getColumnIndex("routeNomLong");
					final int ordreIndex = arretFavori_tmp.getColumnIndex("ordre");
					final ArretFavori favori = new ArretFavori();
					int count = 1;
					while (arretFavori_tmp.moveToNext()) {
						favori.arretId = arretFavori_tmp.getString(arretIdIndex);
						favori.ligneId = arretFavori_tmp.getString(ligneIdIndex);
						favori.nomArret = arretFavori_tmp.getString(nomArretIndex);
						favori.direction = arretFavori_tmp.getString(directionIndex);
						favori.nomCourt = arretFavori_tmp.getString(nomCourtIndex);
						favori.nomLong = arretFavori_tmp.getString(nomLongIndex);
						favori.ordre = arretFavori_tmp.isNull(ordreIndex) ? count : arretFavori_tmp.getInt(ordreIndex);
						count++;
						base.insert(db, favori);
					}
					db.execSQL("DROP TABLE ArretFavori_tmp");
				}
			});
			mapUpgrades.put(6, new DataBaseHelper.UpgradeDatabase() {
				public void upagrade(final SQLiteDatabase db) {
					final Cursor cursor =
							db.query("sqlite_master", Collections.singleton("name").toArray(new String[1]), " type = 'table'", null, null, null,
									null);
					while (cursor.moveToNext()) {
						final String tableName = cursor.getString(0);
						if (!"android_metadata".equals(tableName) && !"VeloFavori".equals(tableName) && !"ArretFavori".equals(tableName)) {
							db.execSQL("DROP TABLE " + tableName);
						}
					}
					cursor.close();
					base.getTable(Direction.class).createTable(db);
					base.getTable(ArretRoute.class).createTable(db);
					base.getTable(DernierMiseAJour.class).createTable(db);
					base.getTable(Ligne.class).createTable(db);
					base.getTable(Arret.class).createTable(db);
					base.getTable(Calendrier.class).createTable(db);
					base.getTable(Trajet.class).createTable(db);
				}
			});
		}
		return mapUpgrades;
	}

	@Override
	public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
		LOG_YBO.debug("Demande de mise à jour de la base de la version " + oldVersion + " à la version " + newVersion);
		try {
			for (int version = oldVersion + 1; version <= newVersion; version++) {
				if (getUpgrades().containsKey(version)) {
					LOG_YBO.debug("Lancement de la mise à jour pour la version " + version);
					getUpgrades().get(version).upagrade(db);
				}
			}
		} catch (Exception exception) {
			LOG_YBO.erreur("Une erreur est survenue lors de l'upgrade, on supprime le schéma et on le recrée.", exception);
			final Cursor cursor =
					db.query("sqlite_master", Collections.singleton("name").toArray(new String[1]), " type = 'table'", null, null, null, null);
			while (cursor.moveToNext()) {
				final String tableName = cursor.getString(0);
				if (!"android_metadata".equals(tableName)) {
					db.execSQL("DROP TABLE " + tableName);
				}
			}
			cursor.close();
			base.createDataBase(db);
		}
	}

	public <Entite> List<Entite> select(final Entite entite) throws DataBaseException {
		return base.select(getReadableDatabase(), entite, null, null, null);
	}

	public <Entite> List<Entite> select(final Entite entite, final String orderBy)
			throws DataBaseException {
		return base.select(getReadableDatabase(), entite, null, null, orderBy);
	}

	public <Entite> Entite selectSingle(final Entite entite) throws DataBaseException {
		final List<Entite> entites = select(entite);
		if (entites.size() > 1) {
			throw new DataBaseException("Plusieurs résultats trouvés pour un selectSingle");
		}
		if (entites.size() == 0) {
			return null;
		}
		return entites.get(0);
	}

}
