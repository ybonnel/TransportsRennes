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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;
import fr.ybo.transportsbordeaux.activity.MenuAccueil;
import fr.ybo.transportsbordeaux.adapters.TwitterAdapter;
import fr.ybo.transportsbordeaux.twitter.GetTwitters;
import fr.ybo.transportsbordeaux.twitter.MessageTwitter;
import fr.ybo.transportsbordeaux.util.LogYbo;

public class ListTwitter extends MenuAccueil.ListActivity {

	private static final LogYbo LOG_YBO = new LogYbo(ListTwitter.class);

	private ProgressDialog myProgressDialog;

	private final List<MessageTwitter> messages = Collections.synchronizedList(new ArrayList<MessageTwitter>(20));

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.liste);
		setListAdapter(new TwitterAdapter(this, messages));
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		new AsyncTask<Void, Void, Void>() {

			private boolean erreur;

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				myProgressDialog = ProgressDialog.show(ListTwitter.this, "", getString(R.string.dialogRequeteTwitter), true);
			}

			@Override
			protected Void doInBackground(Void... pParams) {
				try {
					messages.addAll(GetTwitters.getInstance().getMessages());
				} catch (Exception exception) {
					erreur = true;
					LOG_YBO.erreur("Erreur lors de l'interrogation de twitter", exception);
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				((BaseAdapter) getListAdapter()).notifyDataSetChanged();
				myProgressDialog.dismiss();
				if (erreur) {
					Toast.makeText(ListTwitter.this, getString(R.string.erreur_twitter), Toast.LENGTH_LONG).show();
					finish();
				}
				super.onPostExecute(result);
			}
		}.execute();
	}

}
