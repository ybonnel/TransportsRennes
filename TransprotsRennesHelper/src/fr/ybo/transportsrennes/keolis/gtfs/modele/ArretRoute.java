package fr.ybo.transportsrennes.keolis.gtfs.modele;

public class ArretRoute {
    private String arretId;

    private String routeId;

    private String direction;

    public String getArretId() {
        return arretId;
    }

    public String getDirection() {
        return direction;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setArretId(final String arretId) {
        this.arretId = arretId;
    }

    public void setDirection(final String direction) {
        this.direction = direction;
    }

    public void setRouteId(final String routeId) {
        this.routeId = routeId;
    }

}
