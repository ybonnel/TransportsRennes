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

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

/**
 * A <em>really</em> dumb implementation of the {@link MenuItem} interface, that's only useful for
 * our old-actionbar purposes. See {@code com.android.internal.view.menu.MenuItemImpl} in
 * AOSP for a more complete implementation.
 */
class SimpleMenuItem implements MenuItem {

    private final SimpleMenu mMenu;

    private final int mId;
    private final int mOrder;
    private CharSequence mTitle;
    private CharSequence mTitleCondensed;
    private Drawable mIconDrawable;
    private int mIconResId;
    private boolean mEnabled = true;

    SimpleMenuItem(final SimpleMenu menu, final int id, final int order, final CharSequence title) {
        mMenu = menu;
        mId = id;
        mOrder = order;
        mTitle = title;
    }

    @Override
    public int getItemId() {
        return mId;
    }

    @Override
    public int getOrder() {
        return mOrder;
    }

    @Override
    public MenuItem setTitle(final CharSequence title) {
        mTitle = title;
        return this;
    }

    @Override
    public MenuItem setTitle(final int titleRes) {
        return setTitle(mMenu.getContext().getString(titleRes));
    }

    @Override
    public CharSequence getTitle() {
        return mTitle;
    }

    @Override
    public MenuItem setTitleCondensed(final CharSequence title) {
        mTitleCondensed = title;
        return this;
    }

    @Override
    public CharSequence getTitleCondensed() {
        return mTitleCondensed != null ? mTitleCondensed : mTitle;
    }

   @Override
   public MenuItem setIcon(final Drawable icon) {
        mIconResId = 0;
        mIconDrawable = icon;
        return this;
    }

    @Override
    public MenuItem setIcon(final int iconResId) {
        mIconDrawable = null;
        mIconResId = iconResId;
        return this;
    }

    @Override
    public Drawable getIcon() {
        if (mIconDrawable != null) {
            return mIconDrawable;
        }

        if (mIconResId != 0) {
            return mMenu.getResources().getDrawable(mIconResId);
        }

        return null;
    }

    @Override
    public MenuItem setEnabled(final boolean enabled) {
        mEnabled = enabled;
        return this;
    }

    @Override
    public boolean isEnabled() {
        return mEnabled;
    }

    // No-op operations. We use no-ops to allow inflation from menu XML.

    @Override
    public int getGroupId() {
        return 0;
    }

    @Override
    public View getActionView() {
        return null;
    }

    @Override
    public MenuItem setIntent(final Intent intent) {
        // Noop
        return this;
    }

    @Override
    public Intent getIntent() {
        return null;
    }

    @Override
    public MenuItem setShortcut(final char c, final char c1) {
        // Noop
        return this;
    }

    @Override
    public MenuItem setNumericShortcut(final char c) {
        // Noop
        return this;
    }

    @Override
    public char getNumericShortcut() {
        return 0;
    }

    @Override
    public MenuItem setAlphabeticShortcut(final char c) {
        // Noop
        return this;
    }

    @Override
    public char getAlphabeticShortcut() {
        return 0;
    }

    @Override
    public MenuItem setCheckable(final boolean b) {
        // Noop
        return this;
    }

    @Override
    public boolean isCheckable() {
        return false;
    }

    @Override
    public MenuItem setChecked(final boolean b) {
        // Noop
        return this;
    }

    @Override
    public boolean isChecked() {
        return false;
    }

    @Override
    public MenuItem setVisible(final boolean b) {
        // Noop
        return this;
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public boolean hasSubMenu() {
        return false;
    }

    @Override
    public SubMenu getSubMenu() {
        return null;
    }

    @Override
    public MenuItem setOnMenuItemClickListener(
            final OnMenuItemClickListener onMenuItemClickListener) {
        // Noop
        return this;
    }

    @Override
    public ContextMenu.ContextMenuInfo getMenuInfo() {
        return null;
    }

    @Override
    public void setShowAsAction(final int i) {
        // Noop
    }

    @Override
    public MenuItem setActionView(final View view) {
        // Noop
        return this;
    }

    @Override
    public MenuItem setActionView(final int i) {
        // Noop
        return this;
    }

}
