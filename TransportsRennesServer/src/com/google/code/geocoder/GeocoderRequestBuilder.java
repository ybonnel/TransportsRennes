package com.google.code.geocoder;

import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.LatLng;
import com.google.code.geocoder.model.LatLngBounds;

/**
 * @author <a href="mailto:panchmp@gmail.com">Michael Panchenko</a>
 */
public class GeocoderRequestBuilder {
    final GeocoderRequest geocoderRequest = new GeocoderRequest();

    public GeocoderRequestBuilder setAddress(String address) {
        geocoderRequest.setAddress(address);
        return this;
    }

    public GeocoderRequestBuilder

    setLanguage(String language) {
        geocoderRequest.setLanguage(language);
        return this;
    }

    public GeocoderRequestBuilder setRegion(String region) {
        geocoderRequest.setRegion(region);
        return this;
    }

    public GeocoderRequestBuilder setBounds(LatLngBounds bounds) {
        geocoderRequest.setBounds(bounds);
        return this;
    }

    public GeocoderRequestBuilder setLocation(LatLng location) {
        geocoderRequest.setLocation(location);
        return this;
    }

    public GeocoderRequest getGeocoderRequest() {
        return geocoderRequest;
    }
}
