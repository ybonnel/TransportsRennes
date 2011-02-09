package com.google.code.geocoder.model;

import java.util.List;

/**
 * @author <a href="mailto:panchmp@gmail.com">Michael Panchenko</a>
 */
public class GeocoderResult {
    protected List<String> types;
    protected String formattedAddress;
    protected List<GeocoderAddressComponent> addressComponents;
    protected GeocoderGeometry geometry;

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public List<GeocoderAddressComponent> getAddressComponents() {
        return addressComponents;
    }

    public void setAddressComponents(List<GeocoderAddressComponent> addressComponents) {
        this.addressComponents = addressComponents;
    }

    public GeocoderGeometry getGeometry() {
        return geometry;
    }

    public void setGeometry(GeocoderGeometry geometry) {
        this.geometry = geometry;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GeocoderResult that = (GeocoderResult) o;

        if (addressComponents != null ? !addressComponents.equals(that.addressComponents) : that.addressComponents != null)
            return false;
        if (formattedAddress != null ? !formattedAddress.equals(that.formattedAddress) : that.formattedAddress != null)
            return false;
        if (geometry != null ? !geometry.equals(that.geometry) : that.geometry != null) return false;
        if (types != null ? !types.equals(that.types) : that.types != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = types != null ? types.hashCode() : 0;
        result = 31 * result + (formattedAddress != null ? formattedAddress.hashCode() : 0);
        result = 31 * result + (addressComponents != null ? addressComponents.hashCode() : 0);
        result = 31 * result + (geometry != null ? geometry.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("GeocoderResult");
        sb.append("{types=").append(types);
        sb.append(", formattedAddress='").append(formattedAddress).append('\'');
        sb.append(", addressComponents=").append(addressComponents);
        sb.append(", geometry=").append(geometry);
        sb.append('}');
        return sb.toString();
    }
}