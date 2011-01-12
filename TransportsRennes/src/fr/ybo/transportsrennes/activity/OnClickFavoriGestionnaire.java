package fr.ybo.transportsrennes.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import fr.ybo.transportsrennes.TransportsRennesApplication;
import fr.ybo.transportsrennes.keolis.gtfs.UpdateDataBase;
import fr.ybo.transportsrennes.keolis.gtfs.modele.ArretFavori;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Ligne;
import fr.ybo.transportsrennes.util.LogYbo;


public class OnClickFavoriGestionnaire implements View.OnClickListener {


	private static final LogYbo LOG_YBO = new LogYbo(OnClickFavoriGestionnaire.class);

	private Ligne ligne;
	private String nomArret;
	private String direction;
	private ArretFavori myFavori = new ArretFavori();
	private Activity activity;

	public OnClickFavoriGestionnaire(Ligne ligne, String arretId, String nomArret, String direction, Activity activity) {
		this.ligne = ligne;
		this.nomArret = nomArret;
		this.direction = direction;
		myFavori.arretId = arretId;
		myFavori.ligneId = ligne.id;
		this.activity = activity;
	}

	private ProgressDialog myProgressDialog;

	private void chargerLigne() {

		myProgressDialog = ProgressDialog.show(activity, "", "Premier accès à la ligne " + ligne.nomCourt + ", chargement des données...", true);

		new AsyncTask<Void, Void, Void>() {

			boolean erreur = false;

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
					Toast.makeText(activity,
							"Une erreur est survenue lors de la récupération des données du STAR, réessayez plus tard, si cela persiste, envoyer un mail au développeur...",
							Toast.LENGTH_LONG).show();
					activity.finish();
				}
			}

		}.execute();

	}


	public void onClick(View view) {
		ImageView imageView = (ImageView) view;
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
			TransportsRennesApplication.getDataBaseHelper().delete(myFavori);
			imageView.setImageResource(android.R.drawable.btn_star_big_off);
			imageView.invalidate();
		}
	}
}
