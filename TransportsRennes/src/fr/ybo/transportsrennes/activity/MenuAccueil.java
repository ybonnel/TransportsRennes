package fr.ybo.transportsrennes.activity;


import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.TransportsRennes;

public class MenuAccueil {

	protected static final int GROUP_ID = 99;
	protected static final int MENU_ID = 99;

	protected static void addMenu(Menu menu) {
		menu.add(GROUP_ID, MENU_ID, Menu.NONE, R.string.menu_accueil).setIcon(R.drawable.ic_menu_home);
	}

	protected static boolean onOptionsItemSelected(Context context, MenuItem item) {
			switch (item.getItemId()) {
				case MENU_ID:
					Intent intent = new Intent(context, TransportsRennes.class);
					intent.putExtra("update", false);
					context.startActivity(intent);
					return true;
			}
			return false;
	}

	public static abstract class ListActivity extends android.app.ListActivity {


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

	public static abstract class Activity extends android.app.Activity {


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
