package fr.ybo.itineraires;

import fr.ybo.opentripplanner.client.ClientOpenTripPlanner;

public class CalculItineraires {

	private static final String URL_OTP = "http://transports-rennes.ic-s.org";

	private static ClientOpenTripPlanner instance;

	public static synchronized ClientOpenTripPlanner getInstance() {
		if (instance == null) {
			instance = new ClientOpenTripPlanner(URL_OTP);
		}
		return instance;
	}

	private CalculItineraires() {
	}
}
