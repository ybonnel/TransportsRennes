package fr.ybo.transportsbordeaux.itineraires;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import fr.ybo.opentripplanner.client.modele.Itinerary;
import fr.ybo.opentripplanner.client.modele.Leg;

public class Trajet implements Serializable {

	private List<PortionTrajet> portions;
	private Date endTime;

	public Collection<PortionTrajet> getPortions() {
		if (portions == null) {
			portions = new ArrayList<PortionTrajet>();
		}
		return portions;
	}

	public static Trajet convert(final Itinerary itinerary) {
		final Trajet trajet = new Trajet();
		trajet.endTime = itinerary.endTime;
		if (itinerary.legs != null) {
			for (final Leg leg : itinerary.legs.leg) {
				trajet.getPortions().add(PortionTrajet.convert(leg));
			}
		}
		return trajet;
	}

	public Date getEndTime() {
		return endTime;
	}

}
