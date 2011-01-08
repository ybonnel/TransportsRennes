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
import fr.ybo.transportsrennes.keolis.gtfs.modele.Route;
import fr.ybo.transportsrennes.util.LogYbo;


public class OnClickFavoriGestionnaire implements View.OnClickListener {


	private static final LogYbo LOG_YBO = new LogYbo(OnClickFavoriGestionnaire.class);

	private Route route;
	private String nomArret;
	private String direction;
	private ArretFavori myFavori = new ArretFavori();
	private Activity activity;

	public OnClickFavoriGestionnaire(Route route, String stopId, String nomArret, String direction, Activity activity) {
		this.route = route;
		this.nomArret = nomArret;
		this.direction = direction;
		myFavori.setStopId(stopId);
		myFavori.setRouteId(route.getId());
		this.activity = activity;
	}

	private ProgressDialog myProgressDialog;

	private void chargerRoute() {

		myProgressDialog = ProgressDialog.show(activity, "", "Premier accès à la ligne " + route.getNomCourt() + ", chargement des données...", true);

		new AsyncTask<Void, Void, Void>() {

			boolean erreur = false;

			@Override
			protected Void doInBackground(final Void... pParams) {
				try {
					UpdateDataBase.chargeDetailRoute(route);
				} catch (Exception exception) {
					LOG_YBO.erreur("Erreur lors du chargement du détail de la route", exception);
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
			route = TransportsRennesApplication.getDataBaseHelper().selectSingle(route);
			if (route.getChargee() == null || !route.getChargee()) {
				chargerRoute();
			}
			// Ajout d'un favori.
			myFavori.setRouteNomCourt(route.getNomCourt());
			myFavori.setRouteNomLong(route.getNomLong());
			myFavori.setDirection(direction);
			myFavori.setNomArret(nomArret);
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
