package fr.ybo.transportsrennes.keolis.gtfs.modele;

import fr.ybo.transportsrennes.keolis.gtfs.annotation.*;
import fr.ybo.transportsrennes.keolis.gtfs.annotation.Colonne.TypeColonne;
import fr.ybo.transportsrennes.keolis.gtfs.moteur.adapter.AdapterDouble;
import fr.ybo.transportsrennes.keolis.modele.ObjetWithDistance;

@Table
@FichierCsv("stops.txt")
public class Arret extends ObjetWithDistance {

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

	private transient ArretFavori favori;

	public ArretFavori getFavori() {
		return favori;
	}

	public void setFavori(ArretFavori favori) {
		this.favori = favori;
	}

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

	public String getId() {
		return id;
	}

	@Override
	public Integer getDistance() {
		return distance;
	}

	public double getLatitude() {
		return latitude.doubleValue();
	}

	public double getLongitude() {
		return longitude.doubleValue();
	}

	@Override
	public void setDistance(Integer distance) {
		this.distance = distance;
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


	/**
	 * Distance à la position courante. Calculée par la méthode calculDistance.
	 */
	private Integer distance = null;


}
