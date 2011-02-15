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

package fr.ybo.transportsrennes.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.TransportsRennes;
import fr.ybo.transportsrennes.TransportsRennesApplication;

public class MenuAccueil {

	private static final int GROUP_ID = 99;
	private static final int MENU_ID = 99;

	private MenuAccueil() {
		super();
	}

	private static void addMenu(final Menu menu) {
		menu.add(GROUP_ID, MENU_ID, Menu.NONE, R.string.menu_accueil).setIcon(R.drawable.ic_menu_home);
	}

	@SuppressWarnings({"BooleanMethodNameMustStartWithQuestion"})
	private static boolean onOptionsItemSelected(final Context context, final MenuItem item) {
		if (MENU_ID == item.getItemId()) {
			final Intent intent = new Intent(context, TransportsRennes.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			context.startActivity(intent);
			return true;
		}
		return false;
	}

	public abstract static class ListActivity extends android.app.ListActivity {

		@Override
		protected void onCreate(final Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			TransportsRennesApplication.getTraker().trackPageView("/" + getClass().getSimpleName());
		}

		@Override
		public boolean onCreateOptionsMenu(final Menu menu) {
			super.onCreateOptionsMenu(menu);
			addMenu(menu);
			return true;
		}

		@Override
		public boolean onOptionsItemSelected(final MenuItem item) {
			super.onOptionsItemSelected(item);
			return MenuAccueil.onOptionsItemSelected(this, item);
		}
	}

	public abstract static class Activity extends android.app.Activity {

		@Override
		protected void onCreate(final Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			TransportsRennesApplication.getTraker().trackPageView("/" + getClass().getSimpleName());
		}

		@Override
		public boolean onCreateOptionsMenu(final Menu menu) {
			super.onCreateOptionsMenu(menu);
			addMenu(menu);
			return true;
		}

		@Override
		public boolean onOptionsItemSelected(final MenuItem item) {
			super.onOptionsItemSelected(item);
			return MenuAccueil.onOptionsItemSelected(this, item);
		}
	}
}
