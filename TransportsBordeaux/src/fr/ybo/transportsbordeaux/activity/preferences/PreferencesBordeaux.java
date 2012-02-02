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
package fr.ybo.transportsbordeaux.activity.preferences;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import fr.ybo.transportsbordeaux.R;
import fr.ybo.transportsbordeaux.activity.commun.MenuAccueil;
import fr.ybo.transportsbordeaux.application.TransportsBordeauxApplication;
import fr.ybo.transportsbordeaux.database.TransportsBordeauxDatabase;
import fr.ybo.transportsbordeaux.util.TacheAvecProgressDialog;

public class PreferencesBordeaux extends MenuAccueil.Activity {

    private boolean dbOnSdCard = false;
    private boolean notifUpdateOn = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbOnSdCard = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("TransportsBordeaux_sdCard", false);
        notifUpdateOn = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
                "TransportsBordeaux_notifUpdate", true);
        setContentView(R.layout.preferences);
        Button boutonTerminer = (Button) findViewById(R.id.preferencesTermine);
        boutonTerminer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                boolean oldDbOnSdCard = PreferenceManager.getDefaultSharedPreferences(PreferencesBordeaux.this)
                        .getBoolean("TransportsBordeaux_sdCard", false);
                if (oldDbOnSdCard != dbOnSdCard) {
                    if (dbOnSdCard && !Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                        Toast.makeText(PreferencesBordeaux.this, R.string.sdCardInaccessbile, Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(PreferencesBordeaux.this);
                    View alertView = LayoutInflater.from(PreferencesBordeaux.this).inflate(R.layout.infoapropos, null);
                    TextView textView = (TextView) alertView.findViewById(R.id.textAPropos);
                    textView.setText(R.string.changeDbOnSdCard);
                    builder.setView(alertView);
                    builder.setCancelable(false);
                    builder.setNegativeButton(R.string.non, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            PreferencesBordeaux.this.finish();
                        }
                    });
                    builder.setPositiveButton(R.string.oui, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            new TacheAvecProgressDialog<Void, Void, Void>(PreferencesBordeaux.this,
                                    PreferencesBordeaux.this.getString(R.string.suppressionDB)) {

                                @Override
                                protected Void doInBackground(Void... params) {
                                    PreferencesBordeaux.this.deleteDatabase(TransportsBordeauxDatabase.DATABASE_NAME);
                                    return null;
                                }

                                @Override
                                protected void onPostExecute(Void result) {
                                    super.onPostExecute(result);
                                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(
                                            PreferencesBordeaux.this).edit();
                                    editor.putBoolean("TransportsBordeaux_sdCard", dbOnSdCard);
                                    editor.commit();
                                    TransportsBordeauxApplication.constructDatabase(PreferencesBordeaux.this
                                            .getApplicationContext());
                                    TransportsBordeauxApplication.setBaseNeuve(true);
                                    PreferencesBordeaux.this.finish();
                                }
                            }.execute((Void) null);
                        }
                    });
                    builder.create().show();
                } else {
                    PreferencesBordeaux.this.finish();
                }
            }
        });
        CheckBox dbOnSdCheckBox = (CheckBox) findViewById(R.id.dbOnSdCard);
        dbOnSdCheckBox.setChecked(dbOnSdCard);
        dbOnSdCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dbOnSdCard = isChecked;
            }
        });
        CheckBox notifUpdateOnCheckBox = (CheckBox) findViewById(R.id.notifUpdateOn);
        notifUpdateOnCheckBox.setChecked(notifUpdateOn);
        notifUpdateOnCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                notifUpdateOn = isChecked;
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(
                        PreferencesBordeaux.this).edit();
                editor.putBoolean("TransportsBordeaux_notifUpdate", notifUpdateOn);
                editor.commit();
            }
        });
    }

}
