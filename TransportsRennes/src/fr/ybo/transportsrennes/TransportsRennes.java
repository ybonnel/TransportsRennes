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

package fr.ybo.transportsrennes;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import fr.ybo.transportsrennes.keolis.gtfs.modele.ArretFavori;
import fr.ybo.transportsrennes.keolis.gtfs.modele.DernierMiseAJour;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Ligne;
import fr.ybo.transportsrennes.util.LogYbo;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;


public class TransportsRennes extends Activity {

	private ProgressDialog myProgressDialog;

	private static final LogYbo LOG_YBO = new LogYbo(TransportsRennes.class);

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TransportsRennesApplication.getTraker().trackPageView("/TransportsRennes");
		setContentView(R.layout.main);
		final Button btnBus = (Button) findViewById(R.id.home_btn_bus);
		final Button btnBusFavori = (Button) findViewById(R.id.home_btn_bus_favori);
		final Button btnBusGps = (Button) findViewById(R.id.home_btn_bus_gps);
		final Button btnAlert = (Button) findViewById(R.id.home_btn_alert);
		final Button btnVeloStar = (Button) findViewById(R.id.home_btn_velo);
		final Button btnVeloFavori = (Button) findViewById(R.id.home_btn_velo_favori);
		final Button btnParking = (Button) findViewById(R.id.home_btn_parking);
		final Button btnPointsDeVente = (Button) findViewById(R.id.home_btn_tickets);
		btnBus.setOnClickListener(new View.OnClickListener() {
			public void onClick(final View view) {
				onBusClick(view);
			}
		});
		btnBusFavori.setOnClickListener(new View.OnClickListener() {
			public void onClick(final View view) {
				onBusFavoriClick(view);
			}
		});
		btnBusGps.setOnClickListener(new View.OnClickListener() {
			public void onClick(final View view) {
				onBusGpsClick();
			}
		});
		btnAlert.setOnClickListener(new View.OnClickListener() {
			public void onClick(final View view) {
				onAlertClick(view);
			}
		});
		btnVeloStar.setOnClickListener(new View.OnClickListener() {
			public void onClick(final View view) {
				onVeloClick(view);
			}
		});
		btnVeloFavori.setOnClickListener(new View.OnClickListener() {
			public void onClick(final View view) {
				onVeloFavoriClick(view);
			}
		});
		btnParking.setOnClickListener(new View.OnClickListener() {
			public void onClick(final View view) {
				onParkingClick(view);
			}
		});
		btnPointsDeVente.setOnClickListener(new View.OnClickListener() {
			public void onClick(final View view) {
				onPointsDeVenteClick(view);
			}
		});
		if (TransportsRennesApplication.isUpdateNecessaire()) {
			new AsyncTask<Void, Void, Void>() {

				private boolean erreur;

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
				protected void onPostExecute(final Void result) {
					super.onPostExecute(result);
					myProgressDialog.dismiss();
					if (erreur) {
						Toast.makeText(TransportsRennes.this, getString(R.string.erreur_verifUpdate), Toast.LENGTH_LONG).show();
						if (TransportsRennesApplication.getDataBaseHelper().selectSingle(new DernierMiseAJour()) == null) {
							LOG_YBO.warn(
									"La vérification de mise à jour n'a pas fonctionné alors qu'il n'y a pas encore de données, fermeture de l'application");
							finish();
						}
					} else {
						TransportsRennesApplication.verifUpdateDone();
					}
				}
			}.execute((Void[]) null);
		}
	}

	@SuppressWarnings({"unused", "WeakerAccess", "UnusedParameters"})
	public void onAlertClick(final View view) {
		final Intent intent = new Intent(this, TabAlertes.class);
		startActivity(intent);
	}

	@SuppressWarnings({"unused", "WeakerAccess", "UnusedParameters"})
	public void onBusClick(final View view) {
		final Intent intent = new Intent(this, BusRennes.class);
		startActivity(intent);
	}

	@SuppressWarnings({"unused", "WeakerAccess", "UnusedParameters"})
	public void onBusFavoriClick(final View view) {
		final Intent intent = new Intent(this, ListFavoris.class);
		startActivity(intent);
	}

	@SuppressWarnings({"unused", "WeakerAccess", "UnusedParameters"})
	public void onBusGpsClick() {
		final Intent intent = new Intent(this, ListArretByPosition.class);
		startActivity(intent);
	}

	@SuppressWarnings({"unused", "WeakerAccess", "UnusedParameters"})
	public void onVeloClick(final View view) {
		final Intent intent = new Intent(this, ListStationsByPosition.class);
		startActivity(intent);
	}

	@SuppressWarnings({"unused", "WeakerAccess", "UnusedParameters"})
	public void onVeloFavoriClick(final View view) {
		final Intent intent = new Intent(this, ListStationsFavoris.class);
		startActivity(intent);
	}

	@SuppressWarnings({"unused", "WeakerAccess", "UnusedParameters"})
	public void onParkingClick(final View view) {
		final Intent intent = new Intent(this, ListParkRelais.class);
		startActivity(intent);
	}

	@SuppressWarnings({"unused", "WeakerAccess", "UnusedParameters"})
	public void onPointsDeVenteClick(final View view) {
		final Intent intent = new Intent(this, ListPointsDeVente.class);
		startActivity(intent);
	}


	private void upgradeDatabase() {
		myProgressDialog = ProgressDialog.show(this, "", getString(R.string.infoChargementGtfs), true);

		new AsyncTask<Void, Void, Void>() {

			private boolean erreur;

			@Override
			protected Void doInBackground(final Void... pParams) {
				try {
					UpdateDataBase.updateIfNecessaryDatabase();
					final Collection<String> ligneIds = new HashSet<String>(10);
					for (final ArretFavori favori : TransportsRennesApplication.getDataBaseHelper().select(new ArretFavori())) {
						if (!ligneIds.contains(favori.ligneId)) {
							ligneIds.add(favori.ligneId);
						}
					}
					final Ligne ligneSelect = new Ligne();
					for (final String ligneId : ligneIds) {
						ligneSelect.id = ligneId;
						Ligne ligne = TransportsRennesApplication.getDataBaseHelper().selectSingle(ligneSelect);
						final String nomLigne = ligne.nomCourt;
						runOnUiThread(new Runnable() {
							public void run() {
								myProgressDialog
										.setMessage(getString(R.string.infoChargementGtfs) + getString(R.string.chargementLigneFavori, nomLigne));
							}
						});
						UpdateDataBase.chargeDetailLigne(ligne);
					}
				} catch (Exception exception) {
					LOG_YBO.erreur("Une erreur est survenue dans TransportsRennes.doInBackGround", exception);
					erreur = true;
				}
				return null;
			}

			@Override
			protected void onPostExecute(final Void result) {
				super.onPostExecute(result);
				myProgressDialog.dismiss();
				if (erreur) {
					Toast.makeText(TransportsRennes.this, getString(R.string.erreur_chargementStar), Toast.LENGTH_LONG).show();
					finish();
				}
			}
		}.execute((Void[]) null);
	}

	private void verifierUpgrade() {
		final DataBaseHelper dataBaseHelper = TransportsRennesApplication.getDataBaseHelper();
		final DernierMiseAJour dernierMiseAJour = dataBaseHelper.selectSingle(new DernierMiseAJour());
		final Date dateDernierFichierKeolis = GestionZipKeolis.getLastUpdate();
		if (dernierMiseAJour == null || dernierMiseAJour.derniereMiseAJour == null ||
				dateDernierFichierKeolis.after(dernierMiseAJour.derniereMiseAJour)) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(getString(dernierMiseAJour == null ? R.string.premierLancement : R.string.majDispo));
			builder.setCancelable(false);
			builder.setPositiveButton(getString(R.string.oui), new Dialog.OnClickListener() {
				public void onClick(final DialogInterface dialog, final int id) {
					dialog.dismiss();
					upgradeDatabase();
				}
			});
			if (dernierMiseAJour == null) {
				builder.setNegativeButton(getString(R.string.non), new Dialog.OnClickListener() {
					public void onClick(final DialogInterface dialog, final int id) {
						dialog.cancel();
						finish();
					}
				});
			} else {
				builder.setNegativeButton(getString(R.string.non), new Dialog.OnClickListener() {
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
	public void onConfigurationChanged(final Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setContentView(R.layout.main);
	}

	private static final int GROUP_ID = 0;
	private static final int MENU_ID = 1;
	private static final int MENU_MAP_ID = 2;
	private static final int MENU_LOAD_LINES = 3;


	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		super.onCreateOptionsMenu(menu);
		final MenuItem item = menu.add(GROUP_ID, MENU_ID, Menu.NONE, R.string.menu_apropos);
		item.setIcon(android.R.drawable.ic_menu_info_details);
		final MenuItem itemMap = menu.add(GROUP_ID, MENU_MAP_ID, Menu.NONE, R.string.menu_carte);
		itemMap.setIcon(android.R.drawable.ic_menu_mapmode);
		final MenuItem itemLoadLines = menu.add(GROUP_ID, MENU_LOAD_LINES, Menu.NONE, R.string.menu_loadLines);
		itemLoadLines.setIcon(android.R.drawable.ic_menu_save);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
			case MENU_ID:
				final TextView message = new TextView(this);
				message.setPadding(8, 8, 8, 8);
				message.setTextSize(18);
				final Spanned spanned = Html.fromHtml(getString(R.string.dialogAPropos));
				message.setText(spanned, TextView.BufferType.SPANNABLE);
				message.setMovementMethod(LinkMovementMethod.getInstance());
				final AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setView(message).setCancelable(true);
				builder.create().show();
				return true;
			case MENU_MAP_ID:
				final Intent intent = new Intent(this, AllOnMap.class);
				startActivity(intent);
				return true;
			case MENU_LOAD_LINES:
				final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
				alertBuilder.setMessage(getString(R.string.loadAllLineAlert));
				alertBuilder.setCancelable(false);
				alertBuilder.setPositiveButton(getString(R.string.oui), new Dialog.OnClickListener() {
					public void onClick(final DialogInterface dialog, final int id) {
						dialog.dismiss();
						loadAllLines();
					}
				});
				alertBuilder.setNegativeButton(getString(R.string.non), new Dialog.OnClickListener() {
					public void onClick(final DialogInterface dialog, final int id) {
						dialog.cancel();
					}
				});
				alertBuilder.show();
				return true;
		}
		return false;
	}

	private void loadAllLines() {
		myProgressDialog = ProgressDialog.show(this, "", getString(R.string.infoChargementGtfs), true);

		new AsyncTask<Void, Void, Void>() {

			private boolean erreur;

			@Override
			protected Void doInBackground(final Void... pParams) {
				try {
					for (final Ligne ligne : TransportsRennesApplication.getDataBaseHelper().select(new Ligne())) {
						if (ligne.chargee == null || !ligne.chargee) {
							final String nomLigne = ligne.nomCourt;
							runOnUiThread(new Runnable() {
								public void run() {
									myProgressDialog.setMessage(
											getString(R.string.infoChargementGtfs) + '\n' + getString(R.string.premierAccesLigne, nomLigne));
								}
							});
							UpdateDataBase.chargeDetailLigne(ligne);
						}
					}
				} catch (Exception exception) {
					LOG_YBO.erreur("Une erreur est survenue dans TransportsRennes.loadAllLines", exception);
					erreur = true;
				}
				return null;
			}

			@Override
			protected void onPostExecute(final Void result) {
				super.onPostExecute(result);
				myProgressDialog.dismiss();
				if (erreur) {
					Toast.makeText(TransportsRennes.this, getString(R.string.erreur_chargementStar), Toast.LENGTH_LONG).show();
				}
			}
		}.execute((Void[]) null);
	}
}
