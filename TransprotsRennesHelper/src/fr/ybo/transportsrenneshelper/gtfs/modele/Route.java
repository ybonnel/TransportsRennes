package fr.ybo.transportsrenneshelper.gtfs.modele;

import fr.ybo.transportsrenneshelper.annotation.BaliseCsv;
import fr.ybo.transportsrenneshelper.annotation.FichierCsv;

@FichierCsv("routes.txt")
public class Route {

	@BaliseCsv("route_id")
	public String id;
	@BaliseCsv("route_short_name")
	public String nomCourt;
	@BaliseCsv("route_long_name")
	public String nomLong;

	public String nomCourtFormatte;

	@Override
	public String toString() {
		return "Route{" + "nomLong='" + nomLong + '\'' + ", id='" + id + '\'' + ", nomCourt='" + nomCourt + '\'' + '}';
	}
}
