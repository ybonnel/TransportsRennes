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
package fr.ybo.transportsrennes.keolis.xml.sax;

import fr.ybo.transportsrennes.keolis.modele.bus.ParkRelai;

/**
 * Handler pour rÃ©cupÃ©rer les parks relais.
 *
 * @author ybonnel
 */
public class GetParkRelaiHandler extends KeolisHandler<ParkRelai> {

	/**
	 * RELAY_PARK.
	 */
	private static final String RELAY_PARK = "relaypark";
	/**
	 * NAME.
	 */
	private static final String NAME = "name";
	/**
	 * LATITUDE.
	 */
	private static final String LATITUDE = "latitude";
	/**
	 * LONGITUDE.
	 */
	private static final String LONGITUDE = "longitude";
	/**
	 * CAR_PARK_AVAILABLE.
	 */
	private static final String CAR_PARK_AVAILABLE = "carparkavailable";
	/**
	 * CAR_PARK_CAPACITY.
	 */
	private static final String CAR_PARK_CAPACITY = "carparkcapacity";
	/**
	 * LAST_UPDATE.
	 */
	private static final String LAST_UPDATE = "lastupdate";
	/**
	 * STATE.
	 */
	private static final String STATE = "state";

	@Override
	protected String getBaliseData() {
		return RELAY_PARK;
	}

	@Override
	protected ParkRelai getNewObjetKeolis() {
		return new ParkRelai();
	}

	@Override
	protected void remplirObjectKeolis(ParkRelai currentObjectKeolis, String baliseName, String contenuOfBalise) {
		if (baliseName.equals(NAME)) {
			currentObjectKeolis.name = contenuOfBalise;
		} else if (baliseName.equals(LATITUDE)) {
			currentObjectKeolis.latitude = Double.parseDouble(contenuOfBalise);
		} else if (baliseName.equals(LONGITUDE)) {
			currentObjectKeolis.longitude = Double.parseDouble(contenuOfBalise);
		} else if (baliseName.equals(CAR_PARK_AVAILABLE)) {
			currentObjectKeolis.carParkAvailable = Integer.parseInt(contenuOfBalise);
		} else if (baliseName.equals(CAR_PARK_CAPACITY)) {
			currentObjectKeolis.carParkCapacity = Integer.parseInt(contenuOfBalise);
		} else if (baliseName.equals(LAST_UPDATE)) {
			currentObjectKeolis.lastupdate = contenuOfBalise;
		} else if (baliseName.equals(STATE)) {
			currentObjectKeolis.state = Integer.parseInt(contenuOfBalise);
		}
	}
}
