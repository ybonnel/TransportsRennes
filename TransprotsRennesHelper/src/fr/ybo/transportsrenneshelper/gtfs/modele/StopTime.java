package fr.ybo.transportsrenneshelper.gtfs.modele;

import fr.ybo.transportsrenneshelper.annotation.BaliseCsv;
import fr.ybo.transportsrenneshelper.annotation.FichierCsv;
import fr.ybo.transportsrenneshelper.gtfs.gestionnaire.GestionnaireGtfs;
import fr.ybo.transportsrenneshelper.moteurcsv.adapter.AdapterInteger;
import fr.ybo.transportsrenneshelper.moteurcsv.adapter.AdapterTime;

@FichierCsv("stop_times.txt")
public class StopTime {
	@BaliseCsv("trip_id")
	public String tripId;
	@BaliseCsv("stop_id")
	public String stopId;
	@BaliseCsv(value = "departure_time", adapter = AdapterTime.class)
	public int heureDepart;
	@BaliseCsv(value = "stop_sequence", adapter = AdapterInteger.class)
	public int stopSequence;

	public String getKey() {
		return tripId + stopId;
	}

	public Trip getTrip() {
		return GestionnaireGtfs.getInstance().getMapTrips().get(tripId);
	}

	public Stop getStop() {
		return GestionnaireGtfs.getInstance().getMapStops().get(stopId);
	}

	@Override
	public String toString() {
		return "StopTime{" + "tripId='" + tripId + '\'' + ", stopId='" + stopId + '\'' + ", heureDepart=" + heureDepart + ", stopSequence=" +
				stopSequence + '}';
	}
}
