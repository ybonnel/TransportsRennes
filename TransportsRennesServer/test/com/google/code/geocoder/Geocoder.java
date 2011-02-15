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

import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.LatLng;
import com.google.code.geocoder.model.LatLngBounds;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.ybo.itineraires.util.StringUtils;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * @author <a href="mailto:panchmp@gmail.com">Michael Panchenko</a>
 */
public class Geocoder {

	private static final String GEOCODE_REQUEST_URL = "http://maps.googleapis.com/maps/api/geocode/json?sensor=false";

	public GeocodeResponse geocode(GeocoderRequest geocoderRequest) {
		try {
			String urlString = getURL(geocoderRequest);

			Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

			URL url = new URL(urlString);
			Reader reader = new InputStreamReader(url.openStream(), "utf-8");
			try {
				return gson.fromJson(reader, GeocodeResponse.class);
			} finally {
				reader.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private String getURL(GeocoderRequest geocoderRequest) throws UnsupportedEncodingException {
		String address = geocoderRequest.getAddress();
		LatLngBounds bounds = geocoderRequest.getBounds();
		String language = geocoderRequest.getLanguage();
		String region = geocoderRequest.getRegion();
		LatLng location = geocoderRequest.getLocation();

		String urlString = GEOCODE_REQUEST_URL;
		if (StringUtils.isNotBlank(address)) {
			urlString += "&address=" + URLEncoder.encode(address, "UTF-8");
		} else if (location != null) {
			urlString += "&latlng=" + URLEncoder.encode(location.toUrlValue(), "UTF-8");
		} else {
			throw new IllegalArgumentException("Address or location not defined");
		}
		if (StringUtils.isNotBlank(language)) {
			urlString += "&language=" + URLEncoder.encode(language, "UTF-8");
		}
		if (StringUtils.isNotBlank(region)) {
			urlString += "&region=" + URLEncoder.encode(region, "UTF-8");
		}
		if (bounds != null) {
			urlString += "&bounds=" + URLEncoder.encode(bounds.getSouthwest().toUrlValue() + '|' + bounds.getNortheast().toUrlValue(), "UTF-8");
		}
		return urlString;
	}
}