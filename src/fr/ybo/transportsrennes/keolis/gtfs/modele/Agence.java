package fr.ybo.transportsrennes.keolis.gtfs.modele;

import fr.ybo.transportsrennes.keolis.gtfs.annotation.BaliseCsv;
import fr.ybo.transportsrennes.keolis.gtfs.annotation.Colonne;
import fr.ybo.transportsrennes.keolis.gtfs.annotation.FichierCsv;
import fr.ybo.transportsrennes.keolis.gtfs.annotation.PrimaryKey;
import fr.ybo.transportsrennes.keolis.gtfs.annotation.Table;

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

	public String getLangue() {
		return langue;
	}

	public String getNom() {
		return nom;
	}

	public String getTelephone() {
		return telephone;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public String getUrl() {
		return url;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public void setLangue(final String langue) {
		this.langue = langue;
	}

	public void setNom(final String nom) {
		this.nom = nom;
	}

	public void setTelephone(final String telephone) {
		this.telephone = telephone;
	}

	public void setTimeZone(final String timeZone) {
		this.timeZone = timeZone;
	}

	public void setUrl(final String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "Agence [id=" + id + ", nom=" + nom + ", url=" + url + ", timeZone=" + timeZone + ", telephone=" + telephone
				+ ", langue=" + langue + "]";
	}
}
