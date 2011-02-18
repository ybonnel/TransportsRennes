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

import java.io.Serializable;

@SuppressWarnings({"UnusedDeclaration"})
class AddressDetails implements Serializable {
	private static final long serialVersionUID = 1L;

	private String country;
	private String administrativeAreaLevel1;
	private String locality;
	private String subLocality;
	private String route;
	private String streetAddress;
	private String subPremise;

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getAdministrativeAreaLevel1() {
		return administrativeAreaLevel1;
	}

	public void setAdministrativeAreaLevel1(String administrativeAreaLevel1) {
		this.administrativeAreaLevel1 = administrativeAreaLevel1;
	}

	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	public String getSubLocality() {
		return subLocality;
	}

	public void setSubLocality(String subLocality) {
		this.subLocality = subLocality;
	}

	public String getRoute() {
		return route;
	}

	public void setRoute(String route) {
		this.route = route;
	}

	public String getStreetAddress() {
		return streetAddress;
	}

	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}

	public String getSubPremise() {
		return subPremise;
	}

	public void setSubPremise(String subPremise) {
		this.subPremise = subPremise;
	}

	@SuppressWarnings({"OverlyComplexBooleanExpression"})
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}

		AddressDetails that = (AddressDetails) obj;

		return !(administrativeAreaLevel1 != null ? !administrativeAreaLevel1.equals(that.administrativeAreaLevel1) :
				that.administrativeAreaLevel1 != null) && !(country != null ? !country.equals(that.country) : that.country != null) &&
				!(locality != null ? !locality.equals(that.locality) : that.locality != null) &&
				!(route != null ? !route.equals(that.route) : that.route != null) &&
				!(streetAddress != null ? !streetAddress.equals(that.streetAddress) : that.streetAddress != null) &&
				!(subLocality != null ? !subLocality.equals(that.subLocality) : that.subLocality != null) &&
				!(subPremise != null ? !subPremise.equals(that.subPremise) : that.subPremise != null);

	}

	@SuppressWarnings({"MethodWithMoreThanThreeNegations"})
	@Override
	public int hashCode() {
		int result = country != null ? country.hashCode() : 0;
		result = 31 * result + (administrativeAreaLevel1 != null ? administrativeAreaLevel1.hashCode() : 0);
		result = 31 * result + (locality != null ? locality.hashCode() : 0);
		result = 31 * result + (subLocality != null ? subLocality.hashCode() : 0);
		result = 31 * result + (route != null ? route.hashCode() : 0);
		result = 31 * result + (streetAddress != null ? streetAddress.hashCode() : 0);
		result = 31 * result + (subPremise != null ? subPremise.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("AddressDetails{country='");
		sb.append(country);
		sb.append("', administrativeAreaLevel1='");
		sb.append(administrativeAreaLevel1);
		sb.append("', locality='");
		sb.append(locality);
		sb.append("', subLocality='");
		sb.append(subLocality);
		sb.append("', route='");
		sb.append(route);
		sb.append("', streetAddress='");
		sb.append(streetAddress);
		sb.append("', subPremise='");
		sb.append(subPremise);
		sb.append("'}");
		return sb.toString();
	}
}
