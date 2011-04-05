package fr.ybo.opentripplanner.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import fr.ybo.opentripplanner.client.modele.GraphMetadata;

public class MetadataTest {

	@Test
	public void testGetMetadata() throws OpenTripPlannerException {
		GraphMetadata metadata = new ClientOpenTripPlanner("http://127.0.0.1:8080").getMetadata();
		assertNotNull(metadata);
		assertEquals(47.7883051, metadata.getMinLatitude(), 0.0000001);
		assertEquals(48.3784029, metadata.getMaxLatitude(), 0.0000001);
		assertEquals(-1.9928184, metadata.getMinLongitude(), 0.0000001);
		assertEquals(-1.4078096, metadata.getMaxLongitude(), 0.0000001);
	}

	@Test(expected = OpenTripPlannerException.class)
	public void testGetMetadataErreur() throws OpenTripPlannerException {
		new ClientOpenTripPlanner("http://tutu:8080").getMetadata();
	}

}
