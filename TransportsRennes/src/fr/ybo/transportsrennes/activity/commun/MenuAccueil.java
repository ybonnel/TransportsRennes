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
package fr.ybo.transportsrennes.activity.commun;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.google.android.maps.MapView;
import com.ubikod.capptain.android.sdk.activity.CapptainActivity;
import com.ubikod.capptain.android.sdk.activity.CapptainListActivity;
import com.ubikod.capptain.android.sdk.activity.CapptainMapActivity;

import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.activity.TransportsRennes;
import fr.ybo.transportsrennes.application.TransportsRennesApplication;

public class MenuAccueil {

    private static final int GROUP_ID = 99;
    private static final int MENU_ID = 99;

    private MenuAccueil() {
    }

    private static void addMenu(Menu menu) {
        menu.add(GROUP_ID, MENU_ID, Menu.NONE, R.string.menu_accueil).setIcon(R.drawable.ic_menu_home);
    }

    private static boolean onOptionsItemSelected(Context context, MenuItem item) {
        if (item.getItemId() == MENU_ID) {
            Intent intent = new Intent(context, TransportsRennes.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
            return true;
        }
        return false;
    }

    public abstract static class MapActivity extends CapptainMapActivity {

		@Override
		protected void onCreate(Bundle bundle) {
			TransportsRennesApplication.majTheme(this);
			super.onCreate(bundle);
		}

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            super.onCreateOptionsMenu(menu);
            addMenu(menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            super.onOptionsItemSelected(item);
            return MenuAccueil.onOptionsItemSelected(this, item);
        }

        private MapView mapView;

        private boolean satelite = false;

        protected void gestionButtonLayout() {
            mapView = (MapView) findViewById(R.id.mapview);
            mapView.setSatellite(false);
            ImageButton layoutButton = (ImageButton) findViewById(R.id.layers_button);
            layoutButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
                    String[] items = {"Satellite"};
                    boolean[] checkeds = {satelite};
                    builder.setMultiChoiceItems(items, checkeds, new OnMultiChoiceClickListener() {
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            satelite = !satelite;
                            mapView.setSatellite(satelite);
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });
        }
    }

    public abstract static class ListActivity extends CapptainListActivity {

		@Override
		protected void onCreate(Bundle bundle) {
			TransportsRennesApplication.majTheme(this);
			super.onCreate(bundle);
		}

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            super.onCreateOptionsMenu(menu);
            addMenu(menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            super.onOptionsItemSelected(item);
            return MenuAccueil.onOptionsItemSelected(this, item);
        }
    }

    public abstract static class Activity extends CapptainActivity {
		@Override
		protected void onCreate(Bundle bundle) {
			TransportsRennesApplication.majTheme(this);
			super.onCreate(bundle);
		}

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            super.onCreateOptionsMenu(menu);
            addMenu(menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            super.onOptionsItemSelected(item);
            return MenuAccueil.onOptionsItemSelected(this, item);
        }
    }
}
