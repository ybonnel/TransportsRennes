package fr.ybo.opentripplanner.client.modele;

import java.util.ArrayList;
import java.util.List;

public class Itineraries {

	public Itineraries() {
	}

	public List<Itinerary> itinerary = new ArrayList<Itinerary>();

	public void addItinerary(Itinerary itinerary) {
		this.itinerary.add(itinerary);
	}
}
