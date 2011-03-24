package com.google.code.geocoder.util;

/**
 * Classe de constantes.
 * @author ybonnel
 *
 */
public final class Constantes {
	/**
	 * Constructeur privé pour empecher l'instanciation.
	 */
	private Constantes() {
	}
	
	/**
	 * Precision par défaut pour les latitudes/longitudes.
	 */
	public static final int DEFAULT_PRECISION = 6;

	/**
	 * Réquète json pour les appels aux services google.
	 */
	public static final String GEOCODE_REQUEST_URL = "http://maps.googleapis.com/maps/api/geocode/json?sensor=false";
	/**
	 * Encodage.
	 */
	public static final String ENCODAGE = "UTF-8";

}
