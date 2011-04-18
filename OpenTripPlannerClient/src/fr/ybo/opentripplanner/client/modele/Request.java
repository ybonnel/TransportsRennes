package fr.ybo.opentripplanner.client.modele;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Request {

	private static String FROM = "fromPlace";
	private static String TO = "toPlace";
	private static String DATE = "date";
	private static String TIME = "time";

	private static String MAX_WALK_DISTANCE = "maxWalkDistance";
	private static String OPTIMIZE = "optimize";
	private static String MODE = "mode";
	private static String NUMBER_ITINERARIES = "numItineraries";
	private static String SHOW_INTERMEDIATE_STOPS = "showIntermediateStops";

	private static String ARRIVE_BY = "arriveBy";
	private static String WALK_SPEED = "walkSpeed";
	private static String WHEELCHAIR = "wheelchair";
	private static String MIN_TRANSFER_TIME = "minTransferTime";
	private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM-dd-yyyy");
	private static SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");

	private Double fromPlaceLatitude;
	private Double fromPlaceLongitude;
	private Double toPlaceLatitude;
	private Double toPlaceLongitude;
	private Date date;
	private Boolean arriveBy;
	private Boolean wheelchair;
	private Double maxWalkDistance;
	private Double walkSpeed;
	private OptimizeType optimize;
	private TraverseModeSet modes;
	private Integer minTransferTime;
	private Integer numItineraries;
	private Boolean showIntermediateStops;

	public Request(Double fromPlaceLatitude, Double fromPlaceLongitude, Double toPlaceLatitude,
			Double toPlaceLongitude, Date date) {
		super();
		this.fromPlaceLatitude = fromPlaceLatitude;
		this.fromPlaceLongitude = fromPlaceLongitude;
		this.toPlaceLatitude = toPlaceLatitude;
		this.toPlaceLongitude = toPlaceLongitude;
		this.date = date;
	}

	/**
	 * @param arriveBy
	 *            the arriveBy to set
	 */
	public void setArriveBy(Boolean arriveBy) {
		this.arriveBy = arriveBy;
	}

	/**
	 * @param wheelchair
	 *            the wheelchair to set
	 */
	public void setWheelchair(Boolean wheelchair) {
		this.wheelchair = wheelchair;
	}

	/**
	 * @param maxWalkDistance
	 *            the maxWalkDistance to set
	 */
	public void setMaxWalkDistance(Double maxWalkDistance) {
		this.maxWalkDistance = maxWalkDistance;
	}

	/**
	 * @param walkSpeed
	 *            the walkSpeed to set
	 */
	public void setWalkSpeed(Double walkSpeed) {
		this.walkSpeed = walkSpeed;
	}

	/**
	 * @param optimize
	 *            the optimize to set
	 */
	public void setOptimize(OptimizeType optimize) {
		this.optimize = optimize;
	}

	/**
	 * @param modes
	 *            the modes to set
	 */
	public void setModes(TraverseModeSet modes) {
		this.modes = modes;
	}

	/**
	 * @param minTransferTime
	 *            the minTransferTime to set
	 */
	public void setMinTransferTime(Integer minTransferTime) {
		this.minTransferTime = minTransferTime;
	}

	/**
	 * @param numItineraries
	 *            the numItineraries to set
	 */
	public void setNumItineraries(Integer numItineraries) {
		this.numItineraries = numItineraries;
	}

	/**
	 * @param showIntermediateStops
	 *            the showIntermediateStops to set
	 */
	public void setShowIntermediateStops(Boolean showIntermediateStops) {
		this.showIntermediateStops = showIntermediateStops;
	}

	private StringBuilder stringBuilder;
	private boolean firstParam;

	private void appendToUrl(String param, Object value) {
		if (value != null) {
			appendToUrl(param, value.toString());
		}

	}

	private void appendToUrl(String param, String value) {
		if (value == null) {
			return;
		}
		if (!firstParam) {
			stringBuilder.append('&');
		}
		stringBuilder.append(param);
		stringBuilder.append('=');
		stringBuilder.append(value);
		firstParam = false;
	}

	private String latLonToString(Double latitude, Double longitude) {
		if (latitude == null || longitude == null) {
			return null;
		}
		return new StringBuilder().append(latitude).append(',').append(longitude).toString();
	}

	private synchronized String formatDate() {
		if (date == null) {
			return null;
		}
		return DATE_FORMAT.format(date);
	}

	private synchronized String formatTime() {
		if (date == null) {
			return null;
		}
		return TIME_FORMAT.format(date);

	}

	public String constructUrl(String urlBase) {
		firstParam = true;
		stringBuilder = new StringBuilder(urlBase);
		stringBuilder.append('?');
		appendToUrl(FROM, latLonToString(fromPlaceLatitude, fromPlaceLongitude));
		appendToUrl(TO, latLonToString(toPlaceLatitude, toPlaceLongitude));
		appendToUrl(DATE, formatDate());
		appendToUrl(TIME, formatTime());
		appendToUrl(ARRIVE_BY, arriveBy);
		appendToUrl(WHEELCHAIR, wheelchair);
		appendToUrl(MAX_WALK_DISTANCE, maxWalkDistance);
		appendToUrl(WALK_SPEED, walkSpeed);
		appendToUrl(OPTIMIZE, optimize);
		appendToUrl(MODE, modes);
		appendToUrl(MIN_TRANSFER_TIME, minTransferTime);
		appendToUrl(NUMBER_ITINERARIES, numItineraries);
		appendToUrl(SHOW_INTERMEDIATE_STOPS, showIntermediateStops);

		return stringBuilder.toString();

	}

	@Override
	public String toString() {
		return "Request [fromPlaceLatitude=" + fromPlaceLatitude + ", fromPlaceLongitude=" + fromPlaceLongitude
				+ ", toPlaceLatitude=" + toPlaceLatitude + ", toPlaceLongitude=" + toPlaceLongitude + ", date=" + date
				+ ", arriveBy=" + arriveBy + ", wheelchair=" + wheelchair + ", maxWalkDistance=" + maxWalkDistance
				+ ", walkSpeed=" + walkSpeed + ", optimize=" + optimize + ", modes=" + modes + ", minTransferTime="
				+ minTransferTime + ", numItineraries=" + numItineraries + ", showIntermediateStops="
				+ showIntermediateStops + ", stringBuilder=" + stringBuilder + ", firstParam=" + firstParam + "]";
	}
	
	

}
