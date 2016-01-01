package fr.ybo.transportsbordeaux.itineraires;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import fr.ybo.opentripplanner.client.modele.Itinerary;
import fr.ybo.opentripplanner.client.modele.TripPlan;

public class ItineraireReponse implements Serializable {
	private List<Trajet> trajets;

	public List<Trajet> getTrajets() {
		if (trajets == null) {
			trajets = new ArrayList<Trajet>();
		}
		return trajets;
	}

	public static Serializable convert(final TripPlan tripPlan) {
		if (tripPlan == null) {
			return null;
		}
		final ItineraireReponse itineraireReponse = new ItineraireReponse();
		if (tripPlan.itineraries != null) {
			for (final Itinerary itinerary : tripPlan.itineraries.itinerary) {
				itineraireReponse.getTrajets().add(Trajet.convert(itinerary));
			}
		}
		return itineraireReponse;

	}
}
