package fr.ybo.transportsrennes.keolis.gtfs.modele;

import fr.ybo.transportsrennes.keolis.gtfs.annotation.*;
import fr.ybo.transportsrennes.keolis.gtfs.annotation.Colonne.TypeColonne;
import fr.ybo.transportsrennes.keolis.gtfs.moteur.adapter.AdapterInteger;
import fr.ybo.transportsrennes.keolis.gtfs.moteur.adapter.AdapterTime;

@Table
@FichierCsv("stop_times.txt")
public class HeuresArrets {
	@Colonne
	@PrimaryKey
	@BaliseCsv("trip_id")
	private String tripId;
	@Colonne
	@PrimaryKey
	@BaliseCsv("stop_id")
	private String stopId;
	@Colonne(type = TypeColonne.INTEGER)
	@BaliseCsv(value = "stop_sequence", adapter = AdapterInteger.class)
	private Integer stopSequence;
	@Colonne(type = TypeColonne.INTEGER)
	@Indexed
	@BaliseCsv(value = "departure_time", adapter = AdapterTime.class)
	private Integer heureDepart;

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
}
