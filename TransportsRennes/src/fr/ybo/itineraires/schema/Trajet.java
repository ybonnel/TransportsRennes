package fr.ybo.itineraires.schema;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings({"serial"})
public class Trajet implements Serializable {

	private ArrayList<PortionTrajet> portions;

	public List<PortionTrajet> getPortions() {
		if (portions == null) {
			portions = new ArrayList<PortionTrajet>(10);
		}
		return portions;
	}

}
