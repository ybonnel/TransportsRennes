package fr.ybo.transportsrennes.activity.commun;

import android.view.Menu;
import android.view.MenuItem;
import fr.ybo.transportsrennes.activity.actionbar.ActivityHelper;
import fr.ybo.transportsrennes.util.CapptainFragmentActivity;

public class BaseActivity {

	public static abstract class BaseFragmentActivity extends CapptainFragmentActivity {
		final ActivityHelper mActivityHelper = ActivityHelper.createInstance(this);

		public ActivityHelper getActivityHelper() {
			return mActivityHelper;
		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			switch (item.getItemId()) {
				case android.R.id.home:
					// app icon in action bar clicked; go home
					mActivityHelper.goHome();
				default:
					return super.onOptionsItemSelected(item);
			}
		}

		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			return mActivityHelper.onCreateOptionsMenu(menu);
		}

	}
}
