package fr.ybo.opentripplanner.client.modele;

import java.util.ArrayList;
import java.util.List;

public class Legs {

	public Legs() {
	}

	public List<Leg> legs = new ArrayList<Leg>();

	public void addLeg(Leg leg) {
		this.legs.add(leg);
	}
}
