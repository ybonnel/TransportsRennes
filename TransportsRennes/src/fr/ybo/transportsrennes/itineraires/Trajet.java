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
import fr.ybo.opentripplanner.client.modele.Leg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuppressWarnings("serial")
public class Trajet implements Serializable {

    private List<PortionTrajet> portions;
    private Date endTime;

    public List<PortionTrajet> getPortions() {
        if (portions == null) {
            portions = new ArrayList<PortionTrajet>();
        }
        return portions;
    }

    public static Trajet convert(Itinerary itinerary) {
        Trajet trajet = new Trajet();
        trajet.endTime = itinerary.endTime;
        if (itinerary.legs != null) {
            for (Leg leg : itinerary.legs.leg) {
                trajet.getPortions().add(PortionTrajet.convert(leg));
            }
        }
        return trajet;
    }

    public Date getEndTime() {
        return endTime;
    }

}
