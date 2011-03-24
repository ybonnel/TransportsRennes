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
 * Réquète pour le géo-codage.
 * 
 * @author ybonnel
 * 
 */
public class GeocoderRequest {
	/**
	 * Address. Optional.
	 */
	private String address;
	/**
	 * Preferred language for results. Optional.
	 */
	private String language;
	/**
	 * Country code top-level domain within which to search. Optional.
	 */
	private String region;
	/**
	 * LatLngBounds within which to search. Optional.
	 */
	private LatLngBounds bounds;
	/**
	 * LatLng about which to search. Optional.
	 */
	private LatLng location;

	/**
	 * Constructeur.
	 */
	public GeocoderRequest() {
	}

	/**
	 * Constructeur.
	 * 
	 * @param address
	 *            adresse.
	 * @param language
	 *            langue.
	 */
	public GeocoderRequest(String address, String language) {
		this.address = address;
		this.language = language;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address
	 *            the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * @param language
	 *            the language to set
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * @return the region
	 */
	public String getRegion() {
		return region;
	}

	/**
	 * @param region
	 *            the region to set
	 */
	public void setRegion(String region) {
		this.region = region;
	}

	/**
	 * @return the bounds
	 */
	public LatLngBounds getBounds() {
		return bounds;
	}

	/**
	 * @param bounds
	 *            the bounds to set
	 */
	public void setBounds(LatLngBounds bounds) {
		this.bounds = bounds;
	}

	/**
	 * @return the location
	 */
	public LatLng getLocation() {
		return location;
	}

	/**
	 * @param location
	 *            the location to set
	 */
	public void setLocation(LatLng location) {
		this.location = location;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("GeocoderRequest");
		stringBuilder.append("{address='").append(address).append('\'');
		stringBuilder.append(", bounds=").append(bounds);
		stringBuilder.append(", language='").append(language).append('\'');
		stringBuilder.append(", location=").append(location);
		stringBuilder.append(", region='").append(region).append('\'');
		stringBuilder.append('}');
		return stringBuilder.toString();
	}
}