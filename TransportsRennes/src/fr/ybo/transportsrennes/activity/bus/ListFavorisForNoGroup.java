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

import java.util.Date;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import fr.ybo.database.DataBaseException;
import fr.ybo.database.DataBaseHelper;
import fr.ybo.transportscommun.activity.commun.BaseActivity.BaseFragmentActivity;
import fr.ybo.transportscommun.donnees.manager.FavorisManager;
import fr.ybo.transportscommun.donnees.manager.gtfs.GestionZipKeolis;
import fr.ybo.transportscommun.donnees.modele.DernierMiseAJour;
import fr.ybo.transportscommun.donnees.modele.GroupeFavori;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.activity.loading.LoadingActivity;
import fr.ybo.transportsrennes.application.TransportsRennesApplication;

/**
 * @author ybonnel
 */
public class ListFavorisForNoGroup extends BaseFragmentActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listfavoris);
		getActivityHelper().setupActionBar(R.menu.bus_favoris_menu_items, R.menu.holo_bus_favoris_menu_items);
		if (FavorisManager.hasFavorisToLoad()) {
			final Intent intent = new Intent(this, LoadingActivity.class);
			intent.putExtra("operation", LoadingActivity.OPERATION_LOAD_FAVORIS);
			startActivity(intent);
		} else {
			verifierUpgrade();
		}
    }

    private static final int GROUP_ID = 0;
    private static final int MENU_AJOUTER = 1;

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        super.onCreateOptionsMenu(menu);
        final MenuItem item = menu.add(GROUP_ID, MENU_AJOUTER, Menu.NONE, R.string.ajouterGroupe);
        item.setIcon(android.R.drawable.ic_menu_add);
        return true;
    }

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
			case R.id.menu_export:
				FavorisManager.getInstance().export(this);
				break;
			case R.id.menu_import:
				FavorisManager.getInstance().load(this);
				startActivity(new Intent(this, TabFavoris.class));
				finish();
				break;
			case MENU_AJOUTER:
				showDialog(AJOUTER_GROUPE_DIALOG_ID);
				return true;
		}
		return false;
	}

	private static final int AJOUTER_GROUPE_DIALOG_ID = 0;

	@Override
	protected Dialog onCreateDialog(final int id) {
		if (id == AJOUTER_GROUPE_DIALOG_ID) {
			final AlertDialog.Builder alert = new AlertDialog.Builder(this);
			final TextView input = new EditText(this);
			alert.setView(input);
			alert.setPositiveButton(getString(R.string.ajouter), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(final DialogInterface dialog, final int whichButton) {
					final String value = input.getText().toString().trim();
					if (value.isEmpty()) {
						Toast.makeText(ListFavorisForNoGroup.this, getString(R.string.groupeObligatoire),
								Toast.LENGTH_LONG).show();
						return;
					}
					final GroupeFavori groupeFavori = new GroupeFavori();
					groupeFavori.name = value;
					if (!TransportsRennesApplication.getDataBaseHelper().select(groupeFavori).isEmpty()
							|| value.equals(getString(R.string.all))) {
						Toast.makeText(ListFavorisForNoGroup.this, getString(R.string.groupeExistant),
								Toast.LENGTH_LONG).show();
						return;
					}
					TransportsRennesApplication.getDataBaseHelper().insert(groupeFavori);
					startActivity(new Intent(ListFavorisForNoGroup.this, TabFavoris.class));
					finish();
				}
			});

			alert.setNegativeButton(getString(R.string.annuler), new MyOnClickListener());
			return alert.create();
		}

		if (id == DIALOG_UPGRADE) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(getString(R.string.majDispo));
			builder.setCancelable(false);
			builder.setPositiveButton(getString(R.string.oui), new Dialog.OnClickListener() {
				@Override
				public void onClick(final DialogInterface dialog, final int id) {
					dialog.dismiss();
					upgradeDatabase();
				}
			});
			builder.setNegativeButton(getString(R.string.non), new MyOnClickListener());
			return builder.create();
		}
		return super.onCreateDialog(id);
	}

	

	private void verifierUpgrade() {
		final DataBaseHelper dataBaseHelper = TransportsRennesApplication.getDataBaseHelper();
		DernierMiseAJour dernierMiseAJour = null;
		try {
			dernierMiseAJour = dataBaseHelper.selectSingle(new DernierMiseAJour());
		} catch (final DataBaseException exception) {
			dataBaseHelper.deleteAll(DernierMiseAJour.class);
		}
		final Date dateDernierFichierKeolis = GestionZipKeolis.getLastUpdate(getResources(), R.raw.last_update);
		if (dernierMiseAJour == null) {
			upgradeDatabase();
		} else if (dernierMiseAJour.derniereMiseAJour == null
				|| dateDernierFichierKeolis.after(dernierMiseAJour.derniereMiseAJour)) {
			showDialog(DIALOG_UPGRADE);
		}
	}


	private void upgradeDatabase() {
		final Intent intent = new Intent(this, LoadingActivity.class);
		intent.putExtra("operation", LoadingActivity.OPERATION_UPGRADE_DATABASE);
		startActivity(intent);
	}
	
	private static final int DIALOG_UPGRADE = 2;

	private static class MyOnClickListener implements Dialog.OnClickListener {
		@Override
        public void onClick(final DialogInterface dialog, final int id) {
            dialog.cancel();
        }
	}

}
