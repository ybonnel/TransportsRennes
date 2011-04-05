package fr.ybo.opentripplanner.client.modele;

import java.util.ArrayList;
import java.util.List;

public class Legs {

	public Legs() {
	}

	public List<Leg> leg = new ArrayList<Leg>();

	public void addLeg(Leg leg) {
		this.leg.add(leg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Legs [legs=" + leg + "]";
	}

}
