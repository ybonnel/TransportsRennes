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
	public String name;
	/**
	 * latitude.
	 */
	public double latitude;
	/**
	 * longitude.
	 */
	public double longitude;
	/**
	 * carParkAvailable.
	 */
	public Integer carParkAvailable;
	/**
	 * carParkCapacity.
	 */
	public Integer carParkCapacity;
	/**
	 * lastupdate.
	 */
	public String lastupdate;
	/**
	 * state.
	 */
	public Integer state;

	/**
	 * @return the latitude
	 */
	@Override
	public final double getLatitude() {
		return latitude;
	}

	/**
	 * @return the longitude
	 */
	@Override
	public final double getLongitude() {
		return longitude;
	}
}
