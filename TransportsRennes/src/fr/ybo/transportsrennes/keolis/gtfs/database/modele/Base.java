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

import android.database.sqlite.SQLiteDatabase;
import fr.ybo.transportsrennes.keolis.gtfs.database.DataBaseException;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Base {

	private final Map<Class<?>, Table> mapClassTable = new HashMap<Class<?>, Table>(10);

	public Base(Iterable<Class<?>> classes) throws DataBaseException {
		for (Class<?> clazz : classes) {
			mapClassTable.put(clazz, new Table(clazz));
		}
	}

	public void createDataBase(SQLiteDatabase db) {
		for (Table table : mapClassTable.values()) {
			table.createTable(db);
		}
	}

	public <Entite> void delete(SQLiteDatabase db, Entite entite) throws DataBaseException {
		Class<?> clazz = entite.getClass();
		if (!mapClassTable.containsKey(clazz)) {
			throw new DataBaseException("La classe " + clazz.getSimpleName() + " n'est pas gérée par la base.");
		}
		mapClassTable.get(clazz).delete(db, entite);
	}

	public <Entite> void deleteAll(SQLiteDatabase db, Class<Entite> clazz) throws DataBaseException {
		if (!mapClassTable.containsKey(clazz)) {
			throw new DataBaseException("La classe " + clazz.getSimpleName() + " n'est pas gérée par la base.");
		}
		mapClassTable.get(clazz).delete(db);
	}

	public void dropDataBase(SQLiteDatabase db) {
		for (Table table : mapClassTable.values()) {
			db.execSQL("DROP TABLE IF EXISTS " + table.getName());
		}
	}

	public Table getTable(Class<?> clazz) throws DataBaseException {
		if (!mapClassTable.containsKey(clazz)) {
			throw new DataBaseException("La classe " + clazz.getSimpleName() + " n'est pas gérée par la base.");
		}
		return new Table(mapClassTable.get(clazz));
	}

	public <Entite> void insert(SQLiteDatabase db, Entite entite) throws DataBaseException {
		Class<?> clazz = entite.getClass();
		if (!mapClassTable.containsKey(clazz)) {
			throw new DataBaseException("La classe " + clazz.getSimpleName() + " n'est pas gérée par la base.");
		}
		mapClassTable.get(clazz).insert(db, entite);
	}

	@SuppressWarnings({"SameParameterValue"})
	public <Entite> List<Entite> select(SQLiteDatabase db, Entite entite, String selection, Collection<String> selectionArgs, String orderBy)
			throws DataBaseException {
		Class<?> clazz = entite.getClass();
		if (!mapClassTable.containsKey(clazz)) {
			throw new DataBaseException("La classe " + clazz.getSimpleName() + " n'est pas gérée par la base.");
		}
		return mapClassTable.get(clazz).select(db, entite, selection, selectionArgs, orderBy);
	}

}
