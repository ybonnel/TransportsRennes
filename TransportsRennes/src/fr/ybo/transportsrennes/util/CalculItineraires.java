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
 * 
 * Contributors:
 *     ybonnel - initial API and implementation
 */
package fr.ybo.transportsrennes.util;

import fr.ybo.opentripplanner.client.ClientOpenTripPlanner;

public class CalculItineraires {

	private static final String URL_OTP = "http://transports.ybonnel.fr/opentripplanner-api-webapp";

	private static ClientOpenTripPlanner instance;

	public static synchronized ClientOpenTripPlanner getInstance() {
		if (instance == null) {
			instance = new ClientOpenTripPlanner(URL_OTP);
		}
		return instance;
	}

	private CalculItineraires() {
	}
}
