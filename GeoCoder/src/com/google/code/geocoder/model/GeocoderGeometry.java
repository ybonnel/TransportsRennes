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
 * @author <a href="mailto:panchmp@gmail.com">Michael Panchenko</a>
 */
public class GeocoderGeometry {
	private LatLng location;
	private GeocoderLocationType locationType;
	private LatLngBounds viewport;

	public LatLng getLocation() {
		return location;
	}

	public void setLocation(LatLng location) {
		this.location = location;
	}

	public GeocoderLocationType getLocationType() {
		return locationType;
	}

	public void setLocationType(GeocoderLocationType locationType) {
		this.locationType = locationType;
	}

	public LatLngBounds getViewport() {
		return viewport;
	}

	public void setViewport(LatLngBounds viewport) {
		this.viewport = viewport;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("GeocoderGeometry");
		sb.append("{location=").append(location);
		sb.append(", locationType=").append(locationType);
		sb.append(", viewport=").append(viewport);
		sb.append('}');
		return sb.toString();
	}
}