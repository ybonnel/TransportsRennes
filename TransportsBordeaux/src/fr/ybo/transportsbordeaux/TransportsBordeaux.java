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

package fr.ybo.transportsbordeaux;

import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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

import com.google.ads.AdRequest;
import com.google.ads.AdView;

import fr.ybo.transportsbordeaux.activity.TacheAvecProgressDialog;
import fr.ybo.transportsbordeaux.database.TransportsBordeauxDatabase;
import fr.ybo.transportsbordeaux.donnees.GestionZipKeolis;
import fr.ybo.transportsbordeaux.donnees.UpdateDataBase;
import fr.ybo.transportsbordeaux.modele.DernierMiseAJour;

public class TransportsBordeaux extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		afficheMessage();
		assignerBoutons();
		new TacheAvecProgressDialog<Void, Void, Void>(this, getString(R.string.verificationUpdate)) {
			@Override
			protected Void doInBackground(Void... pParams) {
				verifierUpgrade();
				return null;
			}
		}.execute();

		// Look up the AdView as a resource and load a request.
		((AdView) this.findViewById(R.id.adView)).loadAd(new AdRequest());
	}

	private void assignerBoutons() {

		Button btnBus = (Button) findViewById(R.id.home_btn_bus);
		btnBus.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				onBusClick();
			}
		});
		Button btnBusFavoris = (Button) findViewById(R.id.home_btn_bus_favori);
		btnBusFavoris.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				onBusFavorisClick();
			}
		});
		Button btnVelo = (Button) findViewById(R.id.home_btn_velo);
		btnVelo.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				onVeloClick();
			}
		});
		Button btnVeloFavori = (Button) findViewById(R.id.home_btn_velo_favori);
		btnVeloFavori.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				onVeloFavoriClick();
			}
		});
		Button btnAlert = (Button) findViewById(R.id.home_btn_alert);
		btnAlert.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				onAlertClick();
			}
		});
		Button btnBusGps = (Button) findViewById(R.id.home_btn_bus_gps);
		btnBusGps.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				onBusGpsClick();
			}
		});
	}

	private void afficheMessage() {
		boolean afficheMessage = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
				"TransportsBordeaux_dialog", true);
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
		builder.setTitle(R.string.titleTransportsBordeaux);
		builder.setCancelable(false);
		builder.setNeutralButton(getString(R.string.Terminer), new TransportsBordeaux.TerminerClickListener());
		builder.create().show();
	}

	private static class TerminerClickListener implements DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialogInterface, int i) {
			dialogInterface.cancel();
		}
	}

	private void saveAfficheMessage() {
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
		editor.putBoolean("TransportsBordeaux_dialog", false);
		editor.commit();
	}

	public void onBusClick() {
		Intent intent = new Intent(this, ListeBus.class);
		startActivity(intent);
	}

	public void onBusFavorisClick() {
		Intent intent = new Intent(this, ListFavoris.class);
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

	public void onAlertClick() {
		Intent intent = new Intent(this, TabAlertes.class);
		startActivity(intent);
	}

	public void onBusGpsClick() {
		Intent intent = new Intent(this, ListArretByPosition.class);
		startActivity(intent);
	}

	private void upgradeDatabase() {
		new TacheAvecProgressDialog<Void, Void, Void>(this, getString(R.string.infoChargementGtfs)) {
			@Override
			protected Void doInBackground(Void... params) {
				UpdateDataBase.updateIfNecessaryDatabase(getResources());
				return null;
			}
		}.execute();
	}

	private void verifierUpgrade() {
		TransportsBordeauxDatabase dataBaseHelper = TransportsBordeauxApplication.getDataBaseHelper();
		DernierMiseAJour dernierMiseAJour = dataBaseHelper.selectSingle(new DernierMiseAJour());
		Date dateDernierFichierKeolis = GestionZipKeolis.getLastUpdate(getResources());
		if (dernierMiseAJour == null || dernierMiseAJour.derniereMiseAJour == null
				|| dateDernierFichierKeolis.after(dernierMiseAJour.derniereMiseAJour)) {
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
					AlertDialog alert = builder.create();
					alert.show();
				}
			});
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setContentView(R.layout.main);
		assignerBoutons();
		// Look up the AdView as a resource and load a request.
		((AdView) this.findViewById(R.id.adView)).loadAd(new AdRequest());
	}

	private static final int GROUP_ID = 0;
	private static final int MENU_ID = 1;

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
				showDialog();
				return true;
		}
		return false;
	}
}
