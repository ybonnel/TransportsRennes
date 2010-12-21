package fr.ybo.transportsrennes.keolis.gtfs.modele;

import fr.ybo.transportsrennes.keolis.gtfs.annotation.*;

@Table
@FichierCsv("agency.txt")
public class Agence {

	@Colonne
	@PrimaryKey
	@BaliseCsv("agency_id")
	private String id;

	@Colonne
	@BaliseCsv("agency_name")
	private String nom;

	@Colonne
	@BaliseCsv("agency_url")
	private String url;

	@Colonne
	@BaliseCsv("agency_timezone")
	private String timeZone;

	@Colonne
	@BaliseCsv("agency_phone")
	private String telephone;

	@Colonne
	@BaliseCsv("agency_lang")
	private String langue;

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}
}
