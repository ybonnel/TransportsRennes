package fr.ybo.transportsbordeaux.itineraires;

import java.io.Serializable;
import java.util.Date;

import fr.ybo.opentripplanner.client.modele.Leg;
import fr.ybo.opentripplanner.client.modele.TraverseMode;


public class PortionTrajet implements Serializable {

	public static PortionTrajet convert(final Leg leg) {
		final PortionTrajet portionTrajet = new PortionTrajet();
		portionTrajet.mode = TraverseMode.valueOf(leg.mode);
		portionTrajet.ligneId = leg.route;
		portionTrajet.startTime = leg.startTime;
		portionTrajet.endTime = leg.endTime;
		if (leg.from != null) {
			portionTrajet.fromName = leg.from.name;
		}
		if (leg.to != null) {
			portionTrajet.toName = leg.to.name;
		}
		if (leg.headsign != null) {
			portionTrajet.direction = leg.getDirection();
		}
		return portionTrajet;
	}

	private TraverseMode mode;
	private String ligneId;
	private Date startTime;
	private Date endTime;
	private String fromName;
	private String toName;
	private String direction;

	public TraverseMode getMode() {
		return mode;
	}

	public String getLigneId() {
		return ligneId;
	}

	public Date getStartTime() {
		return startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public CharSequence getFromName() {
		return fromName;
	}

	public CharSequence getToName() {
		return toName;
	}

	public String getDirection() {
		return direction;
	}

}
