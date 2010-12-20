package fr.ybo.transportsrennes.keolis.gtfs.modele;

import fr.ybo.transportsrennes.keolis.gtfs.annotation.*;

@Table
@FichierCsv("arret_route.txt")
public class ArretRoute {
	@Colonne
	@PrimaryKey
	@BaliseCsv("stop_id")
	private String arretId;

	@Colonne
	@PrimaryKey
	@BaliseCsv("route_id")
	private String routeId;

	@Colonne
	@BaliseCsv("direction")
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
