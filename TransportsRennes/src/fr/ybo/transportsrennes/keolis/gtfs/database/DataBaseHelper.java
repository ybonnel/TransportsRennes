package fr.ybo.transportsrennes.keolis.gtfs.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import fr.ybo.transportsrennes.keolis.gtfs.database.modele.Base;
import fr.ybo.transportsrennes.keolis.gtfs.modele.VeloFavori;
import fr.ybo.transportsrennes.util.LogYbo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataBaseHelper extends SQLiteOpenHelper {

	private static final LogYbo LOG_YBO = new LogYbo(DataBaseHelper.class);
	public static final String DATABASE_NAME = "keolis.db";
	public static final int DATABASE_VERSION = 3;

	private final Base base;

	private boolean transactionOpen = false;

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

	private static interface UpgradeDatabase {
		void upagrade(SQLiteDatabase db);
	}

	private Map<Integer, UpgradeDatabase> mapUpgrades = null;

	private Map<Integer, UpgradeDatabase> getUpgrades() {
		if (mapUpgrades == null) {
			mapUpgrades = new HashMap<Integer, UpgradeDatabase>();
			mapUpgrades.put(2, new UpgradeDatabase() {
				public void upagrade(SQLiteDatabase db) {
					base.getTable(VeloFavori.class).createTable(db);
				}
			});
			mapUpgrades.put(3, new UpgradeDatabase(){
				public void upagrade(SQLiteDatabase db) {
					base.dropDataBase(db);
					base.createDataBase(db);
				}
			});
		}
		return mapUpgrades;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		LOG_YBO.debug("Demande de mise à jour de la base de la version " + oldVersion + " à la version " + newVersion);
		for (int version = oldVersion + 1; version <= newVersion; version++) {
			if (getUpgrades().containsKey(version)) {
				LOG_YBO.debug("Lancement de la mise à jour pour la version " + version);
				getUpgrades().get(version).upagrade(db);
			}
		}
	}

	public <Entite> List<Entite> select(final Entite entite) throws DataBaseException {
		return base.select(getReadableDatabase(), entite, null, null, null);
	}

	public <Entite> List<Entite> select(final Entite entite, final String selection, final List<String> selectionArgs, final String orderBy)
			throws DataBaseException {
		return base.select(getReadableDatabase(), entite, selection, selectionArgs, orderBy);
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
