package fr.ybo.transportsrennes.keolis.modele.velos;

import android.location.Location;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Classe représentant une station de velo star.
 *
 * @author ybonnel
 */
public class Station implements Serializable {

	public static class ComparatorDistance implements Comparator<Station> {

		public int compare(Station o1, Station o2) {
			if (o1 == null || o2 == null || o1.getDistance() == null || o2.getDistance() == null) {
				return 0;
			}
			return o1.getDistance().compareTo(o2.getDistance());
		}
	}

	/**
	 * Serial.
	 */
	private static final long serialVersionUID = 1L;

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
	 * Numéro de la station.
	 */
	private String number;
	/**
	 * Nom de la station.
	 */
	private String name;
	/**
	 * adresse de la station.
	 */
	private String adresse;

	/**
	 * Etat de la station.
	 */
	private boolean state;

	/**
	 * Latitude.
	 */
	private double latitude;

	/**
	 * Longitude.
	 */
	private double longitude;
	/**
	 * Places libres.
	 */
	private int slotsavailable;
	/**
	 * Vélos libres.
	 */
	private int bikesavailable;
	/**
	 * Position.
	 */
	private boolean pos;
	/**
	 * Nom du district.
	 */
	private String district;
	/**
	 * Date de dernière mise à jour.
	 */
	private String lastupdate;
	/**
	 * Distance à la position courante. Calculée par la méthode
	 * {@link Station#calculDistance(Location)}.
	 */
	private Integer distance = null;

	/**
	 * Calcul la distance entre une location et la station.
	 *
	 * @param pCurrentLocation la location courante.
	 */
	public void calculDistance(Location pCurrentLocation) {
		if (pCurrentLocation != null) {
			float[] distanceResult = new float[1];
			Location.distanceBetween(pCurrentLocation.getLatitude(), pCurrentLocation.getLongitude(), latitude, longitude, distanceResult);
			distance = (int) distanceResult[0];
		}
	}

	/**
	 * Format la distance.
	 *
	 * @return la distance formattée.
	 */
	public String formatDistance() {
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

	/**
	 * @return adresse.
	 */
	public String getAdresse() {
		return adresse;
	}

	/**
	 * Getter.
	 *
	 * @return le nombre de velos libres.
	 */
	public int getBikesavailable() {
		return bikesavailable;
	}

	/**
	 * Getter.
	 *
	 * @return la distance à la position courante.
	 */
	public Integer getDistance() {
		return distance;
	}

	/**
	 * Getter.
	 *
	 * @return le nom du district.
	 */
	public String getDistrict() {
		return district;
	}

	/**
	 * Getter.
	 *
	 * @return la date de dernière mise à jour.
	 */
	public String getLastupdate() {
		return lastupdate;
	}

	/**
	 * Getter.
	 *
	 * @return la latitude.
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * Getter.
	 *
	 * @return la longitude.
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * Getter.
	 *
	 * @return nom de la station.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Getter.
	 *
	 * @return numéro de la station.
	 */
	public String getNumber() {
		return number;
	}

	/**
	 * Getter.
	 *
	 * @return la position.
	 */
	public boolean isPos() {
		return pos;
	}

	/**
	 * Getter.
	 *
	 * @return le nombre de places libres.
	 */
	public int getSlotsavailable() {
		return slotsavailable;
	}

	/**
	 * Getter.
	 *
	 * @return état de la station.
	 */
	public boolean getState() {
		return state;
	}

	/**
	 * @param pAdresse l'adresse.
	 */
	public void setAdresse(String pAdresse) {
		adresse = pAdresse;
	}

	/**
	 * Setter.
	 *
	 * @param pBikesavailable nombre de vélos libres.
	 */
	public void setBikesavailable(int pBikesavailable) {
		bikesavailable = pBikesavailable;
	}

	public void setDistance(Integer newDistance) {
		distance = newDistance;
	}

	/**
	 * Setter.
	 *
	 * @param pDistrict le nom du district.
	 */
	public void setDistrict(String pDistrict) {
		district = pDistrict;
	}

	/**
	 * Setter.
	 *
	 * @param pLastupdate la date de dernière mise à jour.
	 */
	public void setLastupdate(String pLastupdate) {
		lastupdate = pLastupdate;
	}

	/**
	 * Setter.
	 *
	 * @param pLatitude la latitude.
	 */
	public void setLatitude(double pLatitude) {
		latitude = pLatitude;
	}

	/**
	 * Setter.
	 *
	 * @param pLongitude la longitude.
	 */
	public void setLongitude(double pLongitude) {
		longitude = pLongitude;
	}

	/**
	 * Setter.
	 *
	 * @param pName nom du district.
	 */
	public void setName(String pName) {
		name = pName;
	}

	/**
	 * Setter.
	 *
	 * @param pNumber le numéro de la station.
	 */
	public void setNumber(String pNumber) {
		number = pNumber;
	}

	/**
	 * Setter.
	 *
	 * @param pPos la position.
	 */
	public void setPos(boolean pPos) {
		pos = pPos;
	}

	/**
	 * Setter.
	 *
	 * @param pSlotsavailable le nombre de places libres.
	 */
	public void setSlotsavailable(int pSlotsavailable) {
		slotsavailable = pSlotsavailable;
	}

	/**
	 * Setter.
	 *
	 * @param pState état de la station.
	 */
	public void setState(boolean pState) {
		state = pState;
	}

	/**
	 * @return String.
	 * @see Object#toString().
	 */
	@Override
	public String toString() {
		return new StringBuilder(name).append(' ').append(bikesavailable).append('/').append((slotsavailable + bikesavailable)).append("  ")
				.append(formatDistance()).toString();
	}
}
