package fr.ybo.transportsrennes.keolis.gtfs.modele;

import fr.ybo.transportsrennes.keolis.gtfs.annotation.*;
import fr.ybo.transportsrennes.keolis.gtfs.moteur.adapter.AdapterInteger;

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

	@BaliseCsv( value = "sequence", adapter = AdapterInteger.class)
	@Colonne( type = Colonne.TypeColonne.INTEGER )
	private Integer sequence;

	public void setRouteId(String routeId) {
		this.routeId = routeId;
	}

	public void setArretId(String arretId) {
		this.arretId = arretId;
	}

	public String getDirection() {
		return direction;
	}
}
