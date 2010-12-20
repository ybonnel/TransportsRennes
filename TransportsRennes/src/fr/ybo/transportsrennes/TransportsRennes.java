package fr.ybo.transportsrennes;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import fr.ybo.transportsrennes.keolis.gtfs.UpdateDataBase;
import fr.ybo.transportsrennes.keolis.gtfs.database.DataBaseHelper;
import fr.ybo.transportsrennes.keolis.gtfs.modele.DernierMiseAJour;
import fr.ybo.transportsrennes.util.LogYbo;

/**
 * Created by IntelliJ IDEA.
 * User: ybonnel
 * Date: 14/12/10
 * Time: 16:26
 * To change this template use File | Settings | File Templates.
 */
public class TransportsRennes extends Activity {

	private ProgressDialog myProgressDialog;

	private static final LogYbo LOG_YBO = new LogYbo(TransportsRennes.class);

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.main);
		verifierUpgrade();
	}

	public void onAlertClick(View view) {
		Intent intent = new Intent(TransportsRennes.this, ListAlerts.class);
		startActivity(intent);
	}

	public void onBusClick(View view) {
		Intent intent = new Intent(TransportsRennes.this, BusRennes.class);
		startActivity(intent);
	}

	public void onBusFavoriClick(View view) {
		Intent intent = new Intent(this, ListFavoris.class);
		startActivity(intent);
	}

	public void onVeloClick(View view) {
		// TODO importer la partie vélo.
	}


	private void upgradeDatabase() {
		myProgressDialog = ProgressDialog.show(TransportsRennes.this, "", "Chargement des données Keolis...", true);

		LOG_YBO.debug("###### Lancement de la mise à jour ");

		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(final Void... pParams) {
				UpdateDataBase.updateIfNecessaryDatabase(
						BusRennesApplication.getDataBaseHelper(),
						TransportsRennes.this.getApplicationContext(), TransportsRennes.this);
				return null;
			}

			@Override
			protected void onPostExecute(final Void pResult) {
				super.onPostExecute(pResult);
				LOG_YBO.debug("###### Fin de la mise à jour ");
				myProgressDialog.dismiss();
			}
		}.execute((Void[]) null);
	}

	private void verifierUpgrade() {
		DataBaseHelper dataBaseHelper = ((BusRennesApplication) getApplication()).getDataBaseHelper();
		DernierMiseAJour dernierMiseAJour = dataBaseHelper.selectSingle(new DernierMiseAJour());
		if (dernierMiseAJour == null) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(
					getString(R.string.premierLancement))
					.setCancelable(false).setPositiveButton("Oui", new DialogInterface.OnClickListener() {
				public void onClick(final DialogInterface dialog, final int id) {
					dialog.dismiss();
					TransportsRennes.this.upgradeDatabase();
				}
			}).setNegativeButton("Non", new DialogInterface.OnClickListener() {
				public void onClick(final DialogInterface dialog, final int id) {
					dialog.cancel();
					TransportsRennes.this.finish();
				}
			});
			final AlertDialog alert = builder.create();
			alert.show();
		}
	}


}
