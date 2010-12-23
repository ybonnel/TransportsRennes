package fr.ybo.transportsrennes.keolis;

import fr.ybo.transportsrennes.keolis.gtfs.modele.*;

import java.util.ArrayList;
import java.util.List;

public class ConstantesKeolis {

	public static final List<Class<?>> LIST_CLASSES_DATABASE = new ArrayList<Class<?>>();

	static {
		LIST_CLASSES_DATABASE.add(Arret.class);
		LIST_CLASSES_DATABASE.add(Calendrier.class);
		LIST_CLASSES_DATABASE.add(DernierMiseAJour.class);
		LIST_CLASSES_DATABASE.add(Route.class);
		LIST_CLASSES_DATABASE.add(HeuresArrets.class);
		LIST_CLASSES_DATABASE.add(ArretFavori.class);
		LIST_CLASSES_DATABASE.add(ArretRoute.class);
	}

	public static final List<Class<?>> LIST_CLASSES_GTFS = new ArrayList<Class<?>>();

	static {
		LIST_CLASSES_GTFS.add(Arret.class);
		LIST_CLASSES_GTFS.add(ArretRoute.class);
		LIST_CLASSES_GTFS.add(Calendrier.class);
		LIST_CLASSES_GTFS.add(HeuresArrets.class);
		LIST_CLASSES_GTFS.add(Route.class);
	}

}
