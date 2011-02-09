package com.google.code.geocoder.model;

public class GeoAddress {
    protected GeocoderLocationType locationType;
    protected String formattedAddress;
    private AddressDetails addressDetails;
    protected LatLng location;
    protected LatLngBounds viewport;
}
