package fr.ybo.transportsbordeaux.itineraires;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.ybo.opentripplanner.client.modele.Itinerary;
import fr.ybo.opentripplanner.client.modele.Leg;

@SuppressWarnings("serial")
public class Trajet implements Serializable {

	private List<PortionTrajet> portions;
	private Date endTime;

	public List<PortionTrajet> getPortions() {
		if (portions == null) {
			portions = new ArrayList<PortionTrajet>();
		}
		return portions;
	}

	public static Trajet convert(Itinerary itinerary) {
		Trajet trajet = new Trajet();
		trajet.endTime = itinerary.endTime;
		if (itinerary.legs != null) {
			for (Leg leg : itinerary.legs.leg) {
				trajet.getPortions().add(PortionTrajet.convert(leg));
			}
		}
		return trajet;
	}

	public Date getEndTime() {
		return endTime;
	}

}
