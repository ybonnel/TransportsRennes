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

package fr.ybo.transportsrennes.keolis.gtfs.database.modele;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import fr.ybo.transportsrennes.keolis.gtfs.database.DataBaseException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Table {

	private final List<Colonne> colonnes = new ArrayList<Colonne>(10);
	private String name;
	private String primaryKeyWhere;
	private String[] columns;
	private final Constructor<?> constructor;

	Table(final Class<?> clazz) throws DataBaseException {
		super();
		final fr.ybo.transportsrennes.keolis.gtfs.annotation.Table table =
				clazz.getAnnotation(fr.ybo.transportsrennes.keolis.gtfs.annotation.Table.class);
		if (table == null) {
			throw new DataBaseException("La classe " + clazz.getSimpleName() + " ne contient pas l'annotation @Table");
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
		super();
		for (final Colonne colonne : table.colonnes) {
			colonnes.add(new Colonne(colonne));
		}
		name = table.name;
		primaryKeyWhere = table.primaryKeyWhere;
		columns = table.columns;
		constructor = table.constructor;
	}

	public void addSuffixeToTableName(final String suffixe) {
		name = name + "_" + suffixe;
		for (final Colonne colonne : colonnes) {
			colonne.setTableName(name);
		}
	}

	public void createTable(final SQLiteDatabase db) {
		final StringBuilder requete = new StringBuilder();
		requete.append("CREATE TABLE ");
		requete.append(name);
		requete.append(" (");
		final Collection<String> indexes = new ArrayList<String>(2);
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

	void delete(final SQLiteDatabase db) {
		db.delete(name, null, null);
	}

	<Entite> void delete(final SQLiteDatabase db, final Entite entite) throws DataBaseException {
		final List<String> where = generePrimaryKeyWhere(entite);
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
		final List<String> whereArgs = new ArrayList<String>(3);
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

	String getName() {
		return name;
	}

	Object getNewEntite() throws DataBaseException {
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
	<Entite> List<Entite> select(final SQLiteDatabase db, final Entite entite, final String selectionPlus, final Collection<String> selectArgsPlus, final String orderBy)
			throws DataBaseException {
		final List<Entite> entites = new ArrayList<Entite>(50);
		final StringBuilder whereClause = new StringBuilder();
		final List<String> selectionArgsList = new ArrayList<String>(selectArgsPlus == null ? 0 : selectArgsPlus.size());
		for (final Colonne colonne : colonnes) {
			colonne.appendWhereIfNotNull(whereClause, entite, selectionArgsList);
		}
		if (selectionPlus != null) {
			whereClause.append(" AND (");
			whereClause.append(selectionPlus);
			whereClause.append(')');
		}
		final String selection = whereClause.length() > 0 ? whereClause.toString() : null;
		if (selectArgsPlus != null) {
			selectionArgsList.addAll(selectArgsPlus);
		}
		final String[] selectionArgs = selection == null ? null : selectionArgsList.toArray(new String[selectionArgsList.size()]);
		final Cursor cursor = db.query(name, getColumns(), selection, selectionArgs, null, null, orderBy);
		while (cursor.moveToNext()) {
			Entite newEntite = (Entite) getNewEntite();
			for (final Colonne colonne : colonnes) {
				colonne.remplirEntite(cursor, newEntite);
			}
			entites.add(newEntite);
		}
		cursor.close();
		return entites;
	}

}
