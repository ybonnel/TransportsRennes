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

import android.os.Bundle;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;
import fr.ybo.transportsbordeaux.activity.MenuAccueil;
import fr.ybo.transportsbordeaux.activity.TacheAvecProgressDialog;
import fr.ybo.transportsbordeaux.adapters.TwitterAdapter;
import fr.ybo.transportsbordeaux.tbc.TbcErreurReseaux;
import fr.ybo.transportsbordeaux.twitter.GetTwitters;
import fr.ybo.transportsbordeaux.twitter.MessageTwitter;

public class ListTwitter extends MenuAccueil.ListActivity {

	private final List<MessageTwitter> messages = Collections.synchronizedList(new ArrayList<MessageTwitter>(20));

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.liste);
		setListAdapter(new TwitterAdapter(this, messages));
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		new TacheAvecProgressDialog<Void, Void, Void>(this, getString(R.string.dialogRequeteTwitter)) {
			
			private boolean erreurReseaux = false;

			@Override
			protected Void doInBackground(Void... params) {
				try {
					messages.addAll(GetTwitters.getInstance().getMessages());
				} catch (TbcErreurReseaux tbcErreurReseaux) {
					erreurReseaux = true;
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				if (erreurReseaux) {
					Toast.makeText(ListTwitter.this, R.string.erreurReseau, Toast.LENGTH_LONG).show();
				}
				((BaseAdapter) getListAdapter()).notifyDataSetChanged();
				super.onPostExecute(result);
			}
		}.execute();
	}

}
