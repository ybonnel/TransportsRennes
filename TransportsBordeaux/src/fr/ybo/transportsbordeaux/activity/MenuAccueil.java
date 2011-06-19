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
package fr.ybo.transportsbordeaux.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import fr.ybo.transportsbordeaux.R;
import fr.ybo.transportsbordeaux.TransportsBordeaux;

public class MenuAccueil {

	private static final int GROUP_ID = 99;
	private static final int MENU_ID = 99;

	private MenuAccueil() {
	}

	private static void addMenu(Menu menu) {
		menu.add(GROUP_ID, MENU_ID, Menu.NONE, R.string.menu_accueil).setIcon(R.drawable.ic_menu_home);
	}

	private static boolean onOptionsItemSelected(Context context, MenuItem item) {
		if (item.getItemId() == MENU_ID) {
			Intent intent = new Intent(context, TransportsBordeaux.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			context.startActivity(intent);
			return true;
		}
		return false;
	}

	public abstract static class ListActivity extends android.app.ListActivity {

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
		}

		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			super.onCreateOptionsMenu(menu);
			addMenu(menu);
			return true;
		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			super.onOptionsItemSelected(item);
			return MenuAccueil.onOptionsItemSelected(this, item);
		}
	}
}
