/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     ybonnel - initial API and implementation
 */
package fr.ybo.transportsrennes.keolis.modele.bus;

import java.util.Calendar;
import java.util.List;

/**
 * @author ybonnel
 *
 */
public class ResultDeparture {

	private final List<Departure> departures;

	private final Calendar apiTime;

	public ResultDeparture(List<Departure> departures, Calendar apiTime) {
		super();
		this.departures = departures;
		this.apiTime = apiTime;
	}

	public List<Departure> getDepartures() {
		return departures;
	}

	public Calendar getApiTime() {
		return apiTime;
	}

}
