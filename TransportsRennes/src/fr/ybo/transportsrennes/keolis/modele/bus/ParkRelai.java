package fr.ybo.transportsrennes.keolis.modele.bus;

import fr.ybo.transportsrennes.keolis.modele.ObjetWithDistance;

import java.io.Serializable;

/**
 * @author ybonnel
 */
@SuppressWarnings("serial")
public class ParkRelai extends ObjetWithDistance implements Serializable {

	/**
	 * name.
	 */
	private String name;
	/**
	 * latitude.
	 */
	private double latitude;
	/**
	 * longitude.
	 */
	private double longitude;
	/**
	 * carParkAvailable.
	 */
	private Integer carParkAvailable;
	/**
	 * carParkCapacity.
	 */
	private Integer carParkCapacity;
	/**
	 * lastupdate.
	 */
	private String lastupdate;
	/**
	 * state.
	 */
	private Integer state;

	/**
	 * Distance à la position courante. Calculée par la méthode calculDistance.
	 */
	private Integer distance = null;


	/**
	 * @return the carParkAvailable
	 */
	public final Integer getCarParkAvailable() {
		return carParkAvailable;
	}

	/**
	 * @return the carParkCapacity
	 */
	public final Integer getCarParkCapacity() {
		return carParkCapacity;
	}

	/**
	 * @return the lastupdate
	 */
	public final String getLastupdate() {
		return lastupdate;
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
	 * @return the state
	 */
	public final Integer getState() {
		return state;
	}

	/**
	 * @param pCarParkAvailable the carParkAvailable to set
	 */
	public final void setCarParkAvailable(final Integer pCarParkAvailable) {
		carParkAvailable = pCarParkAvailable;
	}

	/**
	 * @param pCarParkCapacity the carParkCapacity to set
	 */
	public final void setCarParkCapacity(final Integer pCarParkCapacity) {
		carParkCapacity = pCarParkCapacity;
	}

	/**
	 * @param pLastupdate the lastupdate to set
	 */
	public final void setLastupdate(final String pLastupdate) {
		lastupdate = pLastupdate;
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
	 * @param pState the state to set
	 */
	public final void setState(final Integer pState) {
		state = pState;
	}
}
