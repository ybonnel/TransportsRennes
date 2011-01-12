/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
		LIST_CLASSES_DATABASE.add(Ligne.class);
		LIST_CLASSES_DATABASE.add(Horaire.class);
		LIST_CLASSES_DATABASE.add(ArretFavori.class);
		LIST_CLASSES_DATABASE.add(ArretRoute.class);
		LIST_CLASSES_DATABASE.add(VeloFavori.class);
		LIST_CLASSES_DATABASE.add(Trajet.class);
		LIST_CLASSES_DATABASE.add(Direction.class);
	}

	public static final List<Class<?>> LIST_CLASSES_DATABASE_TO_DELETE_ON_UPDATE = new ArrayList<Class<?>>();

	static {
		LIST_CLASSES_DATABASE_TO_DELETE_ON_UPDATE.add(Arret.class);
		LIST_CLASSES_DATABASE_TO_DELETE_ON_UPDATE.add(Calendrier.class);
		LIST_CLASSES_DATABASE_TO_DELETE_ON_UPDATE.add(DernierMiseAJour.class);
		LIST_CLASSES_DATABASE_TO_DELETE_ON_UPDATE.add(Ligne.class);
		LIST_CLASSES_DATABASE_TO_DELETE_ON_UPDATE.add(ArretRoute.class);
		LIST_CLASSES_DATABASE_TO_DELETE_ON_UPDATE.add(Trajet.class);
		LIST_CLASSES_DATABASE_TO_DELETE_ON_UPDATE.add(Direction.class);
	}

	public static final List<Class<?>> LIST_CLASSES_GTFS = new ArrayList<Class<?>>();

	static {
		LIST_CLASSES_GTFS.add(Arret.class);
		LIST_CLASSES_GTFS.add(ArretRoute.class);
		LIST_CLASSES_GTFS.add(Calendrier.class);
		LIST_CLASSES_GTFS.add(Horaire.class);
		LIST_CLASSES_GTFS.add(Ligne.class);
		LIST_CLASSES_GTFS.add(Trajet.class);
		LIST_CLASSES_GTFS.add(Direction.class);
	}

}
