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
	@BaliseCsv("stop_id")
	private String stopId;
	@Colonne(type = TypeColonne.INTEGER)
	@BaliseCsv(value = "stop_sequence", adapter = AdapterInteger.class)
	private Integer stopSequence;
	@Colonne(type = TypeColonne.INTEGER)
	@PrimaryKey
	@BaliseCsv(value = "departure_time", adapter = AdapterTime.class)
	private Integer heureDepart;
	@Colonne
	@Indexed
	@BaliseCsv("service_id")
	private String serviceId;
	@Colonne
	@PrimaryKey
	@BaliseCsv("route_id")
	private String routeId;
}
