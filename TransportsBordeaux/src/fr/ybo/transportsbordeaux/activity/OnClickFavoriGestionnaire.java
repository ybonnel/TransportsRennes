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

package fr.ybo.transportsbordeaux.activity;

import android.view.View;
import android.widget.ImageView;
import fr.ybo.transportsbordeaux.TransportsBordeauxApplication;
import fr.ybo.transportsbordeaux.modele.ArretFavori;
import fr.ybo.transportsbordeaux.modele.Ligne;

public class OnClickFavoriGestionnaire implements View.OnClickListener {

	private Ligne ligne;
	private final String nomArret;
	private final String direction;
	private final ArretFavori myFavori = new ArretFavori();

	public OnClickFavoriGestionnaire(Ligne ligne, String arretId, String nomArret, String direction, int macroDirection) {
		this.ligne = ligne;
		this.nomArret = nomArret;
		this.direction = direction;
		myFavori.arretId = arretId;
		myFavori.ligneId = ligne.id;
		myFavori.macroDirection = macroDirection;
	}

	public void onClick(View view) {
		ImageView imageView = (ImageView) view;
		if (TransportsBordeauxApplication.getDataBaseHelper().selectSingle(myFavori) == null) {
			ligne = TransportsBordeauxApplication.getDataBaseHelper().selectSingle(ligne);
			// Ajout d'un favori.
			myFavori.nomCourt = ligne.nomCourt;
			myFavori.nomLong = ligne.nomLong;
			myFavori.direction = direction;
			myFavori.nomArret = nomArret;
			TransportsBordeauxApplication.getDataBaseHelper().insert(myFavori);
			imageView.setImageResource(android.R.drawable.btn_star_big_on);
			imageView.invalidate();
		} else {
			// Supression d'un favori.
			TransportsBordeauxApplication.getDataBaseHelper().delete(myFavori);
			imageView.setImageResource(android.R.drawable.btn_star_big_off);
			imageView.invalidate();
		}
	}
}
