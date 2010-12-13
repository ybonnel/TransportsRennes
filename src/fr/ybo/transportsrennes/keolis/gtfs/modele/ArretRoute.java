package fr.ybo.transportsrennes.keolis.gtfs.modele;

import fr.ybo.transportsrennes.keolis.gtfs.annotation.Colonne;
import fr.ybo.transportsrennes.keolis.gtfs.annotation.PrimaryKey;
import fr.ybo.transportsrennes.keolis.gtfs.annotation.Table;

@Table
public class ArretRoute {
	@Colonne
	@PrimaryKey
	private String arretId;

	@Colonne
	@PrimaryKey
	private String routeId;

	@Colonne
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
