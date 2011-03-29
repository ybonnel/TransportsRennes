package fr.ybo.opentripplanner.client;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import fr.ybo.opentripplanner.client.modele.GraphMetadata;

public class MetadataTest {

	@Test
	public void testGetMetadata() throws OpenTripPlannerException {
		System.out.println(Constantes.URL_METADATA);
		GraphMetadata metadata = Metadata.getMetadata();
		assertNotNull(metadata);
		System.out.println("minLatitude : " + metadata.getMinLatitude());
		System.out.println("maxLatitude : " + metadata.getMaxLatitude());
		System.out.println("minLongitude : " + metadata.getMinLongitude());
		System.out.println("maxLongitude : " + metadata.getMaxLongitude());

	}

}
