package fr.ybo.transportsrennes.keolis.modele.bus;

import java.io.Serializable;

/**
 * Station de m�tro.
 * 
 * @author ybonnel
 * 
 */
@SuppressWarnings("serial")
public class MetroStation implements Serializable {
	/** id. */
	private String id;
	/**
	 * Nom.
	 */
	private String name;
	/**
	 * latitude.
	 */
	private double latitude;
	/**
	 * Longitude.
	 */
	private double longitude;
	/**
	 * ??.
	 */
	private boolean hasPlatformDirection1;
	/**
	 * ??.
	 */
	private boolean hasPlatformDirection2;
	/**
	 * ??.
	 */
	private Integer rankingPlatformDirection1;
	/**
	 * ??.
	 */
	private Integer rankingPlatformDirection2;
	/**
	 * Etage.
	 */
	private int floors;
	/**
	 * Derni�re mise � jour.
	 */
	private String lastupdate;

	/**
	 * @return the floors
	 */
	public final int getFloors() {
		return floors;
	}

	/**
	 * @return the id
	 */
	public final String getId() {
		return id;
	}

	/**
	 * @return the lastupdate
	 */
	public final String getLastupdate() {
		return lastupdate;
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

	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * @return the rankingPlatformDirection1
	 */
	public final Integer getRankingPlatformDirection1() {
		return rankingPlatformDirection1;
	}

	/**
	 * @return the rankingPlatformDirection2
	 */
	public final Integer getRankingPlatformDirection2() {
		return rankingPlatformDirection2;
	}

	/**
	 * @return the hasPlatformDirection1
	 */
	public final boolean isHasPlatformDirection1() {
		return hasPlatformDirection1;
	}

	/**
	 * @return the hasPlatformDirection2
	 */
	public final boolean isHasPlatformDirection2() {
		return hasPlatformDirection2;
	}

	/**
	 * @param pFloors
	 *            the floors to set
	 */
	public final void setFloors(final int pFloors) {
		floors = pFloors;
	}

	/**
	 * @param pHasPlatformDirection1
	 *            the hasPlatformDirection1 to set
	 */
	public final void setHasPlatformDirection1(final boolean pHasPlatformDirection1) {
		hasPlatformDirection1 = pHasPlatformDirection1;
	}

	/**
	 * @param pHasPlatformDirection2
	 *            the hasPlatformDirection2 to set
	 */
	public final void setHasPlatformDirection2(final boolean pHasPlatformDirection2) {
		hasPlatformDirection2 = pHasPlatformDirection2;
	}

	/**
	 * @param pId
	 *            the id to set
	 */
	public final void setId(final String pId) {
		id = pId;
	}

	/**
	 * @param pLastupdate
	 *            the lastupdate to set
	 */
	public final void setLastupdate(final String pLastupdate) {
		lastupdate = pLastupdate;
	}

	/**
	 * @param pLatitude
	 *            the latitude to set
	 */
	public final void setLatitude(final double pLatitude) {
		latitude = pLatitude;
	}

	/**
	 * @param pLongitude
	 *            the longitude to set
	 */
	public final void setLongitude(final double pLongitude) {
		longitude = pLongitude;
	}

	/**
	 * @param pName
	 *            the name to set
	 */
	public final void setName(final String pName) {
		name = pName;
	}

	/**
	 * @param pRankingPlatformDirection1
	 *            the rankingPlatformDirection1 to set
	 */
	public final void setRankingPlatformDirection1(final Integer pRankingPlatformDirection1) {
		rankingPlatformDirection1 = pRankingPlatformDirection1;
	}

	/**
	 * @param pRankingPlatformDirection2
	 *            the rankingPlatformDirection2 to set
	 */
	public final void setRankingPlatformDirection2(final Integer pRankingPlatformDirection2) {
		rankingPlatformDirection2 = pRankingPlatformDirection2;
	}

}
