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
package fr.ybo.transportsbordeaux.tbcapi.sax;

import fr.ybo.transportsbordeaux.tbcapi.modele.Station;

/**
 * Handler pour rÃ©cupÃ©rer les parks relais.
 *
 * @author ybonnel
 */
public class GetStationHandler extends KeolisHandler<Station> {
    private static final String GID = "GID";
    private static final String NOM = "NOM";
	private static final String NBPLACES = "NBPLACES";
	private static final String NBVELOS = "NBVELOS";
	private static final String ETAT = "ETAT";
    private static final String POS = "pos";
	private static final String CONNECTEE = "CONNECTEE";

    @Override
	protected Station getNewObjetKeolis() {
		return new Station();
    }

    @Override
	protected void remplirObjectKeolis(Station currentObjectKeolis, String baliseName, String contenuOfBalise) {
        if (baliseName.equals(GID)) {
			currentObjectKeolis.id = Integer.parseInt(contenuOfBalise);
        } else if (baliseName.equals(NOM)) {
            currentObjectKeolis.name = contenuOfBalise;
        } else if (baliseName.equals(POS)) {
            currentObjectKeolis.latitude = Double.parseDouble(contenuOfBalise.split(" ")[0]);
            currentObjectKeolis.longitude = Double.parseDouble(contenuOfBalise.split(" ")[1]);
		} else if (baliseName.equals(ETAT)) {
			currentObjectKeolis.isOpen = CONNECTEE.equals(contenuOfBalise);
		} else if (baliseName.equals(NBPLACES)) {
			currentObjectKeolis.freeSlots = Integer.parseInt(contenuOfBalise);
		} else if (baliseName.equals(NBVELOS)) {
			currentObjectKeolis.availableBikes = Integer.parseInt(contenuOfBalise);
        }
    }
}
