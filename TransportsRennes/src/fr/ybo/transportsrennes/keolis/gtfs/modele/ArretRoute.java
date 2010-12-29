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
}
