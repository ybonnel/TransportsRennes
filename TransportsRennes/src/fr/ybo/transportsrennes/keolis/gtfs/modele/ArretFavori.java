package fr.ybo.transportsrennes.keolis.gtfs.modele;

import fr.ybo.transportsrennes.keolis.gtfs.annotation.Colonne;
import fr.ybo.transportsrennes.keolis.gtfs.annotation.PrimaryKey;
import fr.ybo.transportsrennes.keolis.gtfs.annotation.Table;

import java.io.Serializable;

@SuppressWarnings("serial")
@Table
public class ArretFavori implements Serializable {

	@Colonne
	@PrimaryKey
	private String stopId;

	@Colonne
	private String nomArret;

	@Colonne
	private String direction;

	@Colonne
	private String routeId;

	@Colonne
	private String routeNomCourt;

	@Colonne
	private String routeNomLong;

	public String getDirection() {
		return direction;
	}

	public String getNomArret() {
		return nomArret;
	}

	public String getRouteId() {
		return routeId;
	}

	public String getRouteNomCourt() {
		return routeNomCourt;
	}

	public String getRouteNomLong() {
		return routeNomLong;
	}

	public String getStopId() {
		return stopId;
	}

	public void setDirection(final String direction) {
		this.direction = direction;
	}

	public void setNomArret(final String nomArret) {
		this.nomArret = nomArret;
	}

	public void setRouteId(final String routeId) {
		this.routeId = routeId;
	}

	public void setRouteNomCourt(final String routeNomCourt) {
		this.routeNomCourt = routeNomCourt;
	}

	public void setRouteNomLong(final String routeNomLong) {
		this.routeNomLong = routeNomLong;
	}

	public void setStopId(final String stopId) {
		this.stopId = stopId;
	}

}
