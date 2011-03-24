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
 * Composant d'un adresse.
 * @author ybonnel
 *
 */
class GeocoderAddressComponent {
	/**
	 * Nom long.
	 */
	private String longName;
	/**
	 * Nom court.
	 */
	private String shortName;
	/**
	 * Types.
	 */
	private List<String> types;

	/**
	 * @return nom long.
	 */
	public String getLongName() {
		return longName;
	}

	/**
	 * @param longName nom long.
	 */
	public void setLongName(String longName) {
		this.longName = longName;
	}

	/**
	 * @return le nom court.
	 */
	public String getShortName() {
		return shortName;
	}

	/**
	 * @param shortName le nom court.
	 */
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	/**
	 * @return les types.
	 */
	public List<String> getTypes() {
		return types;
	}

	/**
	 * @param types les types.
	 */
	public void setTypes(List<String> types) {
		this.types = types;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("GeocoderAddressComponent");
		stringBuilder.append("{longName='").append(longName).append('\'');
		stringBuilder.append(", shortName='").append(shortName).append('\'');
		stringBuilder.append(", types=").append(types);
		stringBuilder.append('}');
		return stringBuilder.toString();
	}
}