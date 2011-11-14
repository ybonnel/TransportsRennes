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

import fr.ybo.transportsbordeaux.database.modele.Parking;

/**
 * Handler pour rÃ©cupÃ©rer les parks relais.
 *
 * @author ybonnel
 */
public class GetParkingHandler extends KeolisHandler<Parking> {
    private static final String GID = "GID";
    private static final String NOM = "NOM";
    private static final String NBLIB = "NBLIB";
    private static final String NBTOT = "NBTOT";
    private static final String POS = "pos";

    @Override
    protected Parking getNewObjetKeolis() {
        return new Parking();
    }

    @Override
    protected void remplirObjectKeolis(Parking currentObjectKeolis, String baliseName, String contenuOfBalise) {
        if (baliseName.equals(GID)) {
            currentObjectKeolis.id = contenuOfBalise;
        } else if (baliseName.equals(NOM)) {
            currentObjectKeolis.name = contenuOfBalise;
        } else if (baliseName.equals(POS)) {
            currentObjectKeolis.latitude = Double.parseDouble(contenuOfBalise.split(" ")[0]);
            currentObjectKeolis.longitude = Double.parseDouble(contenuOfBalise.split(" ")[1]);
        } else if (baliseName.equals(NBLIB)) {
            currentObjectKeolis.carParkAvailable = Integer.parseInt(contenuOfBalise);
        } else if (baliseName.equals(NBTOT)) {
            currentObjectKeolis.carParkCapacity = Integer.parseInt(contenuOfBalise);
        }
    }
}
