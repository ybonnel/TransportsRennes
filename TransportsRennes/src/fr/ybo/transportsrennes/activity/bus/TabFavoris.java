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

import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.activity.commun.BaseActivity.BaseTabFragmentActivity;
import fr.ybo.transportsrennes.application.TransportsRennesApplication;
import fr.ybo.transportsrennes.database.FavorisManager;
import fr.ybo.transportsrennes.database.modele.ArretFavori;
import fr.ybo.transportsrennes.database.modele.GroupeFavori;
import fr.ybo.transportsrennes.fragments.bus.ListFavoris;
import fr.ybo.transportsrennes.util.LogYbo;

public class TabFavoris extends BaseTabFragmentActivity {

	private static final LogYbo LOG_YBO = new LogYbo(TabFavoris.class);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tabfavoris);
		getActivityHelper().setupActionBar(R.menu.bus_favoris_menu_items, R.menu.holo_bus_favoris_menu_items);
		List<GroupeFavori> groupes = TransportsRennesApplication.getDataBaseHelper().selectAll(GroupeFavori.class);
		if (groupes.isEmpty()) {
			Intent intent = new Intent(this, ListFavorisForNoGroup.class);
			startActivity(intent);
			finish();
			return;
		}

		configureTabs();

		addTab("all", getString(R.string.all), ListFavoris.class);
		for (GroupeFavori groupe : groupes) {
			Bundle args = new Bundle();
			args.putString("groupe", groupe.name);
			addTab(groupe.name, groupe.name, ListFavoris.class, args);
		}

		setCurrentTab(savedInstanceState);
	}

	private static final int GROUP_ID = 0;
	private static final int MENU_AJOUTER = 1;
	private static final int MENU_SUPPRIMER = 2;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuItem itemAjout = menu.add(GROUP_ID, MENU_AJOUTER, Menu.NONE, R.string.ajouterGroupe);
		itemAjout.setIcon(android.R.drawable.ic_menu_add);
		MenuItem itemSupp = menu.add(GROUP_ID, MENU_SUPPRIMER, Menu.NONE, R.string.suprimerGroupe);
		itemSupp.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		if ("all".equals(getCurrentTab())) {
			menu.findItem(MENU_SUPPRIMER).setVisible(false);
		} else {
			menu.findItem(MENU_SUPPRIMER).setVisible(true);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
			case R.id.menu_export:
				FavorisManager.getInstance().export(this);
				break;
			case R.id.menu_import:
				FavorisManager.getInstance().load(this);
				break;
			case MENU_SUPPRIMER:
				ArretFavori arretFavori = new ArretFavori();
				arretFavori.groupe = getCurrentTab();
				for (ArretFavori favori : TransportsRennesApplication.getDataBaseHelper().select(arretFavori)) {
					favori.groupe = "";
					TransportsRennesApplication.getDataBaseHelper().update(favori);
				}
				GroupeFavori groupeFavori = new GroupeFavori();
				groupeFavori.name = getCurrentTab();
				TransportsRennesApplication.getDataBaseHelper().delete(groupeFavori);
				startActivity(new Intent(this, TabFavoris.class));
				finish();
				return true;
			case MENU_AJOUTER:
				createDialogAjoutGroupe();
				return true;
		}
		return false;
	}

	private void createDialogAjoutGroupe() {
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		final EditText input = new EditText(this);
		alert.setView(input);
		alert.setPositiveButton(getString(R.string.ajouter), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString().trim();
				if (value == null || value.length() == 0) {
					Toast.makeText(TabFavoris.this, getString(R.string.groupeObligatoire), Toast.LENGTH_LONG).show();
					return;
				}
				GroupeFavori groupeFavori = new GroupeFavori();
				groupeFavori.name = value;
				if (!TransportsRennesApplication.getDataBaseHelper().select(groupeFavori).isEmpty()
						|| value.equals(getString(R.string.all))) {
					Toast.makeText(TabFavoris.this, getString(R.string.groupeExistant), Toast.LENGTH_LONG).show();
					return;
				}
				TransportsRennesApplication.getDataBaseHelper().insert(groupeFavori);
				startActivity(new Intent(TabFavoris.this, TabFavoris.class));
				finish();
			}
		});

		alert.setNegativeButton(getString(R.string.annuler), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
			}
		});
		alert.create().show();
	}
}
