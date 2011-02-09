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
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.ybo.itineraires.util.StringUtils;

/**
 * @author <a href="mailto:panchmp@gmail.com">Michael Panchenko</a>
 */
public class Geocoder {

    private static final String GEOCODE_REQUEST_URL = "http://maps.googleapis.com/maps/api/geocode/json?sensor=false";
    public Geocoder() {
    }

    public GeocodeResponse geocode(final GeocoderRequest geocoderRequest) {
        try {
            final String urlString = getURL(geocoderRequest);

            final Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

			URL url = new URL(urlString);
			final Reader reader = new InputStreamReader(url.openStream(),
					"utf-8");
			return gson.fromJson(reader, GeocodeResponse.class);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
        }
    }

    protected String getURL(final GeocoderRequest geocoderRequest) throws UnsupportedEncodingException {
        final String address = geocoderRequest.getAddress();
        final LatLngBounds bounds = geocoderRequest.getBounds();
        final String language = geocoderRequest.getLanguage();
        final String region = geocoderRequest.getRegion();
        final LatLng location = geocoderRequest.getLocation();

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
            urlString += "&bounds=" + URLEncoder.encode(bounds.getSouthwest().toUrlValue() + "|" + bounds.getNortheast().toUrlValue(), "UTF-8");
        }
        return urlString;
    }
}