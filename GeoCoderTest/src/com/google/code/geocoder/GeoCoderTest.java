package com.google.code.geocoder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderStatus;
import com.google.code.geocoder.model.LatLng;
import com.google.code.geocoder.model.LatLngBounds;

/**
 * Test de la classe {@link Geocoder}
 * @author ybonnel
 *
 */
public class GeoCoderTest {
	
	/**
	 * Test avec une adresse bidon.
	 */
	@Test
	public void testGeocode_adresseBidon() {
		GeocoderRequestBuilder builder = new GeocoderRequestBuilder();
		builder.setAddress("azerty, uiop");
		builder.setBounds(new LatLngBounds(new LatLng("-10", "-10"), new LatLng("50", "50")));
		builder.setLanguage("fr");
		builder.setRegion("bretagne");
		System.out.println(builder.getGeocoderRequest().toString());
		GeocodeResponse response = Geocoder.geocode(builder.getGeocoderRequest());
		System.out.println(response.toString());
		assertNotNull(response);
		assertEquals( GeocoderStatus.ZERO_RESULTS, response.getStatus());
	}
	
	/**
	 * Test avec une adresse précise.
	 */
	@Test
	public void testGeocode_adresse1Resultat() {
		GeocoderRequestBuilder builder = new GeocoderRequestBuilder();
		builder.setAddress("91 rue de paris, Rennes");
		GeocodeResponse response = Geocoder.geocode(builder.getGeocoderRequest());
		System.out.println(response.toString());
		assertNotNull(response);
		assertEquals( GeocoderStatus.OK, response.getStatus());
		assertEquals( 1, response.getResults().size());
		assertEquals( "91 Rue de Paris, 35000 Rennes, France", response.getResults().get(0).getFormattedAddress());
	}
	
	/**
	 * Test avec une adresse imprécise.
	 */
	@Test
	public void testGeocode_adresseMultipleResultat() {
		GeocoderRequestBuilder builder = new GeocoderRequestBuilder();
		builder.setAddress("91 rue de paris, 35");
		GeocodeResponse response = Geocoder.geocode(builder.getGeocoderRequest());
		System.out.println(response.toString());
		assertNotNull(response);
		assertEquals( GeocoderStatus.OK, response.getStatus());
		assertEquals( 9, response.getResults().size());
	}

}
