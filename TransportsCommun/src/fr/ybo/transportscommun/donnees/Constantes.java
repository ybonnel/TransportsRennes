/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.ybo.transportscommun.donnees;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fr.ybo.transportscommun.donnees.modele.Arret;
import fr.ybo.transportscommun.donnees.modele.ArretRoute;
import fr.ybo.transportscommun.donnees.modele.Calendrier;
import fr.ybo.transportscommun.donnees.modele.CalendrierException;
import fr.ybo.transportscommun.donnees.modele.DernierMiseAJour;
import fr.ybo.transportscommun.donnees.modele.Direction;
import fr.ybo.transportscommun.donnees.modele.Horaire;
import fr.ybo.transportscommun.donnees.modele.Ligne;
import fr.ybo.transportscommun.donnees.modele.Trajet;

public final class Constantes {

    public static final Collection<Class<?>> CLASSES_DB_TO_DELETE_ON_UPDATE = new ArrayList<Class<?>>(7);

    static {
        CLASSES_DB_TO_DELETE_ON_UPDATE.add(Arret.class);
		CLASSES_DB_TO_DELETE_ON_UPDATE.add(Calendrier.class);
		CLASSES_DB_TO_DELETE_ON_UPDATE.add(CalendrierException.class);
        CLASSES_DB_TO_DELETE_ON_UPDATE.add(DernierMiseAJour.class);
        CLASSES_DB_TO_DELETE_ON_UPDATE.add(Ligne.class);
        CLASSES_DB_TO_DELETE_ON_UPDATE.add(ArretRoute.class);
        CLASSES_DB_TO_DELETE_ON_UPDATE.add(Trajet.class);
        CLASSES_DB_TO_DELETE_ON_UPDATE.add(Direction.class);
    }

    public static final List<Class<?>> LIST_CLASSES_GTFS = new ArrayList<Class<?>>(7);

    static {
        LIST_CLASSES_GTFS.add(Arret.class);
        LIST_CLASSES_GTFS.add(ArretRoute.class);
        LIST_CLASSES_GTFS.add(Calendrier.class);
		LIST_CLASSES_GTFS.add(CalendrierException.class);
        LIST_CLASSES_GTFS.add(Horaire.class);
        LIST_CLASSES_GTFS.add(Ligne.class);
        LIST_CLASSES_GTFS.add(Trajet.class);
        LIST_CLASSES_GTFS.add(Direction.class);
    }

    private Constantes() {
    }
}
