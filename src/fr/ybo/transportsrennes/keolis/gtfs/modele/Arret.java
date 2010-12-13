package fr.ybo.transportsrennes.keolis.gtfs.modele;

import fr.ybo.transportsrennes.keolis.gtfs.annotation.BaliseCsv;
import fr.ybo.transportsrennes.keolis.gtfs.annotation.Colonne;
import fr.ybo.transportsrennes.keolis.gtfs.annotation.Colonne.TypeColonne;
import fr.ybo.transportsrennes.keolis.gtfs.annotation.FichierCsv;
import fr.ybo.transportsrennes.keolis.gtfs.annotation.PrimaryKey;
import fr.ybo.transportsrennes.keolis.gtfs.annotation.Table;
import fr.ybo.transportsrennes.keolis.gtfs.moteur.adapter.AdapterDouble;

@Table
@FichierCsv("stops.txt")
public class Arret {
	@Colonne
	@PrimaryKey
	@BaliseCsv("stop_id")
	private String id;
	@Colonne
	@BaliseCsv("stop_code")
	private String code;
	@Colonne
	@BaliseCsv("stop_name")
	private String nom;
	@Colonne
	@BaliseCsv("stop_desc")
	private String description;
	@Colonne(type = TypeColonne.NUMERIC)
	@BaliseCsv(value = "stop_lat", adapter = AdapterDouble.class)
	private Double latitude;
	@Colonne(type = TypeColonne.NUMERIC)
	@BaliseCsv(value = "stop_lon", adapter = AdapterDouble.class)
	private Double longitude;

	private transient String destination;

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Arret other = (Arret) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

	public String getDestination() {
		return destination;
	}

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (id == null ? 0 : id.hashCode());
		return result;
	}

	public void setCode(final String code) {
		this.code = code;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public void setDestination(final String destination) {
		this.destination = destination;
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
