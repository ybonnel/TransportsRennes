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
package fr.ybo.transportsrennes.activity.bus;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import fr.ybo.transportscommun.activity.commun.BaseActivity.BaseSimpleActivity;
import fr.ybo.transportsrennes.R;

/**
 * Activité affichant les lignes de bus..
 *
 * @author ybonnel
 */
public class BusFavorisShortcutPicker extends BaseSimpleActivity {


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupShortcut();
        finish();
    }


    private void setupShortcut() {
        // First, set up the shortcut intent.
        final Parcelable shortcutIntent = new Intent(this, TabFavoris.class);

        // Then, set up the container intent (the response to the caller)
        final Parcelable iconResource = Intent.ShortcutIconResource.fromContext(this, R.drawable.btn_bus_star_default);
        final Intent intent = new Intent().putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent).putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.btn_bus_favori)).putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconResource);

        // Now, return the result to the launcher
        setResult(RESULT_OK, intent);
    }
}
