/* This program is free software: you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public License
 as published by the Free Software Foundation, either version 3 of
 the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>. */

package fr.ybo.opentripplanner.client.modele;



import java.io.Serializable;
import java.util.Date;

/**
 * A TripPlan is a set of ways to get from point A to point B at time T.
 */
public class TripPlan implements Serializable {

	private static final long serialVersionUID = 1L;
	/** 
     * The time and date of travel 
     */
    public Date date = null;
    /**
     * The origin
     */
    public Place from = null;
    /**
     * The destination
     */
    public Place to = null;

    /** 
     * A list of possible itineraries. 
     */
	public Itineraries itineraries = null;

    public TripPlan() {}
    
    public TripPlan(Place from, Place to, Date date) {
        this.from = from;
        this.to = to;
        this.date = date;
    }
}
