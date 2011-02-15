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

package fr.ybo.transportsrennes.map;

import fr.ybo.transportsrennes.keolis.modele.ObjetWithDistance;
import fr.ybo.transportsrennes.map.mapviewutil.GeoItem;

public class MyGeoItem<Objet extends ObjetWithDistance> extends GeoItem {

	private final Objet objet;

	public MyGeoItem(long id, Objet objet) {
		super(id, (int) (objet.getLatitude() * 1.00E6), (int) (objet.getLongitude() * 1.00E6));
		this.objet = objet;
	}

	public Objet getObjet() {
		return objet;
	}
}
