package fr.ybo.transportsrenneshelper.gtfs.modele;

import fr.ybo.transportsrenneshelper.annotation.BaliseCsv;
import fr.ybo.transportsrenneshelper.annotation.FichierCsv;
import fr.ybo.transportsrenneshelper.gtfs.gestionnaire.GestionnaireGtfs;

@FichierCsv("trips.txt")
public class Trip {
	@BaliseCsv("trip_id")
	public String id;
	@BaliseCsv("service_id")
	public String serviceId;
	@BaliseCsv("route_id")
	public String routeId;
	@BaliseCsv("trip_headsign")
	public String headSign;

	public Calendar getCalendar() {
		return GestionnaireGtfs.getInstance().getMapCalendars().get(serviceId);
	}

	public Route getRoute() {
		return GestionnaireGtfs.getInstance().getMapRoutes().get(routeId);
	}

	@Override
	public String toString() {
		return "Trip{" + "id='" + id + '\'' + ", serviceId='" + serviceId + '\'' + ", routeId='" + routeId + '\'' + ", headSign='" + headSign + '\'' +
				'}';
	}
}
