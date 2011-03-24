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
 * Géometry : localisation.
 * 
 * @author ybonnel
 * 
 */
public class GeocoderGeometry {
	/**
	 * Latitude et longitude.
	 */
	private LatLng location;
	/**
	 * Type de géo-localisation.
	 */
	private GeocoderLocationType locationType;
	/**
	 * Précision.
	 */
	private LatLngBounds viewport;

	/**
	 * @return the location
	 */
	public LatLng getLocation() {
		return location;
	}

	/**
	 * @param location
	 *            the location to set
	 */
	public void setLocation(LatLng location) {
		this.location = location;
	}

	/**
	 * @return the locationType
	 */
	public GeocoderLocationType getLocationType() {
		return locationType;
	}

	/**
	 * @param locationType
	 *            the locationType to set
	 */
	public void setLocationType(GeocoderLocationType locationType) {
		this.locationType = locationType;
	}

	/**
	 * @return the viewport
	 */
	public LatLngBounds getViewport() {
		return viewport;
	}

	/**
	 * @param viewport
	 *            the viewport to set
	 */
	public void setViewport(LatLngBounds viewport) {
		this.viewport = viewport;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("GeocoderGeometry");
		stringBuilder.append("{location=").append(location);
		stringBuilder.append(", locationType=").append(locationType);
		stringBuilder.append(", viewport=").append(viewport);
		stringBuilder.append('}');
		return stringBuilder.toString();
	}
}