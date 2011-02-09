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
	private LatLng southwest, northeast;

	public LatLngBounds() {
	}

	public LatLngBounds(final LatLng southwest, final LatLng northeast) {
		this.southwest = southwest;
		this.northeast = northeast;
	}

	public LatLng getSouthwest() {
		return southwest;
	}

	public void setSouthwest(LatLng southwest) {
		this.southwest = southwest;
	}

	public LatLng getNortheast() {
		return northeast;
	}

	public void setNortheast(LatLng northeast) {
		this.northeast = northeast;
	}

	/**
	 * @return Returns a string of the form "lat_lo,lng_lo,lat_hi,lng_hi" for this bounds, where "lo" corresponds to the southwest corner of the bounding box, while "hi" corresponds to the northeast corner of that box.
	 */
	public String toUrlValue() {
		return getSouthwest().toUrlValue(6) + "," + getNortheast().toUrlValue(6);
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		LatLngBounds that = (LatLngBounds) o;

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