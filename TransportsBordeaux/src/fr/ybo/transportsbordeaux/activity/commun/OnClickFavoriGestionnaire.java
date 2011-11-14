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
package fr.ybo.transportsbordeaux.activity.commun;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import fr.ybo.transportsbordeaux.R;
import fr.ybo.transportsbordeaux.activity.widgets.TransportsWidget11Configure;
import fr.ybo.transportsbordeaux.activity.widgets.TransportsWidget21Configure;
import fr.ybo.transportsbordeaux.application.TransportsBordeauxApplication;
import fr.ybo.transportsbordeaux.database.modele.ArretFavori;
import fr.ybo.transportsbordeaux.database.modele.Ligne;
import fr.ybo.transportsbordeaux.donnees.UpdateDataBase;
import fr.ybo.transportsbordeaux.util.NoSpaceLeftException;

public class OnClickFavoriGestionnaire implements View.OnClickListener {

    private Ligne ligne;
    private final String nomArret;
    private final String direction;
    private final ArretFavori myFavori = new ArretFavori();
    private final Context context;

    public OnClickFavoriGestionnaire(Context context, Ligne ligne, String arretId, String nomArret, String direction) {
        this.ligne = ligne;
        this.nomArret = nomArret;
        this.direction = direction;
        myFavori.arretId = arretId;
        myFavori.ligneId = ligne.id;
        this.context = context;
    }


    private ProgressDialog myProgressDialog;

    private void chargerLigne() {

        myProgressDialog = ProgressDialog.show(context, "",
                context.getString(R.string.premierAccesLigne, ligne.nomCourt), true);

        new AsyncTask<Void, Void, Void>() {

            private boolean erreurNoSpaceLeft = false;

            @Override
            protected void onPreExecute() {
                myProgressDialog.show();
            }

            @Override
            protected Void doInBackground(Void... pParams) {
                try {
                    UpdateDataBase.chargeDetailLigne(ligne, context.getResources());
                } catch (NoSpaceLeftException e) {
                    erreurNoSpaceLeft = true;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                myProgressDialog.dismiss();
                if (erreurNoSpaceLeft) {
                    Toast.makeText(context, R.string.erreurNoSpaceLeft, Toast.LENGTH_LONG).show();
                }
            }

        }.execute((Void) null);

    }

    public void onClick(View view) {
        ImageView imageView = (ImageView) view;
        if (TransportsBordeauxApplication.getDataBaseHelper().selectSingle(myFavori) == null) {
            ligne = TransportsBordeauxApplication.getDataBaseHelper().selectSingle(ligne);
            if (!ligne.isChargee()) {
                chargerLigne();
            }
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
            if (TransportsWidget11Configure.isNotUsed(context, myFavori)
                    && TransportsWidget21Configure.isNotUsed(context, myFavori)) {
                TransportsBordeauxApplication.getDataBaseHelper().delete(myFavori);
                imageView.setImageResource(android.R.drawable.btn_star_big_off);
                imageView.invalidate();
            } else {
                Toast.makeText(context, context.getString(R.string.favoriUsedByWidget), Toast.LENGTH_LONG).show();
            }

            // Supression d'un favori.
        }
    }
}
