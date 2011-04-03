package fr.ybo.opentripplanner.client;

import java.text.SimpleDateFormat;

public class Constantes {
	private static final String URL_HOST = "http://127.0.0.1:8080";
	private static final String URL_BASE = URL_HOST + "/opentripplanner-api-webapp/ws";
	public static final String URL_PLANER = URL_BASE + "/plan";
	public static final String URL_METADATA = URL_BASE + "/metadata";
	/**
	 * Encodage.
	 */
	public static final String ENCODAGE = "UTF-8";

	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

}
