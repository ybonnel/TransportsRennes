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
package fr.ybo.transportsrennes.activity.bus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import fr.ybo.transportsrennes.*;
import fr.ybo.transportsrennes.activity.commun.MenuAccueil;
import fr.ybo.transportsrennes.activity.widgets.TransportsWidget11Configure;
import fr.ybo.transportsrennes.activity.widgets.TransportsWidget21Configure;
import fr.ybo.transportsrennes.activity.widgets.TransportsWidgetConfigure;
import fr.ybo.transportsrennes.adapters.FavoriAdapter;
import fr.ybo.transportsrennes.application.TransportsRennesApplication;
import fr.ybo.transportsrennes.keolis.gtfs.modele.ArretFavori;
import fr.ybo.transportsrennes.keolis.gtfs.modele.GroupeFavori;
import fr.ybo.transportsrennes.util.UpdateTimeUtil;
import fr.ybo.transportsrennes.util.UpdateTimeUtil.UpdateTime;

/**
 * @author ybonnel
 */
public class ListFavoris extends MenuAccueil.ListActivity {

	private void construireListe() {
		ArretFavori favoriExemple = new ArretFavori();
		if (groupe != null) {
			favoriExemple.groupe = groupe;
		}
		List<ArretFavori> favoris = TransportsRennesApplication.getDataBaseHelper().select(favoriExemple, "ordre");

		setListAdapter(new FavoriAdapter(getApplicationContext(), favoris));
		ListView lv = getListView();
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				Intent intent = new Intent(ListFavoris.this, DetailArret.class);
				intent.putExtra("favori", (Serializable) adapterView.getAdapter().getItem(position));
				startActivity(intent);
			}
		});
		lv.setTextFilterEnabled(true);
		registerForContextMenu(lv);
	}

	private UpdateTimeUtil updateTimeUtil;

	private String groupe = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listfavoris);
		if (getIntent().getExtras() != null) {
			groupe = getIntent().getExtras().getString("groupe");
		}
		construireListe();
		updateTimeUtil = new UpdateTimeUtil(new UpdateTime() {

			@Override
			public void update(Calendar calendar) {
				((FavoriAdapter) getListAdapter()).majCalendar();
				((FavoriAdapter) getListAdapter()).notifyDataSetChanged();
			}
		}, this);
		updateTimeUtil.start();
	}

	@Override
	protected void onResume() {
		updateTimeUtil.start();
		super.onResume();
	}

	@Override
	protected void onPause() {
		updateTimeUtil.stop();
		super.onPause();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		if (v.getId() == android.R.id.list) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			ArretFavori favori = (ArretFavori) getListAdapter().getItem(info.position);
			menu.setHeaderTitle(favori.nomArret);
			menu.add(Menu.NONE, R.id.supprimerFavori, 0, getString(R.string.suprimerFavori));
			menu.add(Menu.NONE, R.id.deplacerGroupe, 0, getString(R.string.deplacerGroupe));
		}
	}


	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		final ArretFavori favori;
		switch (item.getItemId()) {
			case R.id.supprimerFavori:
				favori = (ArretFavori) getListAdapter().getItem(info.position);

				if (TransportsWidgetConfigure.isNotUsed(this, favori)
						&& TransportsWidget11Configure.isNotUsed(this, favori)
						&& TransportsWidget21Configure.isNotUsed(this, favori)) {
					TransportsRennesApplication.getDataBaseHelper().delete(favori);
					((FavoriAdapter) getListAdapter()).getFavoris().clear();
					((FavoriAdapter) getListAdapter()).getFavoris().addAll(TransportsRennesApplication.getDataBaseHelper().select(new ArretFavori()));
					((BaseAdapter) getListAdapter()).notifyDataSetChanged();
				} else {
					Toast.makeText(this, getString(R.string.favoriUsedByWidget), Toast.LENGTH_LONG).show();
				}
				return true;
			case R.id.deplacerGroupe:
				favori = (ArretFavori) getListAdapter().getItem(info.position);
				final List<String> groupes = new ArrayList<String>();
				groupes.add(getString(R.string.all));
				for (GroupeFavori groupe : TransportsRennesApplication.getDataBaseHelper()
						.selectAll(GroupeFavori.class)) {
					groupes.add(groupe.name);
				}
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle(getString(fr.ybo.transportsrennes.R.string.chooseGroupe));
				builder.setItems(groupes.toArray(new String[groupes.size()]), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialogInterface, int item) {
						String currentGroupe = groupes.get(item).equals(getString(R.string.all)) ? null : groupes
								.get(item);
						favori.groupe = currentGroupe;
						TransportsRennesApplication.getDataBaseHelper().update(favori);
						dialogInterface.dismiss();
						startActivity(new Intent(ListFavoris.this, TabFavoris.class));
						ListFavoris.this.finish();
					}
				});
				builder.create().show();
				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}

	private static final int GROUP_ID = 0;
	private static final int MENU_AJOUTER = 1;
	private static final int MENU_SUPPRIMER = 2;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuItem item = menu.add(GROUP_ID, MENU_AJOUTER, Menu.NONE, R.string.ajouterGroupe);
		item.setIcon(android.R.drawable.ic_menu_add);
		if (groupe != null) {
			MenuItem itemMap = menu.add(GROUP_ID, MENU_SUPPRIMER, Menu.NONE, R.string.suprimerGroupe);
			itemMap.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
			case MENU_SUPPRIMER:
				ArretFavori arretFavori = new ArretFavori();
				arretFavori.groupe = groupe;
				for (ArretFavori favori : TransportsRennesApplication.getDataBaseHelper().select(arretFavori)) {
					favori.groupe = "";
					TransportsRennesApplication.getDataBaseHelper().update(favori);
				}
				GroupeFavori groupeFavori = new GroupeFavori();
				groupeFavori.name = groupe;
				TransportsRennesApplication.getDataBaseHelper().delete(groupeFavori);
				startActivity(new Intent(this, TabFavoris.class));
				finish();
				return true;
			case MENU_AJOUTER:
				showDialog(AJOUTER_GROUPE_DIALOG_ID);
				return true;
		}
		return false;
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
						Toast.makeText(ListFavoris.this, getString(R.string.groupeObligatoire), Toast.LENGTH_LONG)
								.show();
						return;
					}
					GroupeFavori groupeFavori = new GroupeFavori();
					groupeFavori.name = value;
					if (!TransportsRennesApplication.getDataBaseHelper().select(groupeFavori).isEmpty()
							|| value.equals(getString(R.string.all))) {
						Toast.makeText(ListFavoris.this, getString(R.string.groupeExistant), Toast.LENGTH_LONG).show();
						return;
					}
					TransportsRennesApplication.getDataBaseHelper().insert(groupeFavori);
					startActivity(new Intent(ListFavoris.this, TabFavoris.class));
					ListFavoris.this.finish();
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
