package fr.ybo.opentripplanner.client;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.Test;

import fr.ybo.opentripplanner.client.modele.Request;
import fr.ybo.opentripplanner.client.modele.Response;

public class PlannerTest {

	@Test
	public void testDateFormat() throws ParseException {
		String date = "2011-04-05T12:00:00+02:00";
		String format = "yyyy-MM-dd'T'HH:mm:ss";
		SimpleDateFormat SDF = new SimpleDateFormat(format);
		System.out.println(SDF.parse(date));
	}

	// 91 rue de paris :
	// lat=48.1138212, lng=-1.6606638
	// 29 rue d'antrain
	// lat=48.1160495, lng=-1.6789079

	@Test
	public void testPlanner() throws ParseException, OpenTripPlannerException {
		SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Request request = new Request(48.1138212, -1.6606638, 48.1160495, -1.6789079, SDF.parse("05/04/2011 12:00:00"));
		System.out.println(request);
		Planner planner = new Planner();
		Response response = planner.getItineraries(request);
		System.out.println(response.getPlan().itineraries.itinerary.size());
		System.out.println(response);

	}

}
