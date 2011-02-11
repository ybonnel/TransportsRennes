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

package fr.ybo.itineraires;


import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.GeocoderResult;
import com.google.code.geocoder.model.LatLng;
import org.junit.Test;

public class ItineraireServletTest {

	@Test
	public void testItineraireServlet_nominal() {
		geoCoderAdresses("91 rue de Paris, Rennes");
		geoCoderAdresses("29 rue d'antrain, Rennes");
	}

	private GeocodeResponse geoCoderAdresses(String adresse) {
		final Geocoder geocoder = new Geocoder();
		GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().setAddress(adresse).setLanguage("fr").getGeocoderRequest();
		GeocodeResponse geocoderResponse = null;
		for (int countTest = 0; countTest < 10 && geocoderResponse == null; countTest++) {
			geocoderResponse = geocoder.geocode(geocoderRequest);
		}
		System.out.println("Adresses trouvées : ");
		for (GeocoderResult result : geocoderResponse.getResults()) {
			System.out.println(result.toString());
		}
		return geocoderResponse;
	}


}
