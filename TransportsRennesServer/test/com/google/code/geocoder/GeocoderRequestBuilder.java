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
 * @author <a href="mailto:panchmp@gmail.com">Michael Panchenko</a>
 */
public class GeocoderRequestBuilder {
	private final GeocoderRequest geocoderRequest = new GeocoderRequest();

	public GeocoderRequestBuilder setAddress(final String address) {
		geocoderRequest.setAddress(address);
		return this;
	}

	public GeocoderRequestBuilder setLanguage(final String language) {
		geocoderRequest.setLanguage(language);
		return this;
	}

	public GeocoderRequestBuilder setRegion(final String region) {
		geocoderRequest.setRegion(region);
		return this;
	}

	public GeocoderRequestBuilder setBounds(final LatLngBounds bounds) {
		geocoderRequest.setBounds(bounds);
		return this;
	}

	public GeocoderRequestBuilder setLocation(final LatLng location) {
		geocoderRequest.setLocation(location);
		return this;
	}

	public GeocoderRequest getGeocoderRequest() {
		return geocoderRequest;
	}
}
