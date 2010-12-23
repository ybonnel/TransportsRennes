package fr.ybo.transportsrenneshelper.keolis.gtfs.moteur.adapter;

import fr.ybo.transportsrenneshelper.keolis.gtfs.modele.TypeRoutes;

public class AdapterTypeRoute implements AdapterCsv<TypeRoutes> {

	public TypeRoutes parse(final String chaine) {
		return TypeRoutes.valueOf(Integer.parseInt(chaine));
	}

	public String toString(TypeRoutes typeRoutes) {
		return Integer.toString(typeRoutes.getIndice());
	}

}
