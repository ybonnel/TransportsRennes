package fr.ybo.opentripplanner.client.modele;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Itineraries implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public Itineraries() {
	}

	public List<Itinerary> itinerary = new ArrayList<Itinerary>();

	public void addItinerary(Itinerary itinerary) {
		this.itinerary.add(itinerary);
	}
}
