package fr.ybo.transportsrennes;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import fr.ybo.transportsrennes.keolis.gtfs.UpdateDataBase;
import fr.ybo.transportsrennes.keolis.gtfs.database.DataBaseHelper;
import fr.ybo.transportsrennes.keolis.gtfs.files.GestionZipKeolis;
import fr.ybo.transportsrennes.keolis.gtfs.modele.DernierMiseAJour;
import fr.ybo.transportsrennes.util.LogYbo;

import java.util.Date;


public class TransportsRennes extends Activity {

	private ProgressDialog myProgressDialog;

	private static final LogYbo LOG_YBO = new LogYbo(TransportsRennes.class);

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.main);
		Button btnBus = (Button) findViewById(R.id.home_btn_bus);
		Button btnBusFavori = (Button) findViewById(R.id.home_btn_bus_favori);
		Button btnBusGps = (Button) findViewById(R.id.home_btn_bus_gps);
		Button btnAlert = (Button) findViewById(R.id.home_btn_alert);
		Button btnVeloStar = (Button) findViewById(R.id.home_btn_velo);
		Button btnVeloFavori = (Button) findViewById(R.id.home_btn_velo_favori);
		Button btnParking = (Button) findViewById(R.id.home_btn_parking);
		Button btnPointsDeVente = (Button) findViewById(R.id.home_btn_tickets);
		btnBus.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				onBusClick(view);
			}
		});
		btnBusFavori.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				onBusFavoriClick(view);
			}
		});
		btnBusGps.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				onBusGpsClick(view);
			}
		});
		btnAlert.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				onAlertClick(view);
			}
		});
		btnVeloStar.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				onVeloClick(view);
			}
		});
		btnVeloFavori.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				onVeloFavoriClick(view);
			}
		});
		btnParking.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				onParkingClick(view);
			}
		});
		btnPointsDeVente.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				onPointsDeVenteClick(view);
			}
		});
		new AsyncTask<Void, Void, Void>() {

			private boolean erreur = false;

			@Override
			protected void onPreExecute() {
				myProgressDialog = ProgressDialog.show(TransportsRennes.this, "", getString(R.string.verificationUpdate), true);
			}

			@Override
			protected Void doInBackground(final Void... pParams) {

				try {
					verifierUpgrade();
				} catch (Exception exception) {
					LOG_YBO.erreur("Une erreur est survenue dans TransportsRennes.doInBackGround", exception);
					erreur = true;
				}
				return null;
			}

			@Override
			protected void onPostExecute(final Void pResult) {
				super.onPostExecute(pResult);
				myProgressDialog.dismiss();
				if (erreur) {
					Toast.makeText(TransportsRennes.this,
							"Erreur lors de la vérification de mise à jour, si cela se reproduit, envoyer un mail au développeur...",
							Toast.LENGTH_LONG).show();
					if (TransportsRennesApplication.getDataBaseHelper().selectSingle(new DernierMiseAJour()) == null) {
						LOG_YBO.warn(
								"La vérification de mise à jour n'a pas fonctionné alors qu'il n'y a pas encore de données, fermeture de l'application");
						TransportsRennes.this.finish();
					}
				}
			}
		}.execute((Void[]) null);
	}

	@SuppressWarnings("unused")
	public void onAlertClick(View view) {
		Intent intent = new Intent(TransportsRennes.this, ListAlerts.class);
		startActivity(intent);
	}

	@SuppressWarnings("unused")
	public void onBusClick(View view) {
		Intent intent = new Intent(TransportsRennes.this, BusRennes.class);
		startActivity(intent);
	}

	@SuppressWarnings("unused")
	public void onBusFavoriClick(View view) {
		Intent intent = new Intent(this, ListFavoris.class);
		startActivity(intent);
	}

	@SuppressWarnings("unused")
	public void onBusGpsClick(View view) {
		Intent intent = new Intent(this, ListArretByPosition.class);
		startActivity(intent);
	}

	@SuppressWarnings("unused")
	public void onVeloClick(View view) {
		Intent intent = new Intent(this, ListStationsByPosition.class);
		startActivity(intent);
	}

	@SuppressWarnings("unused")
	public void onVeloFavoriClick(View view) {
		Intent intent = new Intent(this, ListStationsFavoris.class);
		startActivity(intent);
	}

	@SuppressWarnings("unused")
	public void onParkingClick(View view) {
		Intent intent = new Intent(this, ListParkRelais.class);
		startActivity(intent);
	}

	@SuppressWarnings("unused")
	public void onPointsDeVenteClick(View view) {
		Intent intent = new Intent(this, ListPointsDeVente.class);
		startActivity(intent);
	}


	private void upgradeDatabase() {
		myProgressDialog = ProgressDialog.show(TransportsRennes.this, "", getString(R.string.infoChargementGtfs), true);

		LOG_YBO.debug("###### Lancement de la mise à jour ");

		new AsyncTask<Void, Void, Void>() {

			private boolean erreur = false;

			@Override
			protected Void doInBackground(final Void... pParams) {
				try {
					UpdateDataBase.updateIfNecessaryDatabase();
				} catch (Exception exception) {
					LOG_YBO.erreur("Une erreur est survenue dans TransportsRennes.doInBackGround", exception);
					erreur = true;
				}
				return null;
			}

			@Override
			protected void onPostExecute(final Void pResult) {
				super.onPostExecute(pResult);
				LOG_YBO.debug("###### Fin de la mise à jour ");
				myProgressDialog.dismiss();
				if (erreur) {
					Toast.makeText(TransportsRennes.this,
							"Une erreur est survenue lors de la récupération des données du STAR, réessayez plus tard, si cela persiste, envoyer un mail au développeur...",
							Toast.LENGTH_LONG).show();
					TransportsRennes.this.finish();
				}
			}
		}.execute((Void[]) null);
	}

	private void verifierUpgrade() {
		DataBaseHelper dataBaseHelper = TransportsRennesApplication.getDataBaseHelper();
		DernierMiseAJour dernierMiseAJour = dataBaseHelper.selectSingle(new DernierMiseAJour());
		Date dateDernierFichierKeolis = GestionZipKeolis.getLastUpdate();
		if (dernierMiseAJour == null || dernierMiseAJour.getDerniereMiseAJour() == null ||
				dateDernierFichierKeolis.after(dernierMiseAJour.getDerniereMiseAJour())) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(getString(dernierMiseAJour == null ? R.string.premierLancement : R.string.majDispo));
			builder.setCancelable(false);
			builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
				public void onClick(final DialogInterface dialog, final int id) {
					dialog.dismiss();
					TransportsRennes.this.upgradeDatabase();
				}
			});
			if (dernierMiseAJour == null) {
				builder.setNegativeButton("Non", new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog, final int id) {
						dialog.cancel();
						TransportsRennes.this.finish();
					}
				});
			} else {
				builder.setNegativeButton("Non", new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog, final int id) {
						dialog.cancel();
					}
				});
			}
			runOnUiThread(new Runnable() {
				public void run() {
					final AlertDialog alert = builder.create();
					alert.show();
				}
			});
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setContentView(R.layout.main);
	}

	private static final int GROUP_ID = 0;
	private static final int MENU_ID = Menu.FIRST;


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuItem item = menu.add(GROUP_ID, MENU_ID, Menu.NONE, R.string.menu_apropos);
		item.setIcon(android.R.drawable.ic_menu_info_details);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
			case MENU_ID:
				TextView message = new TextView(this);
				message.setPadding(8, 8, 8, 8);
				message.setTextSize(18);
				Spanned spanned = Html.fromHtml(getString(R.string.dialogAPropos));
				message.setText(spanned, TextView.BufferType.SPANNABLE);
				message.setMovementMethod(LinkMovementMethod.getInstance());
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setView(message).setCancelable(true);
				builder.create().show();
				return true;
		}
		return false;
	}
}
