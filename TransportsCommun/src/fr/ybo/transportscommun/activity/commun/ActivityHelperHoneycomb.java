/*
 * Copyright 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.ybo.transportscommun.activity.commun;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Build;
import android.view.Menu;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import fr.ybo.transportscommun.R;

/**
 * An extension of {@link ActivityHelper} that provides Android 3.0-specific
 * functionality for Honeycomb tablets. It thus requires API level 11.
 */
public class ActivityHelperHoneycomb extends ActivityHelper {

	protected ActivityHelperHoneycomb(Activity activity) {
		super(activity);
	}

	/** {@inheritDoc} */
	@Override
	public void setupHomeActivity() {
		super.setupHomeActivity();
		// NOTE: there needs to be a content view set before this is called, so
		// this method
		// should be called in onPostCreate.
		if (UIUtils.isTablet(mActivity)) {
			mActivity.getActionBar().setDisplayOptions(0, ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
		} else {
			mActivity.getActionBar().setDisplayOptions(ActionBar.DISPLAY_USE_LOGO,
                    ActionBar.DISPLAY_USE_LOGO | ActionBar.DISPLAY_SHOW_TITLE);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void setupSubActivity() {
		super.setupSubActivity();
		// NOTE: there needs to be a content view set before this is called, so
		// this method
		// should be called in onPostCreate.
		if (UIUtils.isTablet(mActivity)) {
			mActivity.getActionBar().setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_USE_LOGO,
					ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_USE_LOGO);
		} else {
			mActivity.getActionBar().setDisplayOptions(ActionBar.DISPLAY_USE_LOGO,
					ActionBar.DISPLAY_USE_LOGO | ActionBar.DISPLAY_SHOW_TITLE);
		}
	}

	private int resourceMenu = -1;

	@Override
	protected void addMenus(int resourceMenu) {
		this.resourceMenu = resourceMenu;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (resourceMenu == -1) {
			return false;
		}
		mActivity.getMenuInflater().inflate(resourceMenu, menu);
		if (mActivity instanceof Searchable) {
			SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
			searchView.setOnQueryTextListener(new OnQueryTextListener() {

				@Override
				public boolean onQueryTextSubmit(String query) {
					return false;
				}

				@Override
				public boolean onQueryTextChange(String newText) {
					((Searchable) mActivity).updateQuery(newText);
					return true;
				}
			});
		}
		return true;
	}

    @Override
    public void setupActionBar(int resourceMenuNormal, int resourceMenuNoir) {
        super.setupActionBar(resourceMenuNormal, resourceMenuNoir);
        mActivity.getActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
