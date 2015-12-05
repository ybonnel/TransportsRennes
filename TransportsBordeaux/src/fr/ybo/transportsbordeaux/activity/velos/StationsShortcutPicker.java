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
package fr.ybo.transportsbordeaux.activity.velos;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import fr.ybo.transportsbordeaux.R;
import fr.ybo.transportscommun.activity.commun.BaseActivity.BaseSimpleActivity;


public class StationsShortcutPicker extends BaseSimpleActivity {


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupShortcut();
        finish();
    }


    private void setupShortcut() {
        // First, set up the shortcut intent.
        final Intent shortcutIntent = new Intent(this, ListStationsByPosition.class);

        // Then, set up the container intent (the response to the caller)
        final Intent intent = new Intent();
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.btn_velo));
		final Parcelable iconResource = Intent.ShortcutIconResource.fromContext(this, R.drawable.btn_velo_default);
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconResource);

        // Now, return the result to the launcher
        setResult(RESULT_OK, intent);
    }
}
