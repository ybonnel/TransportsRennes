package fr.ybo.transportsrennes.keolis.gtfs.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import fr.ybo.transportsrennes.keolis.gtfs.database.modele.Base;
import fr.ybo.transportsrennes.keolis.gtfs.modele.VeloFavori;
import fr.ybo.transportsrennes.util.LogYbo;

import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {

	private static final LogYbo LOG_YBO = new LogYbo(DataBaseHelper.class);
	public static final String DATABASE_NAME = "keolis.db";
	public static final int DATABASE_VERSION = 2;

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

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion == 1 && newVersion == 2) {
			LOG_YBO.debug("Mise a jour de la base de la version " + oldVersion + " a " + newVersion + ", ajout de la table VeloFavori");
			base.getTable(VeloFavori.class).createTable(db);
		} else {
			LOG_YBO.warn(
					"Mise a jour de la base de la version " + oldVersion + " a " + newVersion + "non prevue, la base va etre supprimee et recreer");
			base.dropDataBase(db);
			base.createDataBase(db);
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
