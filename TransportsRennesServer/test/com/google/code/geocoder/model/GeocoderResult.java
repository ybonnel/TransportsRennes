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
	protected List<String> types;
	protected String formattedAddress;
	protected List<GeocoderAddressComponent> addressComponents;
	protected GeocoderGeometry geometry;

	public List<String> getTypes() {
		return types;
	}

	public void setTypes(final List<String> types) {
		this.types = types;
	}

	public String getFormattedAddress() {
		return formattedAddress;
	}

	public void setFormattedAddress(final String formattedAddress) {
		this.formattedAddress = formattedAddress;
	}

	public List<GeocoderAddressComponent> getAddressComponents() {
		return addressComponents;
	}

	public void setAddressComponents(final List<GeocoderAddressComponent> addressComponents) {
		this.addressComponents = addressComponents;
	}

	public GeocoderGeometry getGeometry() {
		return geometry;
	}

	public void setGeometry(final GeocoderGeometry geometry) {
		this.geometry = geometry;
	}

	@SuppressWarnings({"OverlyComplexBooleanExpression"})
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}

		final GeocoderResult that = (GeocoderResult) obj;

		return !(addressComponents != null ? !addressComponents.equals(that.addressComponents) : that.addressComponents != null) &&
				!(formattedAddress != null ? !formattedAddress.equals(that.formattedAddress) : that.formattedAddress != null) &&
				!(geometry != null ? !geometry.equals(that.geometry) : that.geometry != null) &&
				!(types != null ? !types.equals(that.types) : that.types != null);

	}

	@SuppressWarnings({"MethodWithMoreThanThreeNegations"})
	@Override
	public int hashCode() {
		int result = types != null ? types.hashCode() : 0;
		result = 31 * result + (formattedAddress != null ? formattedAddress.hashCode() : 0);
		result = 31 * result + (addressComponents != null ? addressComponents.hashCode() : 0);
		result = 31 * result + (geometry != null ? geometry.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("GeocoderResult");
		sb.append("{types=").append(types);
		sb.append(", formattedAddress='").append(formattedAddress).append('\'');
		sb.append(", addressComponents=").append(addressComponents);
		sb.append(", geometry=").append(geometry);
		sb.append('}');
		return sb.toString();
	}
}