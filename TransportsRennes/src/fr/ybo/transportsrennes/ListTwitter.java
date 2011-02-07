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

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;
import fr.ybo.transportsrennes.activity.MenuAccueil;
import fr.ybo.transportsrennes.adapters.TwitterAdapter;
import fr.ybo.transportsrennes.twitter.GetTwitters;
import fr.ybo.transportsrennes.twitter.MessageTwitter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListTwitter extends MenuAccueil.ListActivity {

	private ProgressDialog myProgressDialog;

	private final List<MessageTwitter> messages = Collections.synchronizedList(new ArrayList<MessageTwitter>());

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.liste);
		setListAdapter(new TwitterAdapter(this, messages));
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		new AsyncTask<Void, Void, Void>() {

			private boolean erreur = false;
			private boolean rateLimit = false;
			private String dateReset = null;

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				myProgressDialog = ProgressDialog.show(ListTwitter.this, "", getString(R.string.dialogRequeteTwitter), true);
			}

			@Override
			protected Void doInBackground(final Void... pParams) {
				messages.addAll(GetTwitters.getInstance().getMessages());
				return null;
			}

			@Override
			@SuppressWarnings("unchecked")
			protected void onPostExecute(final Void pResult) {
				((TwitterAdapter) getListAdapter()).notifyDataSetChanged();
				myProgressDialog.dismiss();
				if (rateLimit) {
					Toast.makeText(ListTwitter.this, getString(R.string.erreur_quotaTwitter, dateReset), Toast.LENGTH_LONG).show();
				} else if (erreur) {
					Toast.makeText(ListTwitter.this, getString(R.string.erreur_twitter), Toast.LENGTH_LONG).show();
					ListTwitter.this.finish();
				}
				super.onPostExecute(pResult);
			}
		}.execute();
	}

}
