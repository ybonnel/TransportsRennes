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

package fr.ybo.transportsrennes.itineraires;

import fr.ybo.opentripplanner.client.modele.Itinerary;
import fr.ybo.opentripplanner.client.modele.TripPlan;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class ItineraireReponse implements Serializable {
    private List<Trajet> trajets;

    public List<Trajet> getTrajets() {
        if (trajets == null) {
            trajets = new ArrayList<Trajet>();
        }
        return trajets;
    }

    public static ItineraireReponse convert(TripPlan tripPlan) {
        if (tripPlan == null) {
            return null;
        }
        ItineraireReponse itineraireReponse = new ItineraireReponse();
        if (tripPlan.itineraries != null) {
            for (Itinerary itinerary : tripPlan.itineraries.itinerary) {
                itineraireReponse.getTrajets().add(Trajet.convert(itinerary));
            }
        }
        return itineraireReponse;

    }
}
