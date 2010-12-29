package fr.ybo.transportsrenneshelper.keolis.gtfs.modele;

import fr.ybo.transportsrenneshelper.keolis.gtfs.annotation.BaliseCsv;
import fr.ybo.transportsrenneshelper.keolis.gtfs.annotation.FichierCsv;
import fr.ybo.transportsrenneshelper.keolis.gtfs.moteur.adapter.AdapterInteger;

@FichierCsv("arret_route.txt")
public class ArretRoute {
	@BaliseCsv("stop_id")
	private String arretId;

	@BaliseCsv("route_id")
	private String routeId;

	@BaliseCsv("direction")
	private String direction;

	@BaliseCsv( value = "sequence", adapter = AdapterInteger.class)
	private Integer sequence;

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

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public Integer getSequence() {
		return sequence;
	}
}
