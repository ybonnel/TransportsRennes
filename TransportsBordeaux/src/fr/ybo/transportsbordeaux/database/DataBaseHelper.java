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

package fr.ybo.transportsbordeaux.database;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import fr.ybo.transportsbordeaux.database.modele.Base;
import fr.ybo.transportsbordeaux.modele.Arret;
import fr.ybo.transportsbordeaux.modele.ArretRoute;
import fr.ybo.transportsbordeaux.modele.DernierMiseAJour;
import fr.ybo.transportsbordeaux.modele.Direction;
import fr.ybo.transportsbordeaux.modele.Ligne;
import fr.ybo.transportsbordeaux.util.LogYbo;

public class DataBaseHelper extends SQLiteOpenHelper {

	private static final LogYbo LOG_YBO = new LogYbo(DataBaseHelper.class);
	private static final String DATABASE_NAME = "transportsbordeaux.db";
	private static final int DATABASE_VERSION = 2;

	private final Base base;

	private boolean transactionOpen;

	private Context context;

	public DataBaseHelper(Context context, List<Class<?>> classes) throws DataBaseException {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
		base = new Base(classes);
	}

	public void beginTransaction() {
		endTransaction();
		getWritableDatabase().beginTransaction();
		transactionOpen = true;
	}

	public <Entite> void delete(Entite entite) throws DataBaseException {
		base.delete(getWritableDatabase(), entite);
	}

	public <Entite> void deleteAll(Class<Entite> clazz) throws DataBaseException {
		base.deleteAll(getWritableDatabase(), clazz);
	}

	public void endTransaction() {
		if (transactionOpen) {
			getWritableDatabase().setTransactionSuccessful();
			getWritableDatabase().endTransaction();
		}
		transactionOpen = false;
	}

	public Cursor executeSelectQuery(String query, List<String> selectionArgs) {

		return getReadableDatabase().rawQuery(query, selectionArgs == null ? null : selectionArgs.toArray(new String[selectionArgs.size()]));
	}

	public Base getBase() {
		return base;
	}

	public <Entite> void insert(Entite entite) throws DataBaseException {
		base.insert(getWritableDatabase(), entite);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		base.createDataBase(db);
	}

	private interface UpgradeDatabase {
		void upagrade(SQLiteDatabase db, Context context);
	}

	private Map<Integer, DataBaseHelper.UpgradeDatabase> mapUpgrades;

	private Map<Integer, DataBaseHelper.UpgradeDatabase> getUpgrades() {
		if (mapUpgrades == null) {
			mapUpgrades = new HashMap<Integer, DataBaseHelper.UpgradeDatabase>(10);
			mapUpgrades.put(2, new UpgradeDatabase() {

				@Override
				public void upagrade(SQLiteDatabase db, Context context) {
					base.getTable(Arret.class).dropTable(db);
					base.getTable(ArretRoute.class).dropTable(db);
					base.getTable(DernierMiseAJour.class).dropTable(db);
					base.getTable(Direction.class).dropTable(db);
					base.getTable(Ligne.class).dropTable(db);
					base.getTable(Arret.class).createTable(db);
					base.getTable(ArretRoute.class).createTable(db);
					base.getTable(DernierMiseAJour.class).createTable(db);
					base.getTable(Direction.class).createTable(db);
					base.getTable(Ligne.class).createTable(db);
				}
			});
		}
		return mapUpgrades;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		LOG_YBO.debug("Demande de mise à jour de la base de la version " + oldVersion + " à la version " + newVersion);
		try {
			for (int version = oldVersion + 1; version <= newVersion; version++) {
				if (getUpgrades().containsKey(version)) {
					LOG_YBO.debug("Lancement de la mise à jour pour la version " + version);
					getUpgrades().get(version).upagrade(db, context);
				}
			}
		} catch (Exception exception) {
			LOG_YBO.erreur("Une erreur est survenue lors de l'upgrade, on supprime le schéma et on le recrée.", exception);
			Cursor cursor =
					db.query("sqlite_master", Collections.singleton("name").toArray(new String[1]), " type = 'table'", null, null, null, null);
			while (cursor.moveToNext()) {
				String tableName = cursor.getString(0);
				if (!"android_metadata".equals(tableName)) {
					db.execSQL("DROP TABLE " + tableName);
				}
			}
			cursor.close();
			base.createDataBase(db);
		}
	}

	public <Entite> List<Entite> select(Entite entite) throws DataBaseException {
		return base.select(getReadableDatabase(), entite, null, null, null);
	}

	public <Entite> List<Entite> select(Entite entite, String orderBy) throws DataBaseException {
		return base.select(getReadableDatabase(), entite, null, null, orderBy);
	}

	public <Entite> Entite selectSingle(Entite entite) throws DataBaseException {
		List<Entite> entites = select(entite);
		if (entites.size() > 1) {
			throw new DataBaseException("Plusieurs résultats trouvés pour un selectSingle");
		}
		if (entites.isEmpty()) {
			return null;
		}
		return entites.get(0);
	}

}
