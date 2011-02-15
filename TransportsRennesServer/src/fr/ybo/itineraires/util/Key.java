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

package fr.ybo.itineraires.util;


import fr.ybo.itineraires.bean.ItineraireException;

public class Key {

    private static final String[] KEYS = { "xxxxx" };

	private Key() {
		super();
	}

	public static void valid(final String key) {
        if (key == null) {
            throw new ItineraireException("La clé doit être valorisée");
        }
        for (final String validKey : KEYS) {
            if (validKey.equals(key)) {
                return;
            }
        }
        throw new ItineraireException("La clé n'est pas bonne");
    }

}
