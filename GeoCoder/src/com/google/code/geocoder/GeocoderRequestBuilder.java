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

package com.google.code.geocoder;

import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.LatLng;
import com.google.code.geocoder.model.LatLngBounds;

/**
 * Constructeur de requètes.
 * 
 * @author ybonnel
 * 
 */
public class GeocoderRequestBuilder {
	/**
	 * Requete.
	 */
	private final GeocoderRequest geocoderRequest = new GeocoderRequest();

	/**
	 * @param address
	 *            l'adresse.
	 * @return référence au builder.
	 */
	public GeocoderRequestBuilder setAddress(String address) {
		geocoderRequest.setAddress(address);
		return this;
	}

	/**
	 * @param language
	 *            la langue.
	 * @return référence au builder.
	 */
	public GeocoderRequestBuilder setLanguage(String language) {
		geocoderRequest.setLanguage(language);
		return this;
	}

	/**
	 * 
	 * @param region
	 *            la région.
	 * @return référence au builder.
	 */
	public GeocoderRequestBuilder setRegion(String region) {
		geocoderRequest.setRegion(region);
		return this;
	}

	/**
	 * 
	 * @param bounds
	 *            LatLngBounds within which to search.
	 * @return référence au builder.
	 */
	public GeocoderRequestBuilder setBounds(LatLngBounds bounds) {
		geocoderRequest.setBounds(bounds);
		return this;
	}

	/**
	 * 
	 * @param location
	 *            la position.
	 * @return référence au builder.
	 */
	public GeocoderRequestBuilder setLocation(LatLng location) {
		geocoderRequest.setLocation(location);
		return this;
	}

	/**
	 * 
	 * @return la requete.
	 */
	public GeocoderRequest getGeocoderRequest() {
		return geocoderRequest;
	}
}
