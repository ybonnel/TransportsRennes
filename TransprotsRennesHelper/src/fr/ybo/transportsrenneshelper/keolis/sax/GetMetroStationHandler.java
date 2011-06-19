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
package fr.ybo.transportsrenneshelper.keolis.sax;

import fr.ybo.transportsrenneshelper.keolis.modele.MetroStation;

/**
 * Handler pour les stations de métro.
 * @author ybonnel
 *
 */
public class GetMetroStationHandler extends KeolisHandler<MetroStation> {

	// CHECKSTYLE:OFF
	private static final String STATION = "station";
	private static final String ID = "id";
	private static final String NAME = "name";
	private static final String LATITUDE = "latitude";
	private static final String LONGITUDE = "longitude";
	private static final String HAS_PLATFORM_DIRECTION_1 = "hasPlatformDirection1";
	private static final String HAS_PLATFORM_DIRECTION_2 = "hasPlatformDirection2";
	private static final String RANKING_PLATFORM_DIRECTION_1 = "rankingPlatformDirection1";
	private static final String RANKING_PLATFORM_DIRECTION_2 = "rankingPlatformDirection2";
	private static final String FLOORS = "floors";
	private static final String LAST_UPDATE = "lastupdate";
	// CHECKSTYLE:ON
	@Override
	protected String getBaliseData() {
		return STATION;
	}

	@Override
	protected MetroStation getNewObjetKeolis() {
		return new MetroStation();
	}

	@Override
	protected void remplirObjectKeolis(MetroStation currentObjectKeolis, String baliseName, String contenuOfBalise) {
		if (baliseName.equals(ID)) {
			currentObjectKeolis.setId(contenuOfBalise);
		} else if (baliseName.equals(NAME)) {
			currentObjectKeolis.setName(contenuOfBalise);
		} else if (baliseName.equals(LATITUDE)) {
			currentObjectKeolis.setLatitude(Double.parseDouble(contenuOfBalise));
		} else if (baliseName.equals(LONGITUDE)) {
			currentObjectKeolis.setLongitude(Double.parseDouble(contenuOfBalise));
		} else if (baliseName.equals(HAS_PLATFORM_DIRECTION_1)) {
			currentObjectKeolis.setHasPlatformDirection1("1".equals(contenuOfBalise));
		} else if (baliseName.equals(HAS_PLATFORM_DIRECTION_2)) {
			currentObjectKeolis.setHasPlatformDirection2("1".equals(contenuOfBalise));
		} else if (baliseName.equals(RANKING_PLATFORM_DIRECTION_1)) {
			if (!"".equals(contenuOfBalise)) {
				currentObjectKeolis.setRankingPlatformDirection1(Integer.parseInt(contenuOfBalise));
			}
		} else if (baliseName.equals(RANKING_PLATFORM_DIRECTION_2)) {
			if (!"".equals(contenuOfBalise)) {
				currentObjectKeolis.setRankingPlatformDirection2(Integer.parseInt(contenuOfBalise));
			}
		} else if (baliseName.equals(FLOORS)) {
			currentObjectKeolis.setFloors(Integer.parseInt(contenuOfBalise));
		} else if (baliseName.equals(LAST_UPDATE)) {
			currentObjectKeolis.setLastupdate(contenuOfBalise);
		}
	}
}
