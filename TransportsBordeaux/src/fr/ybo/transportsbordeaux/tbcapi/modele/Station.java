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

import fr.ybo.transportscommun.donnees.modele.IStation;
import fr.ybo.transportscommun.donnees.modele.ObjetWithDistance;


@SuppressWarnings("serial")
public class Station extends ObjetWithDistance implements Serializable, IStation {
	public int id;
    public String name;
    public String address;
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

	/* (non-Javadoc)
	 * @see fr.ybo.transportscommun.donnees.modele.IStation#getBikesAvailables()
	 */
	@Override
	public int getBikesAvailables() {
		return availableBikes;
	}

	/* (non-Javadoc)
	 * @see fr.ybo.transportscommun.donnees.modele.IStation#getSlotsAvailables()
	 */
	@Override
	public int getSlotsAvailables() {
		return freeSlots;
	}

	/* (non-Javadoc)
	 * @see fr.ybo.transportscommun.donnees.modele.IStation#getName()
	 */
	@Override
	public String getName() {
		return name;
	}
}
