package fr.ybo.transportsrennes.keolis.modele.bus;

import fr.ybo.transportsrennes.keolis.modele.ObjetWithDistance;

import java.io.Serializable;

/**
 * Un point de vente.
 *
 * @author ybonnel
 */
@SuppressWarnings("serial")
public class PointDeVente extends ObjetWithDistance implements Serializable {
	/**
	 * Nom du point de vente.
	 */
	private String name;
	/**
	 * Type du point de vente.
	 */
	private String type;
	/**
	 * Adresse du point de vente.
	 */
	private String adresse;
	/**
	 * Code postal du point de vente.
	 */
	private String codePostal;
	/**
	 * Ville du point de vente.
	 */
	private String ville;
	/**
	 * District du point de vente.
	 */
	private String district;
	/**
	 * Téléphone du point de vente.
	 */
	private String telephone;
	/**
	 * Schedule du point de vente.
	 */
	private String schedule;
	/**
	 * Latitude du point de vente.
	 */
	private double latitude;
	/**
	 * Longitude du point de vente.
	 */
	private double longitude;

	private Integer distance;

	/**
	 * @return the adresse
	 */
	public final String getAdresse() {
		return adresse;
	}

	/**
	 * @return the codePostal
	 */
	public final String getCodePostal() {
		return codePostal;
	}

	/**
	 * @return the district
	 */
	public final String getDistrict() {
		return district;
	}

	@Override
	public Integer getDistance() {
		return distance;
	}

	/**
	 * @return the latitude
	 */
	public final double getLatitude() {
		return latitude;
	}

	/**
	 * @return the longitude
	 */
	public final double getLongitude() {
		return longitude;
	}

	@Override
	public void setDistance(Integer distance) {
		this.distance = distance;
	}

	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * @return the schedule
	 */
	public final String getSchedule() {
		return schedule;
	}

	/**
	 * @return the telephone
	 */
	public final String getTelephone() {
		return telephone;
	}

	/**
	 * @return the type
	 */
	public final String getType() {
		return type;
	}

	/**
	 * @return the ville
	 */
	public final String getVille() {
		return ville;
	}

	/**
	 * @param pAdresse the adresse to set
	 */
	public final void setAdresse(final String pAdresse) {
		adresse = pAdresse;
	}

	/**
	 * @param pCodePostal the codePostal to set
	 */
	public final void setCodePostal(final String pCodePostal) {
		codePostal = pCodePostal;
	}

	/**
	 * @param pDistrict the district to set
	 */
	public final void setDistrict(final String pDistrict) {
		district = pDistrict;
	}

	/**
	 * @param pLatitude the latitude to set
	 */
	public final void setLatitude(final double pLatitude) {
		latitude = pLatitude;
	}

	/**
	 * @param pLongitude the longitude to set
	 */
	public final void setLongitude(final double pLongitude) {
		longitude = pLongitude;
	}

	/**
	 * @param pName the name to set
	 */
	public final void setName(final String pName) {
		name = pName;
	}

	/**
	 * @param pSchedule the schedule to set
	 */
	public final void setSchedule(final String pSchedule) {
		schedule = pSchedule;
	}

	/**
	 * @param pTelephone the telephone to set
	 */
	public final void setTelephone(final String pTelephone) {
		telephone = pTelephone;
	}

	/**
	 * @param pType the type to set
	 */
	public final void setType(final String pType) {
		type = pType;
	}

	/**
	 * @param pVille the ville to set
	 */
	public final void setVille(final String pVille) {
		ville = pVille;
	}
}
