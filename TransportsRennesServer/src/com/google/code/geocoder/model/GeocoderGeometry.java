package com.google.code.geocoder.model;


/**
 * @author <a href="mailto:panchmp@gmail.com">Michael Panchenko</a>
 */
public class GeocoderGeometry {
    protected LatLng location;
    protected GeocoderLocationType locationType;
    protected LatLngBounds viewport;

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public GeocoderLocationType getLocationType() {
        return locationType;
    }

    public void setLocationType(GeocoderLocationType locationType) {
        this.locationType = locationType;
    }

    public LatLngBounds getViewport() {
        return viewport;
    }

    public void setViewport(LatLngBounds viewport) {
        this.viewport = viewport;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GeocoderGeometry that = (GeocoderGeometry) o;

        if (location != null ? !location.equals(that.location) : that.location != null) return false;
        if (locationType != that.locationType) return false;
        if (viewport != null ? !viewport.equals(that.viewport) : that.viewport != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = location != null ? location.hashCode() : 0;
        result = 31 * result + (locationType != null ? locationType.hashCode() : 0);
        result = 31 * result + (viewport != null ? viewport.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("GeocoderGeometry");
        sb.append("{location=").append(location);
        sb.append(", locationType=").append(locationType);
        sb.append(", viewport=").append(viewport);
        sb.append('}');
        return sb.toString();
    }
}