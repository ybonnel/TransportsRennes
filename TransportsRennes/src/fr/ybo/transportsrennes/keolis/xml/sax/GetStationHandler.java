/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package fr.ybo.transportsrennes.keolis.xml.sax;

import fr.ybo.transportsrennes.keolis.modele.velos.Station;

/**
 * Handler SAX pour l'api getstation.
 *
 * @author ybonnel
 */
public class GetStationHandler extends KeolisHandler<Station> {

	/**
	 * Nom de la balise station.
	 */
	private static final String STATION = "station";
	/**
	 * Nom de la balise number.
	 */
	private static final String NUMBER = "number";
	/**
	 * Nom de la balise name.
	 */
	private static final String NAME = "name";
	/**
	 * Nom de la balise address.
	 */
	private static final String ADRESSE = "address";
	/**
	 * Nom de la balise state.
	 */
	private static final String STATE = "state";
	/**
	 * Nom de la balise latitude.
	 */
	private static final String LATITUDE = "latitude";
	/**
	 * Nom de la balise longitude.
	 */
	private static final String LONGITUDE = "longitude";
	/**
	 * Nom de la balise slotsavailable.
	 */
	private static final String SLOTSAVAILABLE = "slotsavailable";
	/**
	 * Nom de la balise bikesavailable.
	 */
	private static final String BIKESAVAILABLE = "bikesavailable";
	/**
	 * Nom de la balise pos.
	 */
	private static final String POS = "pos";
	/**
	 * Nom de la balise district.
	 */
	private static final String DISTRICT = "district";
	/**
	 * Nom de la balise lastupdate.
	 */
	private static final String LASTUPDATE = "lastupdate";

	@Override
	protected final String getBaliseData() {
		return STATION;
	}

	@Override
	protected final Station getNewObjetKeolis() {
		return new Station();
	}

	@Override
	protected final void remplirObjectKeolis(final Station currentObjectKeolis, final String baliseName, final String contenu) {
		if (baliseName.equals(NUMBER)) {
			currentObjectKeolis.number = contenu;
		} else if (baliseName.equals(NAME)) {
			currentObjectKeolis.name = contenu;
		} else if (baliseName.equals(ADRESSE)) {
			currentObjectKeolis.adresse = contenu;
		} else if (baliseName.equals(STATE)) {
			currentObjectKeolis.state = Boolean.parseBoolean(contenu);
		} else if (baliseName.equals(LATITUDE)) {
			currentObjectKeolis.latitude = Double.parseDouble(contenu);
		} else if (baliseName.equals(LONGITUDE)) {
			currentObjectKeolis.longitude = Double.parseDouble(contenu);
		} else if (baliseName.equals(SLOTSAVAILABLE)) {
			currentObjectKeolis.slotsavailable = Integer.parseInt(contenu);
		} else if (baliseName.equals(BIKESAVAILABLE)) {
			currentObjectKeolis.bikesavailable = Integer.parseInt(contenu);
		} else if (baliseName.equals(POS)) {
			currentObjectKeolis.pos = "1".equals(contenu);
		} else if (baliseName.equals(DISTRICT)) {
			currentObjectKeolis.district = contenu;
		} else if (baliseName.equals(LASTUPDATE)) {
			currentObjectKeolis.lastupdate = contenu;
		}
	}
}
