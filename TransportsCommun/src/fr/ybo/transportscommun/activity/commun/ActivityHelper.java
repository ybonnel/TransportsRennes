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

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import fr.ybo.transportscommun.AbstractTransportsApplication;
import fr.ybo.transportscommun.R;
import fr.ybo.transportscommun.activity.AccueilActivity;
import fr.ybo.transportscommun.util.CompatUtil;

/**
 * A class that handles some common activity-related functionality in the app,
 * such as setting up the action bar. This class provides functioanlity useful
 * for both phones and tablets, and does not require any Android 3.0-specific
 * features.
 */
public class ActivityHelper {
    final Activity mActivity;

    /**
     * Factory method for creating {@link ActivityHelper} objects for a given
     * activity. Depending on which device the app is running, either a basic
     * helper or Honeycomb-specific helper will be returned.
     */
    public static ActivityHelper createInstance(final Activity activity) {
        return UIUtils.isHoneycomb() ? new ActivityHelperHoneycomb(activity) : new ActivityHelper(activity);
    }

    ActivityHelper(final Activity activity) {
        mActivity = activity;
    }

    /**
     * Method, to be called in {@code onPostCreate}, that sets up this
     * activity as the home activity for the app.
     */
    public void setupHomeActivity() {
    }

    /**
     * Method, to be called in {@code onPostCreate}, that sets up this
     * activity as a sub-activity in the app.
     */
    public void setupSubActivity() {
    }

    /**
     * Invoke "home" action, returning to HomeActivity.
     */
    public void goHome() {
        if (mActivity instanceof AccueilActivity) {
            return;
        }

        final Intent intent = new Intent(mActivity,
                ((AbstractTransportsApplication) mActivity.getApplication()).getAccueilActivity());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mActivity.startActivity(intent);
    }

    /**
     * Sets up the action bar with the given title and accent color. If title is
     * null, then the app logo will be shown instead of a title. Otherwise, a
     * home button and title are visible. If color is null, then the default
     * colorstrip is visible.
     */
    public void setupActionBar(final int resourceMenuNormal, final int resourceMenuNoir) {
        int resourceMenu = resourceMenuNormal;
        if (((AbstractTransportsApplication) mActivity.getApplication()).isThemeNoir()) {
            resourceMenu = resourceMenuNoir;
        }
        final ViewGroup actionBarCompat = getActionBarCompat();
        if (actionBarCompat == null) {
            addMenus(resourceMenu);
            return;
        }
        actionBarCompat.setBackgroundResource(((AbstractTransportsApplication) mActivity.getApplication())
                .getActionBarBackground());
        final LinearLayout.LayoutParams springLayoutParams = new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.FILL_PARENT);
        springLayoutParams.weight = 1;

        final View.OnClickListener homeClickListener = new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                goHome();
            }
        };

        // Add logo
        final ImageButton logo = new ImageButton(mActivity, null, R.attr.actionbarCompatLogoStyle);
        logo.setImageResource(AbstractTransportsApplication.getDonnesSpecifiques().getCompactLogo());
        logo.setOnClickListener(homeClickListener);
        actionBarCompat.addView(logo);

        // Add spring (dummy view to align future children to the right)
        final View spring = new View(mActivity);
        spring.setLayoutParams(springLayoutParams);
        actionBarCompat.addView(spring);
        addMenus(resourceMenu);
    }

    public boolean onCreateOptionsMenu(final Menu menu) {
        return false;
    }

    void addMenus(final int resourceMenu) {
        final SimpleMenu simpleMenu = new SimpleMenu(mActivity);
        mActivity.getMenuInflater().inflate(resourceMenu, simpleMenu);
        for (int i = 0; i < simpleMenu.size(); i++) {
            final MenuItem item = simpleMenu.getItem(i);
            if (item.getItemId() == R.id.menu_search && mActivity instanceof Searchable) {
                addActionButtonCompat(item.getItemId(), item.getIcon(), item.getTitle(), new View.OnClickListener() {

                    private boolean visible;

                    @Override
                    public void onClick(final View v) {
                        if (visible) {
                            mActivity.findViewById(R.id.edittext_search).setVisibility(View.GONE);
                            visible = false;
                        } else {
                            mActivity.findViewById(R.id.edittext_search).setVisibility(View.VISIBLE);
                            mActivity.findViewById(R.id.edittext_search).requestFocus();
                            visible = true;
                        }
                    }
                });
            } else {
                addActionButtonCompat(item.getItemId(), item.getIcon(), item.getTitle(), new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        mActivity.onOptionsItemSelected(item);
                    }
                });
            }
        }
        if (mActivity instanceof Searchable) {
            final EditText editText = (EditText) mActivity.findViewById(R.id.edittext_search);
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2) {
                }

                @Override
                public void onTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2) {
                }

                @Override
                public void afterTextChanged(final Editable editable) {
                    ((Searchable) mActivity).updateQuery(editText.getText().toString());
                }
            });
        }
    }

    public void invalidateOptionsMenu() {
        if (UIUtils.isHoneycomb()) {
            CompatUtil.invalidateOptionsMenu(mActivity);
        } else if (mActivity instanceof ChangeIconActionBar) {
            final ViewGroup actionBar = getActionBarCompat();
            if (actionBar == null) {
                return;
            }
            for (int i = 0; i < actionBar.getChildCount(); i++) {
                final View view = actionBar.getChildAt(i);
                if (view instanceof ImageButton) {
                    ((ChangeIconActionBar) mActivity).changeIconActionBar((ImageButton) view);
                }
            }
            actionBar.invalidate();
        }
    }

    /**
     * Returns the {@link ViewGroup} for the action bar on phones (compatibility
     * action bar). Can return null, and will return null on Honeycomb.
     */
    private ViewGroup getActionBarCompat() {
        return (ViewGroup) mActivity.findViewById(R.id.actionbar_compat);
    }

    /**
     * Adds an action bar button to the compatibility action bar (on phones).
     */
    private View addActionButtonCompat(final int id, final Drawable icon, final CharSequence title, final View.OnClickListener clickListener) {
        final ViewGroup actionBar = getActionBarCompat();
        if (actionBar == null) {
            return null;
        }

        // Create the separator
        final ImageView separator = new ImageView(mActivity, null, R.attr.actionbarCompatSeparatorStyle);
        separator.setLayoutParams(new ViewGroup.LayoutParams(2, ViewGroup.LayoutParams.FILL_PARENT));

        // Create the button
        final ImageButton actionButton = new ImageButton(mActivity, null, R.attr.actionbarCompatButtonStyle);
        actionButton.setId(id);
        actionButton.setLayoutParams(new ViewGroup.LayoutParams((int) mActivity.getResources().getDimension(
                R.dimen.actionbar_compat_height), ViewGroup.LayoutParams.FILL_PARENT));
        actionButton.setImageDrawable(icon);
        actionButton.setScaleType(ImageView.ScaleType.CENTER);
        actionButton.setContentDescription(title);
        actionButton.setOnClickListener(clickListener);

        // Add separator and button to the action bar in the desired order

        actionBar.addView(separator);

        actionBar.addView(actionButton);

        return actionButton;
    }
}
