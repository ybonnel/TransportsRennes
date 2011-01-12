/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

import android.database.sqlite.SQLiteDatabase;
import fr.ybo.transportsrennes.keolis.gtfs.database.DataBaseException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Base {

	private final Map<Class<?>, Table> mapClassTable = new HashMap<Class<?>, Table>();

	public Base(final List<Class<?>> classes) throws DataBaseException {
		for (final Class<?> clazz : classes) {
			mapClassTable.put(clazz, new Table(clazz));
		}
	}

	public void createDataBase(final SQLiteDatabase db) {
		for (final Table table : mapClassTable.values()) {
			table.createTable(db);
		}
	}

	public <Entite> void delete(final SQLiteDatabase db, final Entite entite) throws DataBaseException {
		final Class<?> clazz = entite.getClass();
		if (!mapClassTable.containsKey(clazz)) {
			throw new DataBaseException("La classe " + clazz.getSimpleName() + " n'est pas gérée par la base.");
		}
		mapClassTable.get(clazz).delete(db, entite);
	}

	public <Entite> void deleteAll(final SQLiteDatabase db, final Class<Entite> clazz) throws DataBaseException {
		if (!mapClassTable.containsKey(clazz)) {
			throw new DataBaseException("La classe " + clazz.getSimpleName() + " n'est pas gérée par la base.");
		}
		mapClassTable.get(clazz).delete(db);
	}

	public void dropDataBase(final SQLiteDatabase db) {
		for (final Table table : mapClassTable.values()) {
			db.execSQL("DROP TABLE IF EXISTS " + table.getName());
		}
	}

	public Table getTable(final Class<?> clazz) throws DataBaseException {
		if (!mapClassTable.containsKey(clazz)) {
			throw new DataBaseException("La classe " + clazz.getSimpleName() + " n'est pas gérée par la base.");
		}
		return new Table(mapClassTable.get(clazz));
	}

	public <Entite> void insert(final SQLiteDatabase db, final Entite entite) throws DataBaseException {
		final Class<?> clazz = entite.getClass();
		if (!mapClassTable.containsKey(clazz)) {
			throw new DataBaseException("La classe " + clazz.getSimpleName() + " n'est pas gérée par la base.");
		}
		mapClassTable.get(clazz).insert(db, entite);
	}

	public <Entite> List<Entite> select(final SQLiteDatabase db, final Entite entite, final String selection,
	                                    final List<String> selectionArgs, final String orderBy) throws DataBaseException {
		final Class<?> clazz = entite.getClass();
		if (!mapClassTable.containsKey(clazz)) {
			throw new DataBaseException("La classe " + clazz.getSimpleName() + " n'est pas gérée par la base.");
		}
		return mapClassTable.get(clazz).select(db, entite, selection, selectionArgs, orderBy);
	}

}
