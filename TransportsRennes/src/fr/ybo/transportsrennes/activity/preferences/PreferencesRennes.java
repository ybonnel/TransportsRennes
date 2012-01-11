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
 * 
 * Contributors:
 *     ybonnel - initial API and implementation
 */
package fr.ybo.transportsrennes.activity.preferences;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.activity.commun.BaseActivity.BasePreferenceActivity;
import fr.ybo.transportsrennes.application.TransportsRennesApplication;

public class PreferencesRennes extends BasePreferenceActivity {

	private OnSharedPreferenceChangeListener prefListenner;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		TransportsRennesApplication.majTheme(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preferences);
		getActivityHelper().setupActionBar(R.menu.default_menu_items, R.menu.holo_default_menu_items);
		addPreferencesFromResource(R.xml.preferences);
		prefListenner = new OnSharedPreferenceChangeListener() {

			@Override
			public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
				if ("TransportsRennes_choixTheme".equals(key)) {
					restart();
				}
			}
		};
		getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(prefListenner);
	}

	@Override
	protected void onDestroy() {
		getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(prefListenner);
		super.onDestroy();
	}

	public void restart() {
		startActivity(new Intent(PreferencesRennes.this, PreferencesRennes.class));
		finish();
	}
}
