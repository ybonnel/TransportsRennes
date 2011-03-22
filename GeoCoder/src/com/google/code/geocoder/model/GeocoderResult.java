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
public class GeocoderResult {
	private List<String> types;
	private String formattedAddress;
	private List<GeocoderAddressComponent> addressComponents;
	private GeocoderGeometry geometry;

	public List<String> getTypes() {
		return types;
	}

	public void setTypes(List<String> types) {
		this.types = types;
	}

	public String getFormattedAddress() {
		return formattedAddress;
	}

	public void setFormattedAddress(String formattedAddress) {
		this.formattedAddress = formattedAddress;
	}

	public List<GeocoderAddressComponent> getAddressComponents() {
		return addressComponents;
	}

	public void setAddressComponents(List<GeocoderAddressComponent> addressComponents) {
		this.addressComponents = addressComponents;
	}

	public GeocoderGeometry getGeometry() {
		return geometry;
	}

	public void setGeometry(GeocoderGeometry geometry) {
		this.geometry = geometry;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("GeocoderResult");
		sb.append("{types=").append(types);
		sb.append(", formattedAddress='").append(formattedAddress).append('\'');
		sb.append(", addressComponents=").append(addressComponents);
		sb.append(", geometry=").append(geometry);
		sb.append('}');
		return sb.toString();
	}
}