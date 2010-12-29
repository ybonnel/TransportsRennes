package fr.ybo.transportsrennes.keolis.gtfs.modele;

import android.location.Location;
import fr.ybo.transportsrennes.keolis.gtfs.annotation.*;
import fr.ybo.transportsrennes.keolis.gtfs.annotation.Colonne.TypeColonne;
import fr.ybo.transportsrennes.keolis.gtfs.moteur.adapter.AdapterDouble;

import java.util.Comparator;

@Table
@FichierCsv("stops.txt")
public class Arret {

	public static class ComparatorDistance implements Comparator<Arret> {

		public int compare(Arret o1, Arret o2) {
			if (o1 == null || o2 == null || o1.distance == null || o2.distance == null) {
				return 0;
			}
			return o1.distance.compareTo(o2.distance);
		}
	}

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
	 * Nombre de mètres dans un kiloMètre.
	 */
	private static final double NB_METRES_BY_KM = 1000;
	/**
	 * Multiplicateur de décimales pour l'affichage d'un km (10 pour une
	 * décimale).
	 */
	private static final double MULTI_DECIMALES_FOR_KM = 10;


	/**
	 * Distance à la position courante. Calculée par la méthode
	 * {@link Arret#calculDistance(android.location.Location)}.
	 */
	private Integer distance = null;

	/**
	 * Calcul la distance entre une location et l'arrêt.
	 *
	 * @param pCurrentLocation la location courante.
	 */
	public final void calculDistance(final Location pCurrentLocation) {
		if (pCurrentLocation != null) {
			final float[] distanceResult = new float[1];
			Location.distanceBetween(pCurrentLocation.getLatitude(), pCurrentLocation.getLongitude(), latitude, longitude, distanceResult);
			distance = (int) distanceResult[0];
		}
	}

	/**
	 * Format la distance.
	 *
	 * @return la distance formattée.
	 */
	public final String formatDistance() {
		if (distance == null) {
			return "";
		}
		if (distance < NB_METRES_BY_KM) {
			return distance + "m";
		} else {
			double distanceKm = Math.round((double) distance / (NB_METRES_BY_KM / MULTI_DECIMALES_FOR_KM)) / MULTI_DECIMALES_FOR_KM;
			return distanceKm + "km";
		}
	}


}
