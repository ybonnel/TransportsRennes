package fr.ybo.transportsrennes.activity.commun;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.google.android.maps.MapView;
import com.ubikod.capptain.android.sdk.activity.CapptainActivity;
import com.ubikod.capptain.android.sdk.activity.CapptainListActivity;
import com.ubikod.capptain.android.sdk.activity.CapptainMapActivity;
import com.ubikod.capptain.android.sdk.activity.CapptainTabActivity;

import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.activity.TransportsRennes;
import fr.ybo.transportsrennes.activity.actionbar.ActivityHelper;
import fr.ybo.transportsrennes.activity.actionbar.Refreshable;
import fr.ybo.transportsrennes.activity.bus.TabFavoris;
import fr.ybo.transportsrennes.activity.preferences.PreferencesRennes;
import fr.ybo.transportsrennes.activity.velos.ListStationsFavoris;
import fr.ybo.transportsrennes.application.TransportsRennesApplication;
import fr.ybo.transportsrennes.util.CapptainFragmentActivity;

public class BaseActivity {

	private static boolean onOptionsItemSelected(MenuItem item, ActivityHelper helper, Activity activity) {
		switch (item.getItemId()) {
			case android.R.id.home:
				// app icon in action bar clicked; go home
				helper.goHome();
				return true;
			case R.id.menu_bus_favoris:
				activity.startActivity(new Intent(activity, TabFavoris.class));
				return true;
			case R.id.menu_velo_favoris:
				activity.startActivity(new Intent(activity, ListStationsFavoris.class));
				return true;
			case R.id.menu_prefs:
				activity.startActivity(new Intent(activity, PreferencesRennes.class));
				return true;
			case R.id.menu_refresh:
				if (activity instanceof Refreshable) {
					((Refreshable) activity).refresh();
				}
				return true;
			default:
				return false;
		}
	}

	public static abstract class BaseFragmentActivity extends CapptainFragmentActivity {
		final ActivityHelper mActivityHelper = ActivityHelper.createInstance(this);

		public ActivityHelper getActivityHelper() {
			return mActivityHelper;
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			TransportsRennesApplication.majTheme(this);
			super.onCreate(savedInstanceState);
		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			return BaseActivity.onOptionsItemSelected(item, mActivityHelper, this) || super.onOptionsItemSelected(item);
		}

		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			return mActivityHelper.onCreateOptionsMenu(menu);
		}

		@Override
		protected void onPostCreate(Bundle savedInstanceState) {
			super.onPostCreate(savedInstanceState);
			if (this instanceof TransportsRennes) {
				getActivityHelper().setupHomeActivity();
			} else {
				getActivityHelper().setupSubActivity();
			}
		}

	}

	public static abstract class BaseTabActivity extends CapptainTabActivity {
		final ActivityHelper mActivityHelper = ActivityHelper.createInstance(this);

		public ActivityHelper getActivityHelper() {
			return mActivityHelper;
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			TransportsRennesApplication.majTheme(this);
			super.onCreate(savedInstanceState);
		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			return BaseActivity.onOptionsItemSelected(item, mActivityHelper, this) || super.onOptionsItemSelected(item);
		}

		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			return mActivityHelper.onCreateOptionsMenu(menu);
		}

		@Override
		protected void onPostCreate(Bundle savedInstanceState) {
			super.onPostCreate(savedInstanceState);
			getActivityHelper().setupSubActivity();
		}

	}

	public static abstract class BaseSimpleActivity extends CapptainActivity {
		final ActivityHelper mActivityHelper = ActivityHelper.createInstance(this);

		public ActivityHelper getActivityHelper() {
			return mActivityHelper;
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			TransportsRennesApplication.majTheme(this);
			super.onCreate(savedInstanceState);
		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			return BaseActivity.onOptionsItemSelected(item, mActivityHelper, this) || super.onOptionsItemSelected(item);
		}

		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			return mActivityHelper.onCreateOptionsMenu(menu);
		}

		@Override
		protected void onPostCreate(Bundle savedInstanceState) {
			super.onPostCreate(savedInstanceState);
			getActivityHelper().setupSubActivity();
		}
	}

	public static abstract class BaseListActivity extends CapptainListActivity {
		final ActivityHelper mActivityHelper = ActivityHelper.createInstance(this);

		public ActivityHelper getActivityHelper() {
			return mActivityHelper;
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			TransportsRennesApplication.majTheme(this);
			super.onCreate(savedInstanceState);
		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			return BaseActivity.onOptionsItemSelected(item, mActivityHelper, this) || super.onOptionsItemSelected(item);
		}

		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			return mActivityHelper.onCreateOptionsMenu(menu);
		}

		@Override
		protected void onPostCreate(Bundle savedInstanceState) {
			super.onPostCreate(savedInstanceState);
			getActivityHelper().setupSubActivity();
		}
	}

	public static abstract class BaseMapActivity extends CapptainMapActivity {
		final ActivityHelper mActivityHelper = ActivityHelper.createInstance(this);

		public ActivityHelper getActivityHelper() {
			return mActivityHelper;
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			TransportsRennesApplication.majTheme(this);
			super.onCreate(savedInstanceState);
		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			return BaseActivity.onOptionsItemSelected(item, mActivityHelper, this) || super.onOptionsItemSelected(item);
		}

		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			return mActivityHelper.onCreateOptionsMenu(menu);
		}

		@Override
		protected void onPostCreate(Bundle savedInstanceState) {
			super.onPostCreate(savedInstanceState);
			getActivityHelper().setupSubActivity();
		}

		private MapView mapView;

		private boolean satelite = false;

		protected void gestionButtonLayout() {
			mapView = (MapView) findViewById(R.id.mapview);
			mapView.setSatellite(false);
			ImageButton layoutButton = (ImageButton) findViewById(R.id.layers_button);
			layoutButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					AlertDialog.Builder builder = new AlertDialog.Builder(BaseMapActivity.this);
					String[] items = { "Satellite" };
					boolean[] checkeds = { satelite };
					builder.setMultiChoiceItems(items, checkeds, new OnMultiChoiceClickListener() {
						public void onClick(DialogInterface dialog, int which, boolean isChecked) {
							satelite = !satelite;
							mapView.setSatellite(satelite);
						}
					});
					AlertDialog alert = builder.create();
					alert.show();
				}
			});
		}
	}
}
