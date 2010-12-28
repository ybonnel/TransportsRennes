package fr.ybo.transportsrennes.keolis.gtfs.database.modele;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import fr.ybo.transportsrennes.keolis.gtfs.database.DataBaseException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class Table {

	private final List<Colonne> colonnes = new ArrayList<Colonne>();
	private String name;
	private String primaryKeyWhere;
	private String[] columns;
	private Constructor<?> constructor;

	protected Table(final Class<?> clazz) throws DataBaseException {
		final fr.ybo.transportsrennes.keolis.gtfs.annotation.Table table =
				clazz.getAnnotation(fr.ybo.transportsrennes.keolis.gtfs.annotation.Table.class);
		if (table == null) {
			throw new DataBaseException("La classe " + clazz.getSimpleName() + " ne contient pas l'annoation @Table");
		}
		name = table.value();
		if ("".equals(name)) {
			name = clazz.getSimpleName();
		}
		for (final Field field : clazz.getDeclaredFields()) {
			if (field.getAnnotation(fr.ybo.transportsrennes.keolis.gtfs.annotation.Colonne.class) != null) {
				colonnes.add(new Colonne(field, name));
			}
		}
		try {
			constructor = clazz.getConstructor((Class<?>[]) null);
		} catch (final SecurityException e) {
			throw new DataBaseException(e);
		} catch (final NoSuchMethodException e) {
			throw new DataBaseException(e);
		}
	}

	public Table(final Table table) {
		for (final Colonne colonne : table.colonnes) {
			colonnes.add(new Colonne(colonne));
		}
		name = table.name;
		primaryKeyWhere = table.primaryKeyWhere;
		columns = table.columns;
		constructor = table.constructor;
	}

	public void addSuffixeToTableName(final String suffixe) {
		name = name.concat(suffixe);
		for (final Colonne colonne : colonnes) {
			colonne.setTableName(name);
		}
	}

	public void createTable(final SQLiteDatabase db) {
		final StringBuilder requete = new StringBuilder();
		requete.append("CREATE TABLE ");
		requete.append(name);
		requete.append(" (");
		final List<String> indexes = new ArrayList<String>();
		final StringBuilder primaryKeys = new StringBuilder();
		boolean first = true;
		for (final Colonne colonne : colonnes) {
			if (!first) {
				requete.append(",");
			}
			requete.append(colonne.getSqlDefinition());
			if (colonne.isPrimaryKey()) {
				if (primaryKeys.length() == 0) {
					primaryKeys.append(",PRIMARY KEY (");
				} else {
					primaryKeys.append(',');
				}
				primaryKeys.append(colonne.getName());
			}
			if (colonne.isIndexed()) {
				indexes.add(colonne.getIndexSqlDef());
			}
			first = false;
		}
		if (primaryKeys.length() > 0) {
			requete.append(primaryKeys.toString());
			requete.append(')');
		}
		requete.append(");");
		db.execSQL(requete.toString());
		for (final String requeteIndex : indexes) {
			db.execSQL(requeteIndex);
		}
	}

	protected void delete(final SQLiteDatabase db) {
		db.delete(name, null, null);
	}

	protected <Entite> void delete(final SQLiteDatabase db, final Entite entite) throws DataBaseException {
		List<String> where = generePrimaryKeyWhere(entite);
		db.delete(name, getPrimaryKeyWhere(), where.toArray(new String[where.size()]));
	}

	public void dropTable(final SQLiteDatabase db) {
		final StringBuilder requete = new StringBuilder();
		requete.append("DROP TABLE IF EXISTS ");
		requete.append(name);
		requete.append(';');
		db.execSQL(requete.toString());
	}

	private <Entite> List<String> generePrimaryKeyWhere(final Entite entite) throws DataBaseException {
		final List<String> whereArgs = new ArrayList<String>();
		for (final Colonne colonne : colonnes) {
			if (colonne.isPrimaryKey()) {
				whereArgs.add(colonne.getValueToString(entite));
			}
		}
		return whereArgs;
	}

	private String[] getColumns() {
		if (columns == null) {
			columns = new String[colonnes.size()];
			for (int count = 0; count < colonnes.size(); count++) {
				columns[count] = colonnes.get(count).getName();
			}
		}
		return columns;
	}

	protected String getName() {
		return name;
	}

	protected Object getNewEntite() throws DataBaseException {
		try {
			return constructor.newInstance((Object[]) null);
		} catch (final IllegalArgumentException e) {
			throw new DataBaseException(e);
		} catch (final InstantiationException e) {
			throw new DataBaseException(e);
		} catch (final IllegalAccessException e) {
			throw new DataBaseException(e);
		} catch (final InvocationTargetException e) {
			throw new DataBaseException(e);
		}
	}

	private String getPrimaryKeyWhere() {
		if (primaryKeyWhere == null) {
			final StringBuilder where = new StringBuilder();
			boolean first = true;
			for (final Colonne colonne : colonnes) {
				if (colonne.isPrimaryKey()) {
					if (!first) {
						where.append(" AND ");
					}
					where.append(colonne.getName());
					where.append(" = :");
					where.append(colonne.getName());
					first = false;
				}
			}
			primaryKeyWhere = where.toString();
		}
		return primaryKeyWhere;
	}

	public <Entite> void insert(final SQLiteDatabase db, final Entite entite) throws DataBaseException {
		final ContentValues values = new ContentValues();
		for (final Colonne colonne : colonnes) {
			colonne.ajoutValeur(values, entite);
		}
		db.insertOrThrow(name, null, values);
	}

	@SuppressWarnings("unchecked")
	protected <Entite> List<Entite> select(SQLiteDatabase db, Entite entite, String selectionPlus, List<String> selectArgsPlus, String orderBy)
			throws DataBaseException {
		List<Entite> entites = new ArrayList<Entite>();
		StringBuilder whereClause = new StringBuilder();
		List<String> selectionArgsList = new ArrayList<String>();
		for (Colonne colonne : colonnes) {
			colonne.appendWhereIfNotNull(whereClause, entite, selectionArgsList);
		}
		if (selectionPlus != null) {
			whereClause.append(" AND (");
			whereClause.append(selectionPlus);
			whereClause.append(')');
		}
		String selection = whereClause.length() > 0 ? whereClause.toString() : null;
		if (selectArgsPlus != null) {
			selectionArgsList.addAll(selectArgsPlus);
		}
		String[] selectionArgs = selection == null ? null : selectionArgsList.toArray(new String[selectionArgsList.size()]);
		Cursor cursor = db.query(name, getColumns(), selection, selectionArgs, null, null, orderBy);
		Entite newEntite;
		while (cursor.moveToNext()) {
			newEntite = (Entite) getNewEntite();
			for (Colonne colonne : colonnes) {
				colonne.remplirEntite(cursor, newEntite);
			}
			entites.add(newEntite);
		}
		cursor.close();
		return entites;
	}

}
