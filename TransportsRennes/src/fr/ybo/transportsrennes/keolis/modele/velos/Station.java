package fr.ybo.transportsrennes.keolis.modele.velos;

import android.location.Location;
import fr.ybo.transportsrennes.keolis.modele.ObjetWithDistance;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Classe représentant une station de velo star.
 *
 * @author ybonnel
 */
public class Station extends ObjetWithDistance implements Serializable {

	/**
	 * Serial.
	 */
	private static final long serialVersionUID = 1L;
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
	 * Distance à la position courante. Calculée par la méthode calculDistance
	 */
	private Integer distance = null;

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
}
