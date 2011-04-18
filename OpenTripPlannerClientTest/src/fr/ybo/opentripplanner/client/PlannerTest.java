package fr.ybo.opentripplanner.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

import fr.ybo.opentripplanner.client.modele.Itinerary;
import fr.ybo.opentripplanner.client.modele.Leg;
import fr.ybo.opentripplanner.client.modele.Request;
import fr.ybo.opentripplanner.client.modele.Response;

public class PlannerTest {
	

	@Test
	public void testPlannerAno() throws ParseException, OpenTripPlannerException {
		SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date date = SDF.parse("05/04/2011 12:00:00");
		Request request = new Request(48.3349386, -1.1211244, 48.1160495, -1.6789079, date);
		ClientOpenTripPlanner client = new ClientOpenTripPlanner("http://transports-rennes.ic-s.org");
		client.getItineraries(request);
	}

	// 91 rue de paris :
	// lat=48.1138212, lng=-1.6606638
	// 29 rue d'antrain
	// lat=48.1160495, lng=-1.6789079

	@Test
	public void testPlanner() throws ParseException, OpenTripPlannerException {
		SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date date = SDF.parse("05/04/2011 12:00:00");
		Request request = new Request(48.1138212, -1.6606638, 48.1160495, -1.6789079, date);
		ClientOpenTripPlanner client = new ClientOpenTripPlanner("http://transports-rennes.ic-s.org");
		Response response = client.getItineraries(request);
		assertNotNull(response);
		assertNull(response.getError());
		assertNotNull(response.getPlan());
		assertEquals(date, response.getPlan().date);
		assertNotNull(response.getPlan().from);
		assertEquals(48.1138212, response.getPlan().from.lat, 0.001);
		assertEquals(-1.6606638, response.getPlan().from.lon, 0.001);
		assertEquals("Rue de Paris", response.getPlan().from.name);
		assertEquals(48.1160495, response.getPlan().to.lat, 0.001);
		assertEquals(-1.6789079, response.getPlan().to.lon, 0.001);
		assertEquals("Rue d'Antrain", response.getPlan().to.name);
		assertEquals(3, response.getPlan().itineraries.itinerary.size());
		Itinerary itineraire = response.getPlan().itineraries.itinerary.get(0);
		// 20 minutes et 41 secondes
		assertEquals(1241000, itineraire.duration);
		assertEquals(SDF.parse("05/04/2011 12:03:15"), itineraire.startTime);
		assertEquals(SDF.parse("05/04/2011 12:23:56"), itineraire.endTime);
		// 5 minutes et 58 secondes
		assertEquals(358000, itineraire.walkTime);
		// 11 minutes
		assertEquals(660000, itineraire.transitTime);
		// 3 minutes 43 secondes
		assertEquals(223000, itineraire.waitingTime);
		assertEquals(481.512, itineraire.walkDistance, 0.001);
		assertEquals(0.0, itineraire.elevationLost, 0.001);
		assertEquals(0.0, itineraire.elevationGained, 0.001);
		assertEquals(1, itineraire.transfers.intValue());
		assertFalse(itineraire.tooSloped);
		assertTrue(itineraire.fare.fare.isEmpty());
		assertNotNull(itineraire.legs);
		assertEquals(5, itineraire.legs.leg.size());
		// Première étape, à pied jusqu'à l'arret oberthur.
		Leg leg = itineraire.legs.leg.get(0);
		assertEquals("WALK", leg.mode);
		

		// Bus de oberthur à république
		leg = itineraire.legs.leg.get(1);
		assertEquals("BUS", leg.mode);
		assertEquals("3", leg.route);
		assertEquals("3 | Alma", leg.headsign);
		System.out.println(leg.legGeometry);
		assertEquals("oberthu1", leg.from.stopId.id);
		assertEquals("repjaurs", leg.to.stopId.id);

		leg = itineraire.legs.leg.get(2);
		assertEquals("WALK", leg.mode);
		leg = itineraire.legs.leg.get(3);
		assertEquals("SUBWAY", leg.mode);
		assertEquals("a", leg.route);
		assertEquals("J.F. Kennedy", leg.headsign);
		assertEquals("REP1", leg.from.stopId.id);
		assertEquals("STA1", leg.to.stopId.id);
		leg = itineraire.legs.leg.get(4);
		assertEquals("WALK", leg.mode);
	}

	@Test(expected = OpenTripPlannerException.class)
	public void testPlannerError() throws ParseException, OpenTripPlannerException {
		SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date date = SDF.parse("05/04/2011 12:00:00");
		Request request = new Request(48.1138212, -1.6606638, 48.1160495, -1.6789079, date);
		ClientOpenTripPlanner client = new ClientOpenTripPlanner("http://tutu:8080");
		client.getItineraries(request);
	}

}
