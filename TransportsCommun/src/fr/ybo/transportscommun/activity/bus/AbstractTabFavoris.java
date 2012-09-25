package fr.ybo.transportscommun.activity.bus;

import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import fr.ybo.transportscommun.AbstractTransportsApplication;
import fr.ybo.transportscommun.R;
import fr.ybo.transportscommun.activity.commun.BaseActivity.BaseFragmentActivity;
import fr.ybo.transportscommun.activity.commun.BaseActivity.BaseTabFragmentActivity;
import fr.ybo.transportscommun.donnees.manager.FavorisManager;
import fr.ybo.transportscommun.donnees.modele.ArretFavori;
import fr.ybo.transportscommun.donnees.modele.GroupeFavori;

public abstract class AbstractTabFavoris extends BaseTabFragmentActivity {

	protected abstract int getLayout();

	protected abstract void setupActionBar();

	protected abstract Class<? extends BaseFragmentActivity> getListFavorisForNoGroupClass();

	protected abstract Class<? extends ListFragment> getListFavoris();

	protected abstract void loadFavoris();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.ybo.transportscommun.activity.commun.CapptainFragmentActivity#onResume
	 * ()
	 */
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayout());
		setupActionBar();
		List<GroupeFavori> groupes = AbstractTransportsApplication.getDataBaseHelper().selectAll(GroupeFavori.class);
		if (groupes.isEmpty()) {
			Intent intent = new Intent(this, getListFavorisForNoGroupClass());
			startActivity(intent);
			finish();
			return;
		}

		configureTabs();

		addTab("all", getString(R.string.all), getListFavoris());
		for (GroupeFavori groupe : groupes) {
			Bundle args = new Bundle();
			args.putString("groupe", groupe.name);
			addTab(groupe.name, groupe.name, getListFavoris(), args);
		}

		setCurrentTab(savedInstanceState);
		if (FavorisManager.getInstance().hasFavorisToLoad()) {
			loadFavoris();
		}
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
		if (item.getItemId() == R.id.menu_export) {
			FavorisManager.getInstance().export(this);
		} else if (item.getItemId() == R.id.menu_import) {
			FavorisManager.getInstance().load(this);
			startActivity(new Intent(this, getClass()));
			finish();
		} else if (item.getItemId() == MENU_SUPPRIMER) {
			ArretFavori arretFavori = new ArretFavori();
			arretFavori.groupe = getCurrentTab();
			for (ArretFavori favori : AbstractTransportsApplication.getDataBaseHelper().select(arretFavori)) {
				favori.groupe = "";
				AbstractTransportsApplication.getDataBaseHelper().update(favori);
			}
			GroupeFavori groupeFavori = new GroupeFavori();
			groupeFavori.name = getCurrentTab();
			AbstractTransportsApplication.getDataBaseHelper().delete(groupeFavori);
			startActivity(new Intent(this, getClass()));
			finish();
			return true;
		} else if (item.getItemId() == MENU_AJOUTER) {
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
					Toast.makeText(AbstractTabFavoris.this, getString(R.string.groupeObligatoire), Toast.LENGTH_LONG)
							.show();
					return;
				}
				GroupeFavori groupeFavori = new GroupeFavori();
				groupeFavori.name = value;
				if (!AbstractTransportsApplication.getDataBaseHelper().select(groupeFavori).isEmpty()
						|| value.equals(getString(R.string.all))) {
					Toast.makeText(AbstractTabFavoris.this, getString(R.string.groupeExistant), Toast.LENGTH_LONG)
							.show();
					return;
				}
				AbstractTransportsApplication.getDataBaseHelper().insert(groupeFavori);
				startActivity(new Intent(AbstractTabFavoris.this, AbstractTabFavoris.this.getClass()));
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
