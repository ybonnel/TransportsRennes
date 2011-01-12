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
	public static final String DATABASE_NAME = "keolis.db";
	public static final int DATABASE_VERSION = 5;

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
			mapUpgrades.put(3, new UpgradeDatabase() {
				public void upagrade(SQLiteDatabase db) {
					base.dropDataBase(db);
					base.createDataBase(db);
				}
			});
			mapUpgrades.put(4, new UpgradeDatabase() {
				public void upagrade(SQLiteDatabase db) {
					db.execSQL("ALTER TABLE ArretFavori ADD COLUMN ordre INTEGER");
					int count = 0;
					for (ArretFavori arretFavori : base.select(db, new ArretFavori(), null, null, null)) {
						arretFavori.ordre = count++;
						base.delete(db, arretFavori);
						base.insert(db, arretFavori);
					}
				}
			});
			mapUpgrades.put(5, new UpgradeDatabase() {
				public void upagrade(SQLiteDatabase db) {
					Cursor cursor =
							db.query("sqlite_master", Collections.singleton("name").toArray(new String[1]), " type = 'table'", null, null, null,
									null);
					while (cursor.moveToNext()) {
						String tableName = cursor.getString(0);
						if (!tableName.equals("android_metadata") && !tableName.equals("VeloFavori") && !tableName.equals("ArretFavori")) {
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
					List<String> columns = new ArrayList<String>();
					columns.add("stopId");
					columns.add("routeId");
					columns.add("nomArret");
					columns.add("direction");
					columns.add("routeNomCourt");
					columns.add("routeNomLong");
					columns.add("ordre");
					cursor =
							db.query("ArretFavori_tmp", columns.toArray(new String[7]), null, null, null, null,
									null);
					int arretIdIndex = cursor.getColumnIndex("stopId");
					int ligneIdIndex = cursor.getColumnIndex("routeId");
					int nomArretIndex = cursor.getColumnIndex("nomArret");
					int directionIndex = cursor.getColumnIndex("direction");
					int nomCourtIndex = cursor.getColumnIndex("routeNomCourt");
					int nomLongIndex = cursor.getColumnIndex("routeNomLong");
					int ordreIndex = cursor.getColumnIndex("ordre");
					ArretFavori favori = new ArretFavori();
					while (cursor.moveToNext()) {
						favori.arretId = cursor.getString(arretIdIndex);
						favori.ligneId = cursor.getString(ligneIdIndex);
						favori.nomArret = cursor.getString(nomArretIndex);
						favori.direction = cursor.getString(directionIndex);
						favori.nomCourt = cursor.getString(nomCourtIndex);
						favori.nomLong = cursor.getString(nomLongIndex);
						favori.ordre = cursor.getInt(ordreIndex);
						base.insert(db, favori);
					}
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
