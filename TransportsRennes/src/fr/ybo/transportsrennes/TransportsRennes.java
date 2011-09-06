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
 * 
 * Contributors:
 *     ybonnel - initial API and implementation
 */
package fr.ybo.transportsrennes;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import fr.ybo.transportsrennes.database.TransportsRennesDatabase;
import fr.ybo.transportsrennes.keolis.LigneInexistanteException;
import fr.ybo.transportsrennes.keolis.gtfs.UpdateDataBase;
import fr.ybo.transportsrennes.keolis.gtfs.files.GestionZipKeolis;
import fr.ybo.transportsrennes.keolis.gtfs.modele.ArretFavori;
import fr.ybo.transportsrennes.keolis.gtfs.modele.DernierMiseAJour;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Ligne;


public class TransportsRennes extends Activity {

	private ProgressDialog myProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TransportsRennesApplication.getTraker().trackPageView("/TransportsRennes");
		setContentView(R.layout.main);
		afficheMessage();
		assignerBoutons();
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected void onPreExecute() {
				myProgressDialog = ProgressDialog.show(TransportsRennes.this, "",
						getString(R.string.verificationUpdate), true);
			}

			@Override
			protected Void doInBackground(Void... pParams) {
				verifierUpgrade();
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				try {
					myProgressDialog.dismiss();
				} catch (IllegalArgumentException ignore) {

				}
			}
		}.execute((Void[]) null);
	}

	private void assignerBoutons() {

		Button btnBus = (Button) findViewById(R.id.home_btn_bus);
		Button btnBusFavori = (Button) findViewById(R.id.home_btn_bus_favori);
		Button btnBusGps = (Button) findViewById(R.id.home_btn_bus_gps);
		Button btnAlert = (Button) findViewById(R.id.home_btn_alert);
		Button btnVeloStar = (Button) findViewById(R.id.home_btn_velo);
		Button btnVeloFavori = (Button) findViewById(R.id.home_btn_velo_favori);
		Button btnParking = (Button) findViewById(R.id.home_btn_parking);
		Button btnItineraires = (Button) findViewById(R.id.home_btn_itineraires);
		btnBus.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				onBusClick();
			}
		});
		btnBusFavori.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				onBusFavoriClick();
			}
		});
		btnBusGps.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				onBusGpsClick();
			}
		});
		btnAlert.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				onAlertClick();
			}
		});
		btnVeloStar.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				onVeloClick();
			}
		});
		btnVeloFavori.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				onVeloFavoriClick();
			}
		});
		btnParking.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				onParkingClick();
			}
		});
		btnItineraires.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				onItinerairesClick();
			}
		});
	}

	private void afficheMessage() {
		boolean afficheMessage = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("TransportsRennes141_dialog", true);
		if (afficheMessage) {
			showDialog();
			saveAfficheMessage();
		}

	}

	private void showDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View view = LayoutInflater.from(this).inflate(R.layout.infoapropos, null);
		TextView textView = (TextView) view.findViewById(R.id.textAPropos);
		Spanned spanned = Html.fromHtml(getString(R.string.dialogAPropos));
		textView.setText(spanned, TextView.BufferType.SPANNABLE);
		textView.setMovementMethod(LinkMovementMethod.getInstance());
		builder.setView(view);
		builder.setTitle(R.string.titleTransportsRennes);
		builder.setCancelable(false);
		builder.setNeutralButton(getString(R.string.Terminer), new TransportsRennes.TerminerClickListener());
		builder.create().show();
	}

	private static class TerminerClickListener implements DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialogInterface, int i) {
			dialogInterface.cancel();
		}
	}


	private void saveAfficheMessage() {
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
		editor.putBoolean("TransportsRennes141_dialog", false);
		editor.commit();
	}

	public void onAlertClick() {
		Intent intent = new Intent(this, TabAlertes.class);
		startActivity(intent);
	}

	public void onBusClick() {
		Intent intent = new Intent(this, BusRennes.class);
		startActivity(intent);
	}

	public void onBusFavoriClick() {
		Intent intent = new Intent(this, TabFavoris.class);
		startActivity(intent);
	}

	public void onBusGpsClick() {
		Intent intent = new Intent(this, ListArretByPosition.class);
		startActivity(intent);
	}

	public void onVeloClick() {
		Intent intent = new Intent(this, ListStationsByPosition.class);
		startActivity(intent);
	}

	public void onVeloFavoriClick() {
		Intent intent = new Intent(this, ListStationsFavoris.class);
		startActivity(intent);
	}

	public void onParkingClick() {
		Intent intent = new Intent(this, ListParkRelais.class);
		startActivity(intent);
	}

	public void onItinerairesClick() {
		Intent intent = new Intent(this, ItineraireRequete.class);
		startActivity(intent);
	}


	private void upgradeDatabase() {
		myProgressDialog = ProgressDialog.show(this, "", getString(R.string.infoChargementGtfs), true);

		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... pParams) {
				UpdateDataBase.updateIfNecessaryDatabase(getResources());
				Collection<String> ligneIds = new HashSet<String>(10);
				for (ArretFavori favori : TransportsRennesApplication.getDataBaseHelper().select(new ArretFavori())) {
					if (!ligneIds.contains(favori.ligneId)) {
						ligneIds.add(favori.ligneId);
					}
				}
				Ligne ligneSelect = new Ligne();
				for (String ligneId : ligneIds) {
					ligneSelect.id = ligneId;
					Ligne ligne = TransportsRennesApplication.getDataBaseHelper().selectSingle(ligneSelect);
					final String nomLigne = ligne.nomCourt;
					runOnUiThread(new Runnable() {
						public void run() {
							myProgressDialog.setMessage(getString(R.string.infoChargementGtfs)
									+ getString(R.string.chargementLigneFavori, nomLigne));
						}
					});
					try {
						UpdateDataBase.chargeDetailLigne(ligne, getResources());
					} catch (LigneInexistanteException ignore) {
					}
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				myProgressDialog.dismiss();
			}
		}.execute((Void[]) null);
	}

	private void verifierUpgrade() {
		TransportsRennesDatabase dataBaseHelper = TransportsRennesApplication.getDataBaseHelper();
		DernierMiseAJour dernierMiseAJour = dataBaseHelper.selectSingle(new DernierMiseAJour());
		Date dateDernierFichierKeolis = GestionZipKeolis.getLastUpdate(getResources());
		if (dernierMiseAJour == null || dernierMiseAJour.derniereMiseAJour == null ||
				dateDernierFichierKeolis.after(dernierMiseAJour.derniereMiseAJour)) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(getString(dernierMiseAJour == null ? R.string.premierLancement : R.string.majDispo));
			builder.setCancelable(false);
			builder.setPositiveButton(getString(R.string.oui), new Dialog.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
					upgradeDatabase();
				}
			});
			if (dernierMiseAJour == null) {
				builder.setNegativeButton(getString(R.string.non), new Dialog.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						finish();
					}
				});
			} else {
				builder.setNegativeButton(getString(R.string.non), new Dialog.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
			}
			runOnUiThread(new Runnable() {
				public void run() {
					if (!isFinishing()) {
						AlertDialog alert = builder.create();
						alert.show();
					}
				}
			});
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setContentView(R.layout.main);
		assignerBoutons();
	}

	private static final int GROUP_ID = 0;
	private static final int MENU_ID = 1;
	private static final int MENU_MAP_ID = 2;
	private static final int MENU_TICKETS = 3;
	private static final int MENU_LOAD_LINES = 4;
	private static final int MENU_SHARE = 5;


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuItem item = menu.add(GROUP_ID, MENU_ID, Menu.NONE, R.string.menu_apropos);
		item.setIcon(android.R.drawable.ic_menu_info_details);
		MenuItem itemMap = menu.add(GROUP_ID, MENU_MAP_ID, Menu.NONE, R.string.menu_carte);
		itemMap.setIcon(android.R.drawable.ic_menu_mapmode);
		MenuItem itemPointDeVentes = menu.add(GROUP_ID, MENU_TICKETS, Menu.NONE, R.string.menu_tickets);
		itemPointDeVentes.setIcon(R.drawable.ic_menu_tickets);
		MenuItem itemLoadLines = menu.add(GROUP_ID, MENU_LOAD_LINES, Menu.NONE, R.string.menu_loadLines);
		itemLoadLines.setIcon(android.R.drawable.ic_menu_save);
		MenuItem itemShare = menu.add(GROUP_ID, MENU_SHARE, Menu.NONE, R.string.menu_share);
		itemShare.setIcon(android.R.drawable.ic_menu_share);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
			case MENU_ID:
				showDialog();
				return true;
			case MENU_MAP_ID:
				Intent intentMap = new Intent(this, AllOnMap.class);
				startActivity(intentMap);
				return true;
			case MENU_TICKETS:
				Intent intentTickets = new Intent(this, ListPointsDeVente.class);
				startActivity(intentTickets);
				return true;
			case MENU_LOAD_LINES:
				AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
				alertBuilder.setMessage(getString(R.string.loadAllLineAlert));
				alertBuilder.setCancelable(false);
				alertBuilder.setPositiveButton(getString(R.string.oui), new Dialog.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
						loadAllLines();
					}
				});
				alertBuilder.setNegativeButton(getString(R.string.non), new Dialog.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
				alertBuilder.show();
				return true;
			case MENU_SHARE:
				Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
				shareIntent.setType("text/plain");
				shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.app_name));
				shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.shareText));
				startActivity(Intent.createChooser(shareIntent, getString(R.string.app_name)));
				return true;
		}
		return false;
	}

	private void loadAllLines() {
		myProgressDialog = ProgressDialog.show(this, "", getString(R.string.infoChargementGtfs), true);

		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... pParams) {
				for (Ligne ligne : TransportsRennesApplication.getDataBaseHelper().select(new Ligne())) {
					if (!ligne.isChargee()) {
						final String nomLigne = ligne.nomCourt;
						runOnUiThread(new Runnable() {
							public void run() {
								myProgressDialog.setMessage(getString(R.string.infoChargementGtfs) + '\n'
										+ getString(R.string.premierAccesLigne, nomLigne));
							}
						});
						try {
							UpdateDataBase.chargeDetailLigne(ligne, getResources());
						} catch (LigneInexistanteException ignore) {
						}
					}
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				myProgressDialog.dismiss();
			}
		}.execute((Void[]) null);
	}
}
