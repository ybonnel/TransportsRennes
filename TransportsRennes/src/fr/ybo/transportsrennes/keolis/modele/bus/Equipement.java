package fr.ybo.transportsrennes.keolis.modele.bus;

import java.io.Serializable;

/**
 * Equipement (escalator ou ascenseur).
 *
 * @author ybonnel
 */
@SuppressWarnings("serial")
public class Equipement implements Serializable {
	/**
	 * Id.
	 */
	private String id;
	/**
	 * Station.
	 */
	private String station;
	/**
	 * Type.
	 */
	private String type;
	/**
	 * Etage de d�part.
	 */
	private int fromfloor;
	/**
	 * Etage d'arriv�e.
	 */
	private int tofloor;
	/**
	 * ??.
	 */
	private int platform;
	/**
	 * Derni�re mise � jour.
	 */
	private String lastupdate;

	/**
	 * @return the fromfloor
	 */
	public final int getFromfloor() {
		return fromfloor;
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
	 * @return the platform
	 */
	public final int getPlatform() {
		return platform;
	}

	/**
	 * @return the station
	 */
	public final String getStation() {
		return station;
	}

	/**
	 * @return the tofloor
	 */
	public final int getTofloor() {
		return tofloor;
	}

	/**
	 * @return the type
	 */
	public final String getType() {
		return type;
	}

	/**
	 * @param pFromfloor the fromfloor to set
	 */
	public final void setFromfloor(final int pFromfloor) {
		fromfloor = pFromfloor;
	}

	/**
	 * @param pId the id to set
	 */
	public final void setId(final String pId) {
		id = pId;
	}

	/**
	 * @param pLastupdate the lastupdate to set
	 */
	public final void setLastupdate(final String pLastupdate) {
		lastupdate = pLastupdate;
	}

	/**
	 * @param pPlatform the platform to set
	 */
	public final void setPlatform(final int pPlatform) {
		platform = pPlatform;
	}

	/**
	 * @param pStation the station to set
	 */
	public final void setStation(final String pStation) {
		station = pStation;
	}

	/**
	 * @param pTofloor the tofloor to set
	 */
	public final void setTofloor(final int pTofloor) {
		tofloor = pTofloor;
	}

	/**
	 * @param pType the type to set
	 */
	public final void setType(final String pType) {
		type = pType;
	}

}
