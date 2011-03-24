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

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.LatLng;
import com.google.code.geocoder.model.LatLngBounds;
import com.google.code.geocoder.util.Constantes;
import com.google.code.geocoder.util.StringUtils;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Géocodeur d'adresse.
 * @author ybonnel
 *
 */
public final class Geocoder {

	/**
	 * Constructeur privé pour empécher l'instanciation.
	 */
	private Geocoder() {
	}

	/**
	 * Effecture le géo-codage d'une adresse.
	 * @param geocoderRequest la requète.
	 * @return la réponse.
	 */
	public static GeocodeResponse geocode(GeocoderRequest geocoderRequest) {
		GeocodeResponse reponse = null;
		try {
			String urlString = getURL(geocoderRequest);

			Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

			URL url = new URL(urlString);
			Reader reader = new InputStreamReader(url.openStream(), Constantes.ENCODAGE);
			try {
				reponse = gson.fromJson(reader, GeocodeResponse.class);
			} finally {
				reader.close();
			}
		} catch (Exception e) {
			// FIXME meilleure gestion des erreurs!
			e.printStackTrace();
		}
		return reponse;
	}

	/**
	 * Construction de l'url d'appel.
	 * @param geocoderRequest requète.
	 * @return l'url.
	 * @throws UnsupportedEncodingException ne peux pas arriver!
	 */
	private static String getURL(GeocoderRequest geocoderRequest) throws UnsupportedEncodingException {
		String address = geocoderRequest.getAddress();
		LatLngBounds bounds = geocoderRequest.getBounds();
		String language = geocoderRequest.getLanguage();
		String region = geocoderRequest.getRegion();
		LatLng location = geocoderRequest.getLocation();

		String urlString = Constantes.GEOCODE_REQUEST_URL;
		if (StringUtils.isNotBlank(address)) {
			urlString += "&address=" + URLEncoder.encode(address, Constantes.ENCODAGE);
		} else if (location != null) {
			urlString += "&latlng=" + URLEncoder.encode(location.toUrlValue(), Constantes.ENCODAGE);
		} else {
			throw new IllegalArgumentException("Address or location not defined");
		}
		if (StringUtils.isNotBlank(language)) {
			urlString += "&language=" + URLEncoder.encode(language, Constantes.ENCODAGE);
		}
		if (StringUtils.isNotBlank(region)) {
			urlString += "&region=" + URLEncoder.encode(region, Constantes.ENCODAGE);
		}
		if (bounds != null) {
			urlString += "&bounds="
					+ URLEncoder.encode(bounds.getSouthwest().toUrlValue() + '|' + bounds.getNortheast().toUrlValue(),
							Constantes.ENCODAGE);
		}
		return urlString;
	}
}