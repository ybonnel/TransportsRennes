package fr.ybo.transportsrennes.keolis.gtfs.modele;

import fr.ybo.transportsrennes.keolis.gtfs.annotation.*;
import fr.ybo.transportsrennes.keolis.gtfs.annotation.Colonne.TypeColonne;
import fr.ybo.transportsrennes.keolis.gtfs.moteur.adapter.AdapterBoolean;

@Table
@FichierCsv("calendar.txt")
public class Calendrier {

	@Colonne
	@PrimaryKey
	@BaliseCsv("service_id")
	private String id;
	@Colonne(type = TypeColonne.BOOLEAN)
	@BaliseCsv(value = "monday", adapter = AdapterBoolean.class)
	private Boolean lundi;
	@Colonne(type = TypeColonne.BOOLEAN)
	@BaliseCsv(value = "tuesday", adapter = AdapterBoolean.class)
	private Boolean mardi;
	@Colonne(type = TypeColonne.BOOLEAN)
	@BaliseCsv(value = "wednesday", adapter = AdapterBoolean.class)
	private Boolean mercredi;
	@Colonne(type = TypeColonne.BOOLEAN)
	@BaliseCsv(value = "thursday", adapter = AdapterBoolean.class)
	private Boolean jeudi;
	@Colonne(type = TypeColonne.BOOLEAN)
	@BaliseCsv(value = "friday", adapter = AdapterBoolean.class)
	private Boolean vendredi;
	@Colonne(type = TypeColonne.BOOLEAN)
	@BaliseCsv(value = "saturday", adapter = AdapterBoolean.class)
	private Boolean samedi;
	@Colonne(type = TypeColonne.BOOLEAN)
	@BaliseCsv(value = "sunday", adapter = AdapterBoolean.class)
	private Boolean dimanche;

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}
}
