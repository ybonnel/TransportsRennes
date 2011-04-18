package fr.ybo.opentripplanner.client;

import fr.ybo.opentripplanner.client.modele.Request;

public class OpenTripPlannerException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Request request;

	public OpenTripPlannerException(Throwable throwable, Request request) {
		super(throwable);
		this.request = request;
	}

	public OpenTripPlannerException(Throwable throwable) {
		super(throwable);
	}

	public Request getRequest() {
		return request;
	}
	
}
