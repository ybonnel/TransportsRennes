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

package fr.ybo.itineraires.modele;

public abstract class PortionTrajetPieton extends PortionTrajet {

	protected double distance;
	// Vitesse d'un piéton en m/h
	private static final double VITESSE_PIETON = 4000;

	protected Integer tempsTrajet;

	protected int calculTempsTrajetAproximatif() {
		if (tempsTrajet == null) {
			tempsTrajet = (int)Math.round(distance / VITESSE_PIETON * 60);
		}
		return tempsTrajet;
	}

	@Override
	public int calculHeureArrivee(final int heureDepart) {
		return heureDepart + calculTempsTrajetAproximatif();
	}

	@Override
	public String toString() {
		return "A pied ("
				+ tempsTrajet + " minutes)";
	}

    protected void remplirXml(final fr.ybo.itineraires.schema.PortionTrajetPieton xml) {
        xml.setTempsTrajet(tempsTrajet);
    }

}
