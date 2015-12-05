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
package fr.ybo.transportsbordeaux.activity.preferences;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import fr.ybo.transportsbordeaux.R;
import fr.ybo.transportsbordeaux.application.TransportsBordeauxApplication;
import fr.ybo.transportsbordeaux.database.TransportsBordeauxDatabase;
import fr.ybo.transportscommun.AbstractTransportsApplication;
import fr.ybo.transportscommun.activity.commun.UIUtils;
import fr.ybo.transportscommun.activity.preferences.AbstractPreferences;
import fr.ybo.transportscommun.util.TacheAvecProgressDialog;

public class PreferencesBordeaux extends AbstractPreferences {

	private boolean fermetureEnCours;

	@Override
	public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {
		super.onSharedPreferenceChanged(sharedPreferences, key);
		if (fermetureEnCours) {
			return;
		}
		if ("TransportsBordeaux_sdCard".equals(key)) {
			final boolean dbOnSdCard = PreferenceManager.getDefaultSharedPreferences(this)
					.getBoolean(
					"TransportsBordeaux_sdCard", false);
			if (dbOnSdCard && !Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
				Toast.makeText(this, R.string.sdCardInaccessbile, Toast.LENGTH_LONG).show();
				final SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(
						this).edit();
				editor.putBoolean("TransportsBordeaux_sdCard", false);
				editor.commit();
			} else {
				final AlertDialog.Builder builder = new AlertDialog.Builder(this);
				final View alertView = LayoutInflater.from(this).inflate(R.layout.infoapropos, null);
				final TextView textView = (TextView) alertView.findViewById(R.id.textAPropos);
				if (UIUtils.isHoneycomb()) {
					textView.setTextColor(AbstractTransportsApplication.getTextColor(this));
				}
				textView.setText(R.string.changeDbOnSdCard);
				builder.setView(alertView);
				builder.setCancelable(false);
				builder.setNegativeButton(R.string.non, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog, final int which) {
						fermetureEnCours = true;
						final SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(
								PreferencesBordeaux.this).edit();
						editor.putBoolean("TransportsBordeaux_sdCard", !dbOnSdCard);
						editor.commit();
						finish();
					}
				});
				builder.setPositiveButton(R.string.oui, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog, final int which) {
						new TacheAvecProgressDialog<Void, Void, Void>(PreferencesBordeaux.this,
								getString(R.string.suppressionDB), false) {

							@Override
							protected void myDoBackground() {
								deleteDatabase(TransportsBordeauxDatabase.DATABASE_NAME);
							}

							@Override
							protected void onPostExecute(final Void result) {
								super.onPostExecute(result);
								TransportsBordeauxApplication.constructDatabase(getApplicationContext());
								TransportsBordeauxApplication.setBaseNeuve(true);
								finish();
							}
						}.execute((Void) null);
					}
				});
				builder.create().show();
			}
		}
	}

	@Override
	protected int getXmlPreferences() {
		return R.xml.preferences;
	}

	@Override
	protected void setupActionBar() {
		getActivityHelper().setupActionBar(R.menu.default_menu_items, R.menu.holo_default_menu_items);
	}

}
