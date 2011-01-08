package fr.ybo.transportsrennes.keolis.modele.bus;

import android.location.Location;

import java.io.Serializable;
import java.util.Comparator;

/**
 * @author ybonnel
 */
@SuppressWarnings("serial")
public class ParkRelai implements Serializable {

	public static class ComparatorDistance implements Comparator<ParkRelai> {

		public int compare(ParkRelai o1, ParkRelai o2) {
			if (o1 == null || o2 == null || o1.getDistance() == null || o2.getDistance() == null) {
				return 0;
			}
			return o1.getDistance().compareTo(o2.getDistance());
		}
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
	private boolean state;

	/**
	 * Distance à la position courante. Calculée par la méthode
	 * {@link #calculDistance(android.location.Location)}.
	 */
	private Integer distance = null;

	/**
	 * Getter.
	 *
	 * @return la distance à la position courante.
	 */
	public Integer getDistance() {
		return distance;
	}

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
	 * @return the state
	 */
	public final boolean isState() {
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
	public final void setState(final boolean pState) {
		state = pState;
	}
}
