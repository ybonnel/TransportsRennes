package com.google.code.geocoder.model;

import java.io.Serializable;

public class AddressDetails implements Serializable {
    private static final long serialVersionUID = 1L;

    private String country;
    private String administrativeAreaLevel1;
    private String locality;
    private String subLocality;
    private String route;
    private String street_address;
    private String subPremise;

    public AddressDetails() {
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAdministrativeAreaLevel1() {
        return administrativeAreaLevel1;
    }

    public void setAdministrativeAreaLevel1(String administrativeAreaLevel1) {
        this.administrativeAreaLevel1 = administrativeAreaLevel1;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getSubLocality() {
        return subLocality;
    }

    public void setSubLocality(String subLocality) {
        this.subLocality = subLocality;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getStreet_address() {
        return street_address;
    }

    public void setStreet_address(String street_address) {
        this.street_address = street_address;
    }

    public String getSubPremise() {
        return subPremise;
    }

    public void setSubPremise(String subPremise) {
        this.subPremise = subPremise;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AddressDetails that = (AddressDetails) o;

        if (administrativeAreaLevel1 != null ? !administrativeAreaLevel1.equals(that.administrativeAreaLevel1) : that.administrativeAreaLevel1 != null)
            return false;
        if (country != null ? !country.equals(that.country) : that.country != null) return false;
        if (locality != null ? !locality.equals(that.locality) : that.locality != null) return false;
        if (route != null ? !route.equals(that.route) : that.route != null) return false;
        if (street_address != null ? !street_address.equals(that.street_address) : that.street_address != null)
            return false;
        if (subLocality != null ? !subLocality.equals(that.subLocality) : that.subLocality != null) return false;
        if (subPremise != null ? !subPremise.equals(that.subPremise) : that.subPremise != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = country != null ? country.hashCode() : 0;
        result = 31 * result + (administrativeAreaLevel1 != null ? administrativeAreaLevel1.hashCode() : 0);
        result = 31 * result + (locality != null ? locality.hashCode() : 0);
        result = 31 * result + (subLocality != null ? subLocality.hashCode() : 0);
        result = 31 * result + (route != null ? route.hashCode() : 0);
        result = 31 * result + (street_address != null ? street_address.hashCode() : 0);
        result = 31 * result + (subPremise != null ? subPremise.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("AddressDetails");
        sb.append("{country='").append(country).append('\'');
        sb.append(", administrativeAreaLevel1='").append(administrativeAreaLevel1).append('\'');
        sb.append(", locality='").append(locality).append('\'');
        sb.append(", subLocality='").append(subLocality).append('\'');
        sb.append(", route='").append(route).append('\'');
        sb.append(", street_address='").append(street_address).append('\'');
        sb.append(", subPremise='").append(subPremise).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
