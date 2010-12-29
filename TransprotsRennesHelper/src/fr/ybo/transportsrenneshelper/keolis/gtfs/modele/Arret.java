package fr.ybo.transportsrenneshelper.keolis.gtfs.modele;

import fr.ybo.transportsrenneshelper.keolis.gtfs.annotation.BaliseCsv;
import fr.ybo.transportsrenneshelper.keolis.gtfs.annotation.FichierCsv;
import fr.ybo.transportsrenneshelper.keolis.gtfs.moteur.adapter.AdapterDouble;

@FichierCsv("stops.txt")
public class Arret {

	@BaliseCsv("stop_id")
	private String id;
	@BaliseCsv("stop_code")
	private String code;
	@BaliseCsv("stop_name")
	private String nom;
	@BaliseCsv("stop_desc")
	private String description;
	@BaliseCsv(value = "stop_lat", adapter = AdapterDouble.class)
	private Double latitude;
	@BaliseCsv(value = "stop_lon", adapter = AdapterDouble.class)
	private Double longitude;

	public String getId() {
		return id;
	}

	public Double getLatitude() {
		return latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public String getNom() {
		return nom;
	}
	public void setDescription(final String description) {
		this.description = description;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public void setLatitude(final Double latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(final Double longitude) {
		this.longitude = longitude;
	}

	public void setNom(final String nom) {
		this.nom = nom;
	}
}
