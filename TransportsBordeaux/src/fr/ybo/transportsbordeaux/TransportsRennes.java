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
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.widget.Toast;
import fr.ybo.transportsbordeaux.database.DataBaseHelper;
import fr.ybo.transportsbordeaux.donnees.GestionZipTbc;
import fr.ybo.transportsbordeaux.donnees.UpdateDataBase;
import fr.ybo.transportsbordeaux.modele.DernierMiseAJour;
import fr.ybo.transportsbordeaux.util.LogYbo;


public class TransportsRennes extends Activity {

	private ProgressDialog myProgressDialog;

	private static final LogYbo LOG_YBO = new LogYbo(TransportsRennes.class);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		afficheMessage();
		assignerBoutons();
		new AsyncTask<Void, Void, Void>() {

			private boolean erreur;

			@Override
			protected void onPreExecute() {
				myProgressDialog = ProgressDialog.show(TransportsRennes.this, "",
						getString(R.string.verificationUpdate), true);
			}

			@Override
			protected Void doInBackground(Void... pParams) {

				try {
					verifierUpgrade();
				} catch (Exception exception) {
					LOG_YBO.erreur("Une erreur est survenue dans TransportsRennes.doInBackGround", exception);
					erreur = true;
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				myProgressDialog.dismiss();
				if (erreur) {
					Toast.makeText(TransportsRennes.this, getString(R.string.erreur_verifUpdate), Toast.LENGTH_LONG)
							.show();
					if (TransportsBordeauxApplication.getDataBaseHelper().selectSingle(new DernierMiseAJour()) == null) {
						LOG_YBO.warn("La vérification de mise à jour n'a pas fonctionné alors qu'il n'y a pas encore de données, fermeture de l'application");
						finish();
					}
				}
			}
		}.execute((Void[]) null);
	}

	private void assignerBoutons() {

		Button btnBus = (Button) findViewById(R.id.home_btn_bus);
		btnBus.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				onBusClick();
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
		editor.putBoolean("TransportsBordeaux_dialog", false);
		editor.commit();
	}

	public void onBusClick() {
		/*
		 * Intent intent = new Intent(this, BusBordeaux.class);
		 * startActivity(intent);
		 */
	}


	private void upgradeDatabase() {
		myProgressDialog = ProgressDialog.show(this, "", getString(R.string.infoChargementGtfs), true);

		new AsyncTask<Void, Void, Void>() {

			private boolean erreur;

			@Override
			protected Void doInBackground(Void... pParams) {
				try {
					UpdateDataBase.updateIfNecessaryDatabase(getResources());
				} catch (Exception exception) {
					LOG_YBO.erreur("Une erreur est survenue dans TransportsBordeaux.doInBackGround", exception);
					erreur = true;
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				myProgressDialog.dismiss();
				if (erreur) {
					Toast.makeText(TransportsRennes.this, getString(R.string.erreur_chargementTbc), Toast.LENGTH_LONG)
							.show();
					finish();
				}
			}
		}.execute((Void[]) null);
	}

	private void verifierUpgrade() {
		DataBaseHelper dataBaseHelper = TransportsBordeauxApplication.getDataBaseHelper();
		DernierMiseAJour dernierMiseAJour = dataBaseHelper.selectSingle(new DernierMiseAJour());
		Date dateDernierFichierKeolis = GestionZipTbc.getLastUpdate(getResources());
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
