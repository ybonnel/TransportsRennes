package fr.ybo.transportsrenneshelper.keolis.gtfs.modele;

import fr.ybo.transportsrenneshelper.keolis.gtfs.annotation.BaliseCsv;
import fr.ybo.transportsrenneshelper.keolis.gtfs.annotation.FichierCsv;
import fr.ybo.transportsrenneshelper.keolis.gtfs.moteur.adapter.AdapterInteger;
import fr.ybo.transportsrenneshelper.keolis.gtfs.moteur.adapter.AdapterTime;

@FichierCsv("stop_times.txt")
public class HeuresArrets {
	@BaliseCsv("trip_id")
	private String tripId;
	@BaliseCsv("stop_id")
	private String stopId;
	@BaliseCsv(value = "stop_sequence", adapter = AdapterInteger.class)
	private Integer stopSequence;
	@BaliseCsv(value = "departure_time", adapter = AdapterTime.class)
	private Integer heureDepart;
	@BaliseCsv("service_id")
	private String serviceId;
	@BaliseCsv("route_id")
	private String routeId;

	private Calendrier calendrier;


	public Integer getHeureDepart() {
		return heureDepart;
	}

	public String getStopId() {
		return stopId;
	}

	public Integer getStopSequence() {
		return stopSequence;
	}

	public String getTripId() {
		return tripId;
	}

	public void setHeureDepart(final Integer heureDepart) {
		this.heureDepart = heureDepart;
	}

	public void setStopId(final String stopId) {
		this.stopId = stopId;
	}

	public void setStopSequence(final Integer stopSequence) {
		this.stopSequence = stopSequence;
	}

	public void setTripId(final String tripId) {
		this.tripId = tripId;
	}

	public Calendrier getCalendrier() {
		return calendrier;
	}

	public void setCalendrier(Calendrier calendrier) {
		this.calendrier = calendrier;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getRouteId() {
		return routeId;
	}

	public void setRouteId(String routeId) {
		this.routeId = routeId;
	}
}
