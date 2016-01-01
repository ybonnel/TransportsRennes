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

import java.io.Serializable;

import fr.ybo.transportscommun.donnees.modele.ObjetWithDistance;

/**
 * Un point de vente.
 *
 * @author ybonnel
 */
public class PointDeVente extends ObjetWithDistance implements Serializable {
    /**
     * Nom du point de vente.
     */
    public String name;
    /**
     * Téléphone du point de vente.
     */
    public String telephone;
    /**
     * Latitude du point de vente.
     */
    public double latitude;
    /**
     * Longitude du point de vente.
     */
    public double longitude;

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
