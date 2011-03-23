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

package fr.ybo.moteurcsv.modele;

import fr.ybo.moteurcsv.adapter.AdapterCsv;
import fr.ybo.moteurcsv.exception.MoteurCsvException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ChampCsv {

	private final Class<? extends AdapterCsv<?>> adapter;
	private static final Map<Class<? extends AdapterCsv<?>>, AdapterCsv<?>> MAP_ADAPTERS =
			new HashMap<Class<? extends AdapterCsv<?>>, AdapterCsv<?>>(5);
	private final Field field;

	public ChampCsv(Class<? extends AdapterCsv<?>> adapter, Field field) {
		this.adapter = adapter;
		this.field = field;
	}

	public Class<? extends AdapterCsv<?>> getAdapter() {
		return adapter;
	}

	public Field getField() {
		return field;
	}

	@SuppressWarnings("unchecked")
	public AdapterCsv<Object> getNewAdapterCsv() {
		if (!MAP_ADAPTERS.containsKey(adapter)) {
			try {
				Constructor<? extends AdapterCsv<?>> construteur = adapter.getConstructor((Class<?>[]) null);
				MAP_ADAPTERS.put(adapter, construteur.newInstance((Object[]) null));
			} catch (Exception exception) {
				throw new MoteurCsvException(exception);
			}
		}
		return (AdapterCsv<Object>) MAP_ADAPTERS.get(adapter);
	}
}
