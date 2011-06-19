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
 * 
 * Contributors:
 *     ybonnel - initial API and implementation
 */
package fr.ybo.transportsrenneshelper.keolis;

import fr.ybo.transportsrenneshelper.keolis.modele.MetroStation;

/**
 * Permet d'accéder aux stations de métro, sans pour autant faire un appel à Keolis à chaque fois.
 * Une sorte de proxy/cache.
 * @author ybonnel
 *
 */
public final class GetMetro {
	
	/**
	 * Constructeur privé pour empécher l'instanciation.
	 */
	private GetMetro() {
	}

	/**
	 * Stations de métro.
	 */
	private static Iterable<MetroStation> stations = null;

	/**
	 * 
	 * @return les stations de métro.
	 */
	public static Iterable<MetroStation> getStations() {
		if (stations == null) {
			stations = Keolis.getInstance().getMetroStation();
		}
		return stations;
	}
}
