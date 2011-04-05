package fr.ybo.opentripplanner.client;

import fr.ybo.opentripplanner.client.modele.GraphMetadata;
import fr.ybo.opentripplanner.client.modele.Request;
import fr.ybo.opentripplanner.client.modele.Response;

public class ClientOpenTripPlanner {

	private Metadata metadata;
	private Planner planner;

	public ClientOpenTripPlanner(String baseUrl) {
		metadata = new Metadata(baseUrl);
		planner = new Planner(baseUrl);
	}

	/**
	 * @return
	 * @throws OpenTripPlannerException
	 * @see fr.ybo.opentripplanner.client.Metadata#getMetadata()
	 */
	public GraphMetadata getMetadata() throws OpenTripPlannerException {
		return metadata.getMetadata();
	}

	/**
	 * @param request
	 * @return
	 * @throws OpenTripPlannerException
	 * @see fr.ybo.opentripplanner.client.Planner#getItineraries(fr.ybo.opentripplanner.client.modele.Request)
	 */
	public Response getItineraries(Request request) throws OpenTripPlannerException {
		return planner.getItineraries(request);
	}
}
