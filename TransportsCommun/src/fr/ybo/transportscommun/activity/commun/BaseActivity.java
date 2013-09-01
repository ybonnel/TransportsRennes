package fr.ybo.transportscommun.activity.commun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.TabActivity;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;


import fr.ybo.transportscommun.AbstractTransportsApplication;
import fr.ybo.transportscommun.R;
import fr.ybo.transportscommun.activity.AccueilActivity;

public class BaseActivity {

	private static boolean onOptionsItemSelected(MenuItem item, ActivityHelper helper, Activity activity) {
        if (item.getItemId() == android.R.id.home) {
            helper.goHome();
            return true;
        }
		return ((AbstractTransportsApplication) activity.getApplication())
				.onOptionsItemSelected(item, activity, helper);
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
			mTabsAdapter.addTab(mTabHost.newTabSpec(id), fragment, args, title);
		}

		protected void setCurrentTab(String tag) {
			mTabHost.setCurrentTabByTag(tag);
		}

		protected void setCurrentTab(Bundle savedInstanceState) {
			if (savedInstanceState != null) {
				mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
			} else {
				mTabHost.setCurrentTab(0);
			}
		}

		protected void setOnFragmentChange(OnFragmentChange onFragmentChange) {
			mTabsAdapter.setOnFragmentChange(onFragmentChange);
		}

		protected Fragment getCurrentFragment() {
			return mTabsAdapter.getItem(mTabHost.getCurrentTab());
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

	public static abstract class BaseFragmentActivity extends FragmentActivity {
		final ActivityHelper mActivityHelper = ActivityHelper.createInstance(this);

		public ActivityHelper getActivityHelper() {
			return mActivityHelper;
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			AbstractTransportsApplication.majTheme(this);
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
			if (this instanceof AccueilActivity) {
				getActivityHelper().setupHomeActivity();
			} else {
				getActivityHelper().setupSubActivity();
			}
		}

	}

	@SuppressWarnings("deprecation")
	public static abstract class BaseTabActivity extends TabActivity {
		final ActivityHelper mActivityHelper = ActivityHelper.createInstance(this);

		public ActivityHelper getActivityHelper() {
			return mActivityHelper;
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			AbstractTransportsApplication.majTheme(this);
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

		@SuppressWarnings("deprecation")
		@Override
		protected void onPostCreate(Bundle savedInstanceState) {
			super.onPostCreate(savedInstanceState);
			getActivityHelper().setupSubActivity();
		}

	}

	public static abstract class BaseSimpleActivity extends Activity {
		final ActivityHelper mActivityHelper = ActivityHelper.createInstance(this);

		public ActivityHelper getActivityHelper() {
			return mActivityHelper;
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			AbstractTransportsApplication.majTheme(this);
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

	public static abstract class BasePreferenceActivity extends PreferenceActivity {
		final ActivityHelper mActivityHelper = ActivityHelper.createInstance(this);

		public ActivityHelper getActivityHelper() {
			return mActivityHelper;
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			AbstractTransportsApplication.majTheme(this);
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

	public static abstract class BaseListActivity extends ListActivity {
		final ActivityHelper mActivityHelper = ActivityHelper.createInstance(this);

		public ActivityHelper getActivityHelper() {
			return mActivityHelper;
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			AbstractTransportsApplication.majTheme(this);
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

	public static interface OnFragmentChange {
		public void onFragmentChanged(Fragment currentFragment);
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
		private final FragmentActivity mContext;
		private final TabHost mTabHost;
		private final ViewPager mViewPager;
		private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();
		private final DummyTabFactory dummyTabFactory;

		static final class TabInfo {
			private final Class<?> clss;
			private final Bundle args;

			TabInfo(Class<?> _class, Bundle _args) {
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
			dummyTabFactory = new DummyTabFactory(activity);
		}

		public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args, String title) {
			if (UIUtils.isHoneycomb()) {
				tabSpec.setIndicator(title);
			} else {
				View view = LayoutInflater.from(mContext).inflate(R.layout.tabs_bg, null);
				TextView tv = (TextView) view.findViewById(R.id.tabsText);
				tv.setText(title);
				tv.setTextColor(AbstractTransportsApplication.getTextColor(mContext));
				tabSpec.setIndicator(view);
			}
			tabSpec.setContent(dummyTabFactory);

			TabInfo info = new TabInfo(clss, args);
			mTabs.add(info);
			mTabHost.addTab(tabSpec);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return mTabs.size();
		}

		private Map<Integer, Fragment> mapFragments = new HashMap<Integer, Fragment>();

		@Override
		public Fragment getItem(int position) {
			if (!mapFragments.containsKey(position)) {
				TabInfo info = mTabs.get(position);
				mapFragments.put(position, Fragment.instantiate(mContext, info.clss.getName(), info.args));
			}
			return mapFragments.get(position);
		}

		@Override
		public void onTabChanged(String tabId) {
			int position = mTabHost.getCurrentTab();
			mViewPager.setCurrentItem(position);
			onFragmentChange(position);
		}

		public void onFragmentChange(int position) {
			if (onFragmentChange != null) {
				onFragmentChange.onFragmentChanged(getItem(position));
			}
		}

		private OnFragmentChange onFragmentChange = null;

		public void setOnFragmentChange(OnFragmentChange onFragmentChange) {
			this.onFragmentChange = onFragmentChange;
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
