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

import fr.ybo.transportsrennes.keolis.modele.bus.PointDeVente;

/**
 * Handler permettant de rÃ©cupÃ©rer les points de vente.
 *
 * @author ybonnel
 */
public class GetPointDeVenteHandler extends KeolisHandler<PointDeVente> {

	/**
	 * POS.
	 */
	private static final String POS = "pos";
	/**
	 * NAME.
	 */
	private static final String NAME = "name";
	/**
	 * TYPE.
	 */
	private static final String TYPE = "type";
	/**
	 * ADRESSE.
	 */
	private static final String ADRESSE = "address";
	/**
	 * CODE_POSTAL.
	 */
	private static final String CODE_POSTAL = "zipcode";
	/**
	 * VILLE.
	 */
	private static final String VILLE = "city";
	/**
	 * DISTRICT.
	 */
	private static final String DISTRICT = "district";
	/**
	 * TELEPHONE.
	 */
	private static final String TELEPHONE = "phone";
	/**
	 * SCHEDULE.
	 */
	private static final String SCHEDULE = "schedule";
	/**
	 * LATITUDE.
	 */
	private static final String LATITUDE = "latitude";
	/**
	 * LONGITUDE.
	 */
	private static final String LONGITUDE = "longitude";

	@Override
	protected String getBaliseData() {
		return POS;
	}

	@Override
	protected PointDeVente getNewObjetKeolis() {
		return new PointDeVente();
	}

	@Override
	protected void remplirObjectKeolis(PointDeVente currentObjectKeolis, String baliseName, String contenuOfBalise) {
		if (baliseName.equals(NAME)) {
			currentObjectKeolis.name = contenuOfBalise;
		} else if (baliseName.equals(TYPE)) {
			currentObjectKeolis.type = contenuOfBalise;
		} else if (baliseName.equals(ADRESSE)) {
			currentObjectKeolis.adresse = contenuOfBalise;
		} else if (baliseName.equals(CODE_POSTAL)) {
			currentObjectKeolis.codePostal = contenuOfBalise;
		} else if (baliseName.equals(VILLE)) {
			currentObjectKeolis.ville = contenuOfBalise;
		} else if (baliseName.equals(DISTRICT)) {
			currentObjectKeolis.district = contenuOfBalise;
		} else if (baliseName.equals(TELEPHONE)) {
			currentObjectKeolis.telephone = contenuOfBalise;
		} else if (baliseName.equals(SCHEDULE)) {
			currentObjectKeolis.schedule = contenuOfBalise;
		} else if (baliseName.equals(LATITUDE)) {
			currentObjectKeolis.latitude = Double.parseDouble(contenuOfBalise);
		} else if (baliseName.equals(LONGITUDE)) {
			currentObjectKeolis.longitude = Double.parseDouble(contenuOfBalise);
		}
	}
}
