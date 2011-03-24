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
 * Réponse du géocodage.
 * @author ybonnel
 *
 */
public class GeocodeResponse {
	/**
	 * Status.
	 */
	private GeocoderStatus status;
	/**
	 * Résultats.
	 */
	private List<GeocoderResult> results;

	/**
	 * @return status.
	 */
	public GeocoderStatus getStatus() {
		return status;
	}

	/**
	 * 
	 * @param status le status.
	 */
	public void setStatus(GeocoderStatus status) {
		this.status = status;
	}

	/**
	 * @return les résultats.
	 */
	public List<GeocoderResult> getResults() {
		return results;
	}

	/**
	 * 
	 * @param result les résultats.
	 */
	public void setResults(List<GeocoderResult> result) {
		results = result;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("GeocodeResponse");
		stringBuilder.append("{status=").append(status);
		stringBuilder.append(", results=").append(results);
		stringBuilder.append('}');
		return stringBuilder.toString();
	}
}