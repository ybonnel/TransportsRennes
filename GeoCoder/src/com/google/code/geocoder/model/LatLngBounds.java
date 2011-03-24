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

package com.google.code.geocoder.model;

/**
 * Limites (cadre de latitude/longitude).
 */
public class LatLngBounds {
	/**
	 * Coin sud-ouest.
	 */
	private LatLng southwest;
	/**
	 * Coin nord-est.
	 */
	private LatLng northeast;

	/**
	 * Construteur.
	 */
	public LatLngBounds() {
	}

	/**
	 * Constructeur.
	 * 
	 * @param southwest
	 *            coin sud-ouest.
	 * @param northeast
	 *            coin nord-est.
	 */
	public LatLngBounds(LatLng southwest, LatLng northeast) {
		this.southwest = southwest;
		this.northeast = northeast;
	}

	/**
	 * @return the southwest
	 */
	public LatLng getSouthwest() {
		return southwest;
	}

	/**
	 * @param southwest
	 *            the southwest to set
	 */
	public void setSouthwest(LatLng southwest) {
		this.southwest = southwest;
	}

	/**
	 * @return the northeast
	 */
	public LatLng getNortheast() {
		return northeast;
	}

	/**
	 * @param northeast
	 *            the northeast to set
	 */
	public void setNortheast(LatLng northeast) {
		this.northeast = northeast;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("LatLngBounds");
		stringBuilder.append("{southwest=").append(southwest);
		stringBuilder.append(", northeast=").append(northeast);
		stringBuilder.append('}');
		return stringBuilder.toString();
	}
}