package fr.ybo.transportsrennes.keolis.gtfs.modele;

import fr.ybo.transportsrennes.keolis.gtfs.annotation.BaliseCsv;
import fr.ybo.transportsrennes.keolis.gtfs.annotation.Colonne;
import fr.ybo.transportsrennes.keolis.gtfs.annotation.FichierCsv;
import fr.ybo.transportsrennes.keolis.gtfs.annotation.Indexed;
import fr.ybo.transportsrennes.keolis.gtfs.annotation.PrimaryKey;
import fr.ybo.transportsrennes.keolis.gtfs.annotation.Table;

@Table
@FichierCsv("trips.txt")
public class Trip {
	@Colonne
	@PrimaryKey
	@BaliseCsv("trip_id")
	private String id;
	@Colonne
	@Indexed
	@BaliseCsv("service_id")
	private String serviceId;
	@Colonne
	@Indexed
	@BaliseCsv("route_id")
	private String routeId;
	@Colonne
	@BaliseCsv("trip_headsign")
	private String headSign;
	@Colonne
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
