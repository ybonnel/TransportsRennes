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

package fr.ybo.transportsrennes.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.TransportsRennesApplication;
import fr.ybo.transportsrennes.TransportsWidgetConfigure;
import fr.ybo.transportsrennes.keolis.gtfs.UpdateDataBase;
import fr.ybo.transportsrennes.keolis.gtfs.modele.ArretFavori;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Ligne;
import fr.ybo.transportsrennes.util.LogYbo;


public class OnClickFavoriGestionnaire implements View.OnClickListener {


	private static final LogYbo LOG_YBO = new LogYbo(OnClickFavoriGestionnaire.class);

	private Ligne ligne;
	private final String nomArret;
	private final String direction;
	private final ArretFavori myFavori = new ArretFavori();
	private final Activity activity;

	public OnClickFavoriGestionnaire(final Ligne ligne, final String arretId, final String nomArret, final String direction, final Activity activity) {
		super();
		this.ligne = ligne;
		this.nomArret = nomArret;
		this.direction = direction;
		myFavori.arretId = arretId;
		myFavori.ligneId = ligne.id;
		this.activity = activity;
	}

	private ProgressDialog myProgressDialog;

	private void chargerLigne() {

		myProgressDialog = ProgressDialog.show(activity, "", activity.getString(R.string.premierAccesLigne, ligne.nomCourt), true);

		new AsyncTask<Void, Void, Void>() {

			boolean erreur;

			@Override
			protected Void doInBackground(final Void... pParams) {
				try {
					UpdateDataBase.chargeDetailLigne(ligne);
				} catch (Exception exception) {
					LOG_YBO.erreur("Erreur lors du chargement du détail de la ligne", exception);
					erreur = true;
				}
				return null;
			}

			@Override
			protected void onPostExecute(final Void result) {
				super.onPostExecute(result);
				myProgressDialog.dismiss();
				if (erreur) {
					Toast.makeText(activity, activity.getString(R.string.erreur_chargementStar), Toast.LENGTH_LONG).show();
					activity.finish();
				}
			}

		}.execute();

	}


	public void onClick(final View view) {
		final ImageView imageView = (ImageView) view;
		if (TransportsRennesApplication.getDataBaseHelper().selectSingle(myFavori) == null) {
			ligne = TransportsRennesApplication.getDataBaseHelper().selectSingle(ligne);
			if (ligne.chargee == null || !ligne.chargee) {
				chargerLigne();
			}
			// Ajout d'un favori.
			myFavori.nomCourt = ligne.nomCourt;
			myFavori.nomLong = ligne.nomLong;
			myFavori.direction = direction;
			myFavori.nomArret = nomArret;
			TransportsRennesApplication.getDataBaseHelper().insert(myFavori);
			imageView.setImageResource(android.R.drawable.btn_star_big_on);
			imageView.invalidate();
		} else {
			// Supression d'un favori.
			if (TransportsWidgetConfigure.isNotUsed(activity, myFavori)) {
				TransportsRennesApplication.getDataBaseHelper().delete(myFavori);
				imageView.setImageResource(android.R.drawable.btn_star_big_off);
				imageView.invalidate();
			} else {
				Toast.makeText(activity, activity.getString(R.string.favoriUsedByWidget), Toast.LENGTH_LONG)
						.show();
			}
		}
	}
}
