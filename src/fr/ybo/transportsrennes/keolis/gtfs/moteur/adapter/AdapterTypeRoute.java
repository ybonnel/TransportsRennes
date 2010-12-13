package fr.ybo.transportsrennes.keolis.gtfs.moteur.adapter;

import fr.ybo.transportsrennes.keolis.gtfs.modele.TypeRoutes;

public class AdapterTypeRoute implements AdapterCsv<TypeRoutes> {

	public TypeRoutes parse(final String chaine) {
		return TypeRoutes.valueOf(Integer.parseInt(chaine));
	}

}
