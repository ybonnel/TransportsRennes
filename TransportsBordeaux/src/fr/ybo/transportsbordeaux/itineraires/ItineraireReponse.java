package fr.ybo.transportsbordeaux.itineraires;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import fr.ybo.opentripplanner.client.modele.Itinerary;
import fr.ybo.opentripplanner.client.modele.TripPlan;

@SuppressWarnings("serial")
public class ItineraireReponse implements Serializable {
	private List<Trajet> trajets;

	public List<Trajet> getTrajets() {
		if (trajets == null) {
			trajets = new ArrayList<Trajet>();
		}
		return trajets;
	}

	public static ItineraireReponse convert(TripPlan tripPlan) {
		if (tripPlan == null) {
			return null;
		}
		ItineraireReponse itineraireReponse = new ItineraireReponse();
		if (tripPlan.itineraries != null) {
			for (Itinerary itinerary : tripPlan.itineraries.itinerary) {
				itineraireReponse.getTrajets().add(Trajet.convert(itinerary));
			}
		}
		return itineraireReponse;

	}
}
