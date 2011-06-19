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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.os.Bundle;
import android.widget.BaseAdapter;
import android.widget.ListView;
import fr.ybo.transportsrennes.activity.MenuAccueil;
import fr.ybo.transportsrennes.adapters.TwitterAdapter;
import fr.ybo.transportsrennes.twitter.GetTwitters;
import fr.ybo.transportsrennes.twitter.MessageTwitter;
import fr.ybo.transportsrennes.util.ErreurReseau;
import fr.ybo.transportsrennes.util.TacheAvecProgressDialog;

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
			@Override
			protected Void myDoBackground(Void... pParams) throws ErreurReseau {
				messages.addAll(GetTwitters.getInstance().getMessages());
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				((BaseAdapter) getListAdapter()).notifyDataSetChanged();
				super.onPostExecute(result);
			}
		}.execute();
	}

}
