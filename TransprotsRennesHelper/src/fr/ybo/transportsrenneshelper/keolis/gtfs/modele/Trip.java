package fr.ybo.transportsrenneshelper.keolis.gtfs.modele;

import fr.ybo.transportsrenneshelper.keolis.gtfs.annotation.BaliseCsv;
import fr.ybo.transportsrenneshelper.keolis.gtfs.annotation.FichierCsv;

@FichierCsv("trips.txt")
public class Trip {
	@BaliseCsv("trip_id")
	private String id;
	@BaliseCsv("service_id")
	private String serviceId;
	@BaliseCsv("route_id")
	private String routeId;
	@BaliseCsv("trip_headsign")
	private String headSign;
	@BaliseCsv("direction_id")
	private String directionId;

	public String getDirectionId() {
		return directionId;
	}

	public String getHeadSign() {
		return headSign;
	}

	public String getId() {
		return id;
	}

	public String getRouteId() {
		return routeId;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setDirectionId(final String directionId) {
		this.directionId = directionId;
	}

	public void setHeadSign(final String headSign) {
		this.headSign = headSign;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public void setRouteId(final String routeId) {
		this.routeId = routeId;
	}

	public void setServiceId(final String serviceId) {
		this.serviceId = serviceId;
	}
}
