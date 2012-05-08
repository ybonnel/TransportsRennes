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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.EditText;
import android.widget.Toast;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;

import fr.ybo.transportscommun.activity.commun.BaseActivity.BaseFragmentActivity;
import fr.ybo.transportscommun.donnees.manager.FavorisManager;
import fr.ybo.transportscommun.donnees.modele.GroupeFavori;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.activity.loading.LoadingActivity;
import fr.ybo.transportsrennes.application.TransportsRennesApplication;

/**
 * @author ybonnel
 */
@EActivity(R.layout.listfavoris)
@OptionsMenu(R.menu.bus_favoris_menu_no_action_bar)
public class ListFavorisForNoGroup extends BaseFragmentActivity {

	@AfterViews
	void afterViews() {
		getActivityHelper().setupActionBar(R.menu.bus_favoris_menu_items, R.menu.holo_bus_favoris_menu_items);
		if (FavorisManager.getInstance().hasFavorisToLoad()) {
			Intent intent = new Intent(this, LoadingActivity.class);
			intent.putExtra("operation", LoadingActivity.OPERATION_LOAD_FAVORIS);
			startActivity(intent);
		}
    }

	@OptionsItem
	void menuExport() {
		FavorisManager.getInstance().export(this);
	}

	@OptionsItem
	void menuImport() {
		FavorisManager.getInstance().load(this);
		startActivity(new Intent(this, TabFavoris.class));
		finish();
	}

	@OptionsItem
	void menuAjouter() {
		showDialog(AJOUTER_GROUPE_DIALOG_ID);
	}

	private static final int AJOUTER_GROUPE_DIALOG_ID = 0;

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == AJOUTER_GROUPE_DIALOG_ID) {
			final AlertDialog.Builder alert = new AlertDialog.Builder(this);
			final EditText input = new EditText(this);
			alert.setView(input);
			alert.setPositiveButton(getString(R.string.ajouter), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					String value = input.getText().toString().trim();
					if (value == null || value.length() == 0) {
						Toast.makeText(ListFavorisForNoGroup.this, getString(R.string.groupeObligatoire),
								Toast.LENGTH_LONG).show();
						return;
					}
					GroupeFavori groupeFavori = new GroupeFavori();
					groupeFavori.name = value;
					if (!TransportsRennesApplication.getDataBaseHelper().select(groupeFavori).isEmpty()
							|| value.equals(getString(R.string.all))) {
						Toast.makeText(ListFavorisForNoGroup.this, getString(R.string.groupeExistant),
								Toast.LENGTH_LONG).show();
						return;
					}
					TransportsRennesApplication.getDataBaseHelper().insert(groupeFavori);
					startActivity(new Intent(ListFavorisForNoGroup.this, TabFavoris.class));
					ListFavorisForNoGroup.this.finish();
				}
			});

			alert.setNegativeButton(getString(R.string.annuler), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			return alert.create();
		}
		return super.onCreateDialog(id);
	}
}
