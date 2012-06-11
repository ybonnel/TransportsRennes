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
package fr.ybo.transportsrennes.keolis.modele.velos;

import java.io.Serializable;

import fr.ybo.transportscommun.donnees.modele.IStation;
import fr.ybo.transportscommun.donnees.modele.ObjetWithDistance;

/**
 * Classe représentant une station de velo star.
 *
 * @author ybonnel
 */
@SuppressWarnings("serial")
public class Station extends ObjetWithDistance implements Serializable, IStation {
    /**
     * Numéro de la station.
     */
    public String number;
    /**
     * Nom de la station.
     */
    public String name;
    /**
     * adresse de la station.
     */
    public String adresse;

    /**
     * Etat de la station.
     */
    public boolean state;

    /**
     * Latitude.
     */
    public double latitude;

    /**
     * Longitude.
     */
    public double longitude;
    /**
     * Places libres.
     */
    public int slotsavailable;
    /**
     * Vélos libres.
     */
    public int bikesavailable;
    /**
     * Position.
     */
    public boolean pos;
    /**
     * Nom du district.
     */
    public String district;
    /**
     * Date de dernière mise à jour.
     */
    public String lastupdate;

    /**
     * Getter.
     *
     * @return la latitude.
     */
    @Override
    public double getLatitude() {
        return latitude;
    }

    /**
     * Getter.
     *
     * @return la longitude.
     */
    @Override
    public double getLongitude() {
        return longitude;
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.ybo.transportscommun.donnees.modele.IStation#getBikesAvailables()
	 */
	@Override
	public int getBikesAvailables() {
		return bikesavailable;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.ybo.transportscommun.donnees.modele.IStation#getSlotsAvailables()
	 */
	@Override
	public int getSlotsAvailables() {
		return slotsavailable;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.ybo.transportscommun.donnees.modele.IStation#getName()
	 */
	@Override
	public String getName() {
		return name;
	}
}
