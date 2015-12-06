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

import fr.ybo.opentripplanner.client.modele.Leg;
import fr.ybo.opentripplanner.client.modele.TraverseMode;

import java.io.Serializable;
import java.util.Date;


public class PortionTrajet implements Serializable {

    public static PortionTrajet convert(final Leg leg) {
        final PortionTrajet portionTrajet = new PortionTrajet();
        portionTrajet.mode = TraverseMode.valueOf(leg.mode);
        portionTrajet.ligneId = leg.route;
        portionTrajet.startTime = leg.startTime;
        portionTrajet.endTime = leg.endTime;
        if (leg.from != null) {
            portionTrajet.fromName = leg.from.name;
        }
        if (leg.to != null) {
            portionTrajet.toName = leg.to.name;
        }
        if (leg.headsign != null) {
            portionTrajet.direction = leg.getDirection();
        }
        return portionTrajet;
    }

    private TraverseMode mode;
    private String ligneId;
    private Date startTime;
    private Date endTime;
    private String fromName;
    private String toName;
    private String direction;

    public TraverseMode getMode() {
        return mode;
    }

    public String getLigneId() {
        return ligneId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public CharSequence getFromName() {
        return fromName;
    }

    public CharSequence getToName() {
        return toName;
    }

    public String getDirection() {
        return direction;
    }

}
