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
package fr.ybo.transportsrennes.activity.commun;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import fr.ybo.transportscommun.donnees.manager.LigneInexistanteException;
import fr.ybo.transportscommun.donnees.manager.gtfs.UpdateDataBase;
import fr.ybo.transportscommun.donnees.modele.ArretFavori;
import fr.ybo.transportscommun.donnees.modele.Ligne;
import fr.ybo.transportscommun.util.NoSpaceLeftException;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.activity.widgets.TransportsWidget11Configure;
import fr.ybo.transportsrennes.activity.widgets.TransportsWidget21Configure;
import fr.ybo.transportsrennes.activity.widgets.TransportsWidgetConfigure;
import fr.ybo.transportsrennes.activity.widgets.TransportsWidgetLowResConfigure;
import fr.ybo.transportsrennes.application.TransportsRennesApplication;

public class OnClickFavoriGestionnaire implements View.OnClickListener {

    private Ligne ligne;
    private final String nomArret;
    private final String direction;
    private final ArretFavori myFavori = new ArretFavori();
    private final Activity activity;

    public OnClickFavoriGestionnaire(Ligne ligne, String arretId, String nomArret, String direction, Activity activity,
                                     int macroDirection) {
        this.ligne = ligne;
        this.nomArret = nomArret;
        this.direction = direction;
        myFavori.arretId = arretId;
        myFavori.ligneId = ligne.id;
        myFavori.macroDirection = macroDirection;
        this.activity = activity;
    }

    private ProgressDialog myProgressDialog;

    private void chargerLigne() {

        myProgressDialog = ProgressDialog.show(activity, "",
                activity.getString(R.string.premierAccesLigne, ligne.nomCourt), true);

        new AsyncTask<Void, Void, Void>() {

            private boolean erreurLigneNonTrouvee = false;
			private boolean erreurNoSpaceLeft = false;

            @Override
            protected void onPreExecute() {
                myProgressDialog.show();
            }

            @Override
            protected Void doInBackground(Void... pParams) {
                try {
					UpdateDataBase.chargeDetailLigne(R.raw.class, ligne, activity.getResources());
				} catch (NoSpaceLeftException e) {
					erreurNoSpaceLeft = true;
                } catch (LigneInexistanteException e) {
                    erreurLigneNonTrouvee = true;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                try {
                    myProgressDialog.dismiss();
                } catch (IllegalArgumentException ignore) {

                }
                if (erreurLigneNonTrouvee) {
                    Toast.makeText(activity, activity.getString(R.string.erreurLigneInconue, ligne.nomCourt),
                            Toast.LENGTH_LONG).show();
                    activity.finish();
				} else if (erreurNoSpaceLeft) {
					Toast.makeText(activity, R.string.erreurNoSpaceLeft, Toast.LENGTH_LONG).show();
					activity.finish();
                }
            }

        }.execute((Void) null);

    }

    public void onClick(View view) {
        ImageView imageView = (ImageView) view;
        if (TransportsRennesApplication.getDataBaseHelper().selectSingle(myFavori) == null) {
            ligne = TransportsRennesApplication.getDataBaseHelper().selectSingle(ligne);
            if (!ligne.isChargee()) {
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
            if (TransportsWidgetConfigure.isNotUsed(activity, myFavori)
                    && TransportsWidget11Configure.isNotUsed(activity, myFavori)
					&& TransportsWidget21Configure.isNotUsed(activity, myFavori)
					&& TransportsWidgetLowResConfigure.isNotUsed(activity, myFavori)) {
                TransportsRennesApplication.getDataBaseHelper().delete(myFavori);
                imageView.setImageResource(android.R.drawable.btn_star_big_off);
                imageView.invalidate();
            } else {
                Toast.makeText(activity, activity.getString(R.string.favoriUsedByWidget), Toast.LENGTH_LONG).show();
            }
        }
    }
}
