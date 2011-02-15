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
public class LatLngBounds {
	private LatLng southwest;
	private LatLng northeast;

	public LatLngBounds() {
		super();
	}

	public LatLngBounds(final LatLng southwest, final LatLng northeast) {
		super();
		this.southwest = southwest;
		this.northeast = northeast;
	}

	public LatLng getSouthwest() {
		return southwest;
	}

	public void setSouthwest(final LatLng southwest) {
		this.southwest = southwest;
	}

	public LatLng getNortheast() {
		return northeast;
	}

	public void setNortheast(final LatLng northeast) {
		this.northeast = northeast;
	}

	/**
	 * @return Returns a string of the form "lat_lo,lng_lo,lat_hi,lng_hi" for this bounds, where "lo" corresponds to the southwest corner of the bounding box, while "hi" corresponds to the northeast corner of that box.
	 */
	public String toUrlValue() {
		return southwest.toUrlValue(6) + "," + northeast.toUrlValue(6);
	}


	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}

		final LatLngBounds that = (LatLngBounds) obj;

		return !(northeast != null ? !northeast.equals(that.northeast) : that.northeast != null) &&
				!(southwest != null ? !southwest.equals(that.southwest) : that.southwest != null);

	}

	@Override
	public int hashCode() {
		int result = southwest != null ? southwest.hashCode() : 0;
		result = 31 * result + (northeast != null ? northeast.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("LatLngBounds");
		sb.append("{southwest=").append(southwest);
		sb.append(", northeast=").append(northeast);
		sb.append('}');
		return sb.toString();
	}
}