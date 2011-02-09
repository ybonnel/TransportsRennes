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

package fr.ybo.gtfs.csv.moteur.modele;

import fr.ybo.gtfs.csv.moteur.ErreurMoteurCsv;
import fr.ybo.gtfs.csv.moteur.adapter.AdapterCsv;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ChampCsv {

	private final Class<? extends AdapterCsv<?>> adapter;
	private static Map<Class<? extends AdapterCsv<?>>, AdapterCsv<?>> mapAdapters = new HashMap<Class<? extends AdapterCsv<?>>, AdapterCsv<?>>();
	private final Field field;

	public ChampCsv(final Class<? extends AdapterCsv<?>> adapter, final Field field) {
		super();
		this.adapter = adapter;
		this.field = field;
	}

	public Field getField() {
		return field;
	}

	public AdapterCsv<?> getNewAdapterCsv() {
		if (!mapAdapters.containsKey(adapter)) {
			try {
				final Constructor<? extends AdapterCsv<?>> construteur = adapter.getConstructor((Class<?>[]) null);
				mapAdapters.put(adapter, construteur.newInstance((Object[]) null));
			} catch (final Exception exception) {
				throw new ErreurMoteurCsv(exception);
			}
		}
		return mapAdapters.get(adapter);
	}
}
