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

import java.math.BigDecimal;

import com.google.code.geocoder.util.Constantes;

/**
 * Latitude/Longitude.
 * 
 * @author ybonnel
 * 
 */
public class LatLng {
	/**
	 * Latitude.
	 */
	private BigDecimal lat;
	/**
	 * Longitude.
	 */
	private BigDecimal lng;

	/**
	 * Constructeur.
	 */
	public LatLng() {
	}

	/**
	 * Constructeur.
	 * 
	 * @param lat
	 *            latitude.
	 * @param lng
	 *            longitude.
	 */
	public LatLng(BigDecimal lat, BigDecimal lng) {
		this.lat = lat;
		this.lng = lng;
	}

	/**
	 * Constructeur.
	 * 
	 * @param lat
	 *            latitude.
	 * @param lng
	 *            longitude.
	 */
	public LatLng(String lat, String lng) {
		this.lat = new BigDecimal(lat);
		this.lng = new BigDecimal(lng);
	}


	/**
	 * @return the lat
	 */
	public BigDecimal getLat() {
		return lat;
	}

	/**
	 * @param lat
	 *            the lat to set
	 */
	public void setLat(BigDecimal lat) {
		this.lat = lat;
	}

	/**
	 * @return the lng
	 */
	public BigDecimal getLng() {
		return lng;
	}

	/**
	 * @param lng
	 *            the lng to set
	 */
	public void setLng(BigDecimal lng) {
		this.lng = lng;
	}

	/**
	 * @return Returns a string of the form "lat,lng" for this LatLng. We round
	 *         the lat/lng values to 6 decimal places by default.
	 */
	public String toUrlValue() {
		return toUrlValue(Constantes.DEFAULT_PRECISION);
	}

	/**
	 * @param precision We round the lat/lng values
	 * @return Returns a string of the form "lat,lng" for this LatLng.
	 */
	public String toUrlValue(int precision) {
		return lat.setScale(precision, BigDecimal.ROUND_HALF_EVEN).toString() + ','
				+ lng.setScale(precision, BigDecimal.ROUND_HALF_EVEN).toString();
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("LatLng");
		stringBuilder.append("{lat=").append(lat);
		stringBuilder.append(", lng=").append(lng);
		stringBuilder.append('}');
		return stringBuilder.toString();
	}
}