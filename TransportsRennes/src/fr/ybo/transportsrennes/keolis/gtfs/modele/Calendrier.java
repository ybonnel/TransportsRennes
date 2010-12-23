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

	public Boolean getDimanche() {
		return dimanche;
	}

	public String getId() {
		return id;
	}

	public Boolean getJeudi() {
		return jeudi;
	}

	public Boolean getLundi() {
		return lundi;
	}

	public Boolean getMardi() {
		return mardi;
	}

	public Boolean getMercredi() {
		return mercredi;
	}

	public Boolean getSamedi() {
		return samedi;
	}

	public Boolean getVendredi() {
		return vendredi;
	}

	public void setDimanche(final Boolean dimanche) {
		this.dimanche = dimanche;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public void setJeudi(final Boolean jeudi) {
		this.jeudi = jeudi;
	}

	public void setLundi(final Boolean lundi) {
		this.lundi = lundi;
	}

	public void setMardi(final Boolean mardi) {
		this.mardi = mardi;
	}

	public void setMercredi(final Boolean mercredi) {
		this.mercredi = mercredi;
	}

	public void setSamedi(final Boolean samedi) {
		this.samedi = samedi;
	}

	public void setVendredi(final Boolean vendredi) {
		this.vendredi = vendredi;
	}
}
