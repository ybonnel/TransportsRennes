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
package fr.ybo.transportsrennes.keolis;

import java.util.ArrayList;
import java.util.List;

import fr.ybo.transportscommun.donnees.modele.Arret;
import fr.ybo.transportscommun.donnees.modele.ArretFavori;
import fr.ybo.transportscommun.donnees.modele.ArretRoute;
import fr.ybo.transportscommun.donnees.modele.Bounds;
import fr.ybo.transportscommun.donnees.modele.Calendrier;
import fr.ybo.transportscommun.donnees.modele.CalendrierException;
import fr.ybo.transportscommun.donnees.modele.DernierMiseAJour;
import fr.ybo.transportscommun.donnees.modele.Direction;
import fr.ybo.transportscommun.donnees.modele.GroupeFavori;
import fr.ybo.transportscommun.donnees.modele.Horaire;
import fr.ybo.transportscommun.donnees.modele.Ligne;
import fr.ybo.transportscommun.donnees.modele.Notification;
import fr.ybo.transportscommun.donnees.modele.Trajet;
import fr.ybo.transportscommun.donnees.modele.VeloFavori;
import fr.ybo.transportsrennes.database.modele.AlertBdd;

public final class ConstantesKeolis {

    public static final List<Class<?>> LIST_CLASSES_DATABASE = new ArrayList<Class<?>>(10);

    static {
        LIST_CLASSES_DATABASE.add(Arret.class);
        LIST_CLASSES_DATABASE.add(Calendrier.class);
		LIST_CLASSES_DATABASE.add(CalendrierException.class);
        LIST_CLASSES_DATABASE.add(DernierMiseAJour.class);
        LIST_CLASSES_DATABASE.add(Ligne.class);
        LIST_CLASSES_DATABASE.add(Horaire.class);
        LIST_CLASSES_DATABASE.add(ArretFavori.class);
        LIST_CLASSES_DATABASE.add(ArretRoute.class);
        LIST_CLASSES_DATABASE.add(VeloFavori.class);
        LIST_CLASSES_DATABASE.add(Trajet.class);
        LIST_CLASSES_DATABASE.add(Direction.class);
        LIST_CLASSES_DATABASE.add(GroupeFavori.class);
        LIST_CLASSES_DATABASE.add(Notification.class);
        LIST_CLASSES_DATABASE.add(AlertBdd.class);
        LIST_CLASSES_DATABASE.add(Bounds.class);
    }

    private ConstantesKeolis() {
    }
}
