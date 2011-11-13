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
package fr.ybo.transportsrennes.keolis.modele.bus;

import fr.ybo.transportsrennes.keolis.modele.ObjetWithDistance;

import java.io.Serializable;

/**
 * @author ybonnel
 */
@SuppressWarnings("serial")
public class ParkRelai extends ObjetWithDistance implements Serializable {

    /**
     * name.
     */
    public String name;
    /**
     * latitude.
     */
    public double latitude;
    /**
     * longitude.
     */
    public double longitude;
    /**
     * carParkAvailable.
     */
    public Integer carParkAvailable;
    /**
     * carParkCapacity.
     */
    public Integer carParkCapacity;
    /**
     * lastupdate.
     */
    public String lastupdate;
    /**
     * state.
     */
    public Integer state;

    /**
     * @return the latitude
     */
    @Override
    public double getLatitude() {
        return latitude;
    }

    /**
     * @return the longitude
     */
    @Override
    public double getLongitude() {
        return longitude;
    }
}
