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
 * Résultat du géocodage.
 * 
 * @author ybonnel
 * 
 */
public class GeocoderResult {
	/**
	 * Types.
	 */
	private List<String> types;
	/**
	 * Adresse formattée.
	 */
	private String formattedAddress;
	/**
	 * Composantes de l'adresse.
	 */
	private List<GeocoderAddressComponent> addressComponents;
	/**
	 * Geométry (localisation).
	 */
	private GeocoderGeometry geometry;

	/**
	 * @return the types
	 */
	public List<String> getTypes() {
		return types;
	}

	/**
	 * @param types
	 *            the types to set
	 */
	public void setTypes(List<String> types) {
		this.types = types;
	}

	/**
	 * @return the formattedAddress
	 */
	public String getFormattedAddress() {
		return formattedAddress;
	}

	/**
	 * @param formattedAddress
	 *            the formattedAddress to set
	 */
	public void setFormattedAddress(String formattedAddress) {
		this.formattedAddress = formattedAddress;
	}

	/**
	 * @return the addressComponents
	 */
	public List<GeocoderAddressComponent> getAddressComponents() {
		return addressComponents;
	}

	/**
	 * @param addressComponents
	 *            the addressComponents to set
	 */
	public void setAddressComponents(List<GeocoderAddressComponent> addressComponents) {
		this.addressComponents = addressComponents;
	}

	/**
	 * @return the geometry
	 */
	public GeocoderGeometry getGeometry() {
		return geometry;
	}

	/**
	 * @param geometry
	 *            the geometry to set
	 */
	public void setGeometry(GeocoderGeometry geometry) {
		this.geometry = geometry;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("GeocoderResult");
		stringBuilder.append("{types=").append(types);
		stringBuilder.append(", formattedAddress='").append(formattedAddress).append('\'');
		stringBuilder.append(", addressComponents=").append(addressComponents);
		stringBuilder.append(", geometry=").append(geometry);
		stringBuilder.append('}');
		return stringBuilder.toString();
	}
}