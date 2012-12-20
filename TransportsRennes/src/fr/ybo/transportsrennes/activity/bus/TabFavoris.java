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
import android.support.v4.app.ListFragment;
import fr.ybo.database.DataBaseException;
import fr.ybo.database.DataBaseHelper;
import fr.ybo.transportscommun.activity.bus.AbstractTabFavoris;
import fr.ybo.transportscommun.activity.commun.BaseActivity.BaseFragmentActivity;
import fr.ybo.transportscommun.donnees.manager.gtfs.GestionZipKeolis;
import fr.ybo.transportscommun.donnees.modele.DernierMiseAJour;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.activity.loading.LoadingActivity;
import fr.ybo.transportsrennes.application.TransportsRennesApplication;
import fr.ybo.transportsrennes.fragments.bus.ListFavoris;

public class TabFavoris extends AbstractTabFavoris {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		verifierUpgrade();
	}
	

	private void verifierUpgrade() {
		DataBaseHelper dataBaseHelper = TransportsRennesApplication.getDataBaseHelper();
		DernierMiseAJour dernierMiseAJour = null;
		try {
			dernierMiseAJour = dataBaseHelper.selectSingle(new DernierMiseAJour());
		} catch (DataBaseException exception) {
			dataBaseHelper.deleteAll(DernierMiseAJour.class);
		}
		Date dateDernierFichierKeolis = GestionZipKeolis.getLastUpdate(getResources(), R.raw.last_update);
		if (dernierMiseAJour == null) {
			upgradeDatabase();
		} else if (dernierMiseAJour.derniereMiseAJour == null
				|| dateDernierFichierKeolis.after(dernierMiseAJour.derniereMiseAJour)) {
			showDialog(DIALOG_UPGRADE);
		}
	}


	private void upgradeDatabase() {
		Intent intent = new Intent(this, LoadingActivity.class);
		intent.putExtra("operation", LoadingActivity.OPERATION_UPGRADE_DATABASE);
		startActivity(intent);
	}
	
	private static final int DIALOG_UPGRADE = 2;

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == DIALOG_UPGRADE) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(getString(R.string.majDispo));
			builder.setCancelable(false);
			builder.setPositiveButton(getString(R.string.oui), new Dialog.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
					upgradeDatabase();
				}
			});
			builder.setNegativeButton(getString(R.string.non), new Dialog.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
			return builder.create();
		}
		return super.onCreateDialog(id);
	}
	

	@Override
	protected int getLayout() {
		return R.layout.tabfavoris;
	}

	@Override
	protected void setupActionBar() {
		getActivityHelper().setupActionBar(R.menu.bus_favoris_menu_items, R.menu.holo_bus_favoris_menu_items);
	}

	@Override
	protected Class<? extends BaseFragmentActivity> getListFavorisForNoGroupClass() {
		return ListFavorisForNoGroup.class;
	}

	@Override
	protected Class<? extends ListFragment> getListFavoris() {
		return ListFavoris.class;
	}

	@Override
	protected void loadFavoris() {
		Intent intent = new Intent(this, LoadingActivity.class);
		intent.putExtra("operation", LoadingActivity.OPERATION_LOAD_FAVORIS);
		startActivity(intent);
	}
}
