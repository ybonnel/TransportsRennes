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

import java.util.List;

/**
 * @author <a href="mailto:panchmp@gmail.com">Michael Panchenko</a>
 */
public class GeocodeResponse {
	protected GeocoderStatus status;
	protected List<GeocoderResult> results;

	public GeocodeResponse() {
	}

	public GeocoderStatus getStatus() {
		return status;
	}

	public void setStatus(GeocoderStatus status) {
		this.status = status;
	}

	public List<GeocoderResult> getResults() {
		return results;
	}

	public void setResults(List<GeocoderResult> result) {
		this.results = result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		GeocodeResponse that = (GeocodeResponse) o;

		return !(results != null ? !results.equals(that.results) : that.results != null) && status == that.status;

	}

	@Override
	public int hashCode() {
		int result = status != null ? status.hashCode() : 0;
		result = 31 * result + (results != null ? results.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("GeocodeResponse");
		sb.append("{status=").append(status);
		sb.append(", results=").append(results);
		sb.append('}');
		return sb.toString();
	}
}