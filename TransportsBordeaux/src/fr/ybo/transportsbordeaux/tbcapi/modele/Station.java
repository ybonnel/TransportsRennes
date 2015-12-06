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
package fr.ybo.transportsbordeaux.tbcapi.modele;

import java.io.Serializable;
import java.util.Comparator;

import fr.ybo.transportscommun.donnees.modele.IStation;
import fr.ybo.transportscommun.donnees.modele.ObjetWithDistance;


public class Station extends ObjetWithDistance implements Serializable, IStation {
	public int id;
    public String name;
    public double longitude;
    public double latitude;
    public int availableBikes;
    public int freeSlots;
    public boolean isOpen;

    @Override
    public double getLatitude() {
        return latitude;
    }

    @Override
    public double getLongitude() {
        return longitude;
    }

    public static class StationComparator implements Comparator<Station> {
		@Override
		public int compare(final Station o1, final Station o2) {
			return o1.name.compareToIgnoreCase(o2.name);
		}
	}
}
