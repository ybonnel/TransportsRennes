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
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.activity.commun.BaseActivity.BaseSimpleActivity;
import fr.ybo.transportsrennes.application.TransportsRennesApplication;

public class PreferencesRennes extends BaseSimpleActivity {

	private boolean notifUpdateOn = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		TransportsRennesApplication.majTheme(this);
		super.onCreate(savedInstanceState);
		notifUpdateOn = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("TransportsRennes_notifUpdate",
				true);
		setContentView(R.layout.preferences);
		Button boutonTerminer = (Button) findViewById(R.id.preferencesTermine);
		boutonTerminer.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				finish();
			}
		});
		CheckBox notifUpdateOnCheckBox = (CheckBox) findViewById(R.id.notifUpdateOn);
		notifUpdateOnCheckBox.setChecked(notifUpdateOn);
		notifUpdateOnCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				notifUpdateOn = isChecked;
				SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(PreferencesRennes.this)
						.edit();
				editor.putBoolean("TransportsRennes_notifUpdate", notifUpdateOn);
				editor.commit();
			}
		});

		RadioButton whiteTheme = (RadioButton) findViewById(R.id.whiteTheme);
		whiteTheme.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					changeTheme(0);
				}
			}
		});
		RadioButton blackTheme = (RadioButton) findViewById(R.id.blackTheme);
		blackTheme.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					changeTheme(1);
				}
			}
		});
		switch (TransportsRennesApplication.getTheme(getApplicationContext())) {
			case 1:
				blackTheme.setChecked(true);
				break;
			default:
				whiteTheme.setChecked(true);
				break;
		}
	}

	private void changeTheme(int theme) {
		if (theme != TransportsRennesApplication.getTheme(getApplicationContext())) {
			SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(PreferencesRennes.this)
					.edit();
			editor.putInt("TransportsRennes_theme", theme);
			editor.commit();
			TransportsRennesApplication.majTheme(PreferencesRennes.this);
			startActivity(new Intent(PreferencesRennes.this, PreferencesRennes.class));
			finish();
		}
	}

}
