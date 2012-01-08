package fr.ybo.transportsrennes.activity.commun;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.TabWidget;

import com.google.android.maps.MapView;
import com.ubikod.capptain.android.sdk.activity.CapptainActivity;
import com.ubikod.capptain.android.sdk.activity.CapptainListActivity;
import com.ubikod.capptain.android.sdk.activity.CapptainMapActivity;
import com.ubikod.capptain.android.sdk.activity.CapptainPreferenceActivity;
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

	public static abstract class BaseTabFragmentActivity extends BaseFragmentActivity {
		private TabHost mTabHost;
		private ViewPager mViewPager;
		private TabsAdapter mTabsAdapter;

		protected void configureTabs() {
			mTabHost = (TabHost) findViewById(android.R.id.tabhost);
			mTabHost.setup();

			mViewPager = (ViewPager) findViewById(R.id.pager);

			mTabsAdapter = new TabsAdapter(this, mTabHost, mViewPager);
		}

		protected void addTab(String id, String title, Class<? extends Fragment> fragment) {
			addTab(id, title, fragment, null);
		}

		protected void addTab(String id, String title, Class<? extends Fragment> fragment, Bundle args) {
			mTabsAdapter.addTab(mTabHost.newTabSpec(id).setIndicator(title), fragment, args);
		}

		protected void setCurrentTab(Bundle savedInstanceState) {
			if (savedInstanceState != null) {
				mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
			} else {
				mTabHost.setCurrentTab(0);
			}
		}

		protected String getCurrentTab() {
			if (mTabHost == null) {
				return null;
			}
			return mTabHost.getCurrentTabTag();
		}

		@Override
		protected void onSaveInstanceState(Bundle outState) {
			super.onSaveInstanceState(outState);
			outState.putString("tab", mTabHost.getCurrentTabTag());
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

	public static abstract class BasePreferenceActivity extends CapptainPreferenceActivity {
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

	/**
	 * This is a helper class that implements the management of tabs and all
	 * details of connecting a ViewPager with associated TabHost. It relies on a
	 * trick. Normally a tab host has a simple API for supplying a View or
	 * Intent that each tab will show. This is not sufficient for switching
	 * between pages. So instead we make the content part of the tab host 0dp
	 * high (it is not shown) and the TabsAdapter supplies its own dummy view to
	 * show as the tab content. It listens to changes in tabs, and takes care of
	 * switch to the correct paged in the ViewPager whenever the selected tab
	 * changes.
	 */
	protected static class TabsAdapter extends FragmentPagerAdapter implements TabHost.OnTabChangeListener,
			ViewPager.OnPageChangeListener {
		private final Context mContext;
		private final TabHost mTabHost;
		private final ViewPager mViewPager;
		private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

		static final class TabInfo {
			private final String tag;
			private final Class<?> clss;
			private final Bundle args;

			TabInfo(String _tag, Class<?> _class, Bundle _args) {
				tag = _tag;
				clss = _class;
				args = _args;
			}
		}

		static class DummyTabFactory implements TabHost.TabContentFactory {
			private final Context mContext;

			public DummyTabFactory(Context context) {
				mContext = context;
			}

			@Override
			public View createTabContent(String tag) {
				View v = new View(mContext);
				v.setMinimumWidth(0);
				v.setMinimumHeight(0);
				return v;
			}
		}

		public TabsAdapter(FragmentActivity activity, TabHost tabHost, ViewPager pager) {
			super(activity.getSupportFragmentManager());
			mContext = activity;
			mTabHost = tabHost;
			mViewPager = pager;
			mTabHost.setOnTabChangedListener(this);
			mViewPager.setAdapter(this);
			mViewPager.setOnPageChangeListener(this);
		}

		public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {
			tabSpec.setContent(new DummyTabFactory(mContext));
			String tag = tabSpec.getTag();

			TabInfo info = new TabInfo(tag, clss, args);
			mTabs.add(info);
			mTabHost.addTab(tabSpec);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return mTabs.size();
		}

		@Override
		public Fragment getItem(int position) {
			TabInfo info = mTabs.get(position);
			return Fragment.instantiate(mContext, info.clss.getName(), info.args);
		}

		@Override
		public void onTabChanged(String tabId) {
			int position = mTabHost.getCurrentTab();
			mViewPager.setCurrentItem(position);
		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		}

		@Override
		public void onPageSelected(int position) {
			// Unfortunately when TabHost changes the current tab, it kindly
			// also takes care of putting focus on it when not in touch mode.
			// The jerk.
			// This hack tries to prevent this from pulling focus out of our
			// ViewPager.
			TabWidget widget = mTabHost.getTabWidget();
			int oldFocusability = widget.getDescendantFocusability();
			widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
			mTabHost.setCurrentTab(position);
			widget.setDescendantFocusability(oldFocusability);
		}

		@Override
		public void onPageScrollStateChanged(int state) {
		}
	}
}
