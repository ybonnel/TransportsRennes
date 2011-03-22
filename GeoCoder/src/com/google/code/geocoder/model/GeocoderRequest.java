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
public class GeocoderRequest {
	private String address;         //Address. Optional.
	private String language;        //Preferred language for results. Optional.
	private String region;          //Country code top-level domain within which to search. Optional.
	private LatLngBounds bounds;    //LatLngBounds within which to search. Optional.
	private LatLng location;        //LatLng about which to search. Optional.

	public GeocoderRequest() {
	}

	public GeocoderRequest(String address, String language) {
		this.address = address;
		this.language = language;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public LatLngBounds getBounds() {
		return bounds;
	}

	public void setBounds(LatLngBounds bounds) {
		this.bounds = bounds;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public LatLng getLocation() {
		return location;
	}

	public void setLocation(LatLng location) {
		this.location = location;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("GeocoderRequest");
		sb.append("{address='").append(address).append('\'');
		sb.append(", bounds=").append(bounds);
		sb.append(", language='").append(language).append('\'');
		sb.append(", location=").append(location);
		sb.append(", region='").append(region).append('\'');
		sb.append('}');
		return sb.toString();
	}
}