package fr.ybo.transportsrennes.keolis.modele;


import android.location.Location;

import java.util.Comparator;

public abstract class ObjetWithDistance {

	public Integer distance = null;

	public static class ComparatorDistance implements Comparator<ObjetWithDistance> {

		public int compare(ObjetWithDistance o1, ObjetWithDistance o2) {
			if (o1 == null || o2 == null || o1.distance == null || o2.distance == null) {
				return 0;
			}
			return o1.distance.compareTo(o2.distance);
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

	public abstract double getLatitude();

	public abstract double getLongitude();
	/**
	 * Calcul la distance entre une location et la station.
	 *
	 * @param pCurrentLocation la location courante.
	 */
	public void calculDistance(Location pCurrentLocation) {
		if (pCurrentLocation != null) {
			float[] distanceResult = new float[1];
			Location.distanceBetween(pCurrentLocation.getLatitude(), pCurrentLocation.getLongitude(), getLatitude(), getLongitude(), distanceResult);
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

}
