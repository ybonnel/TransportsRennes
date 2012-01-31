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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import fr.ybo.transportscommun.activity.commun.BaseActivity.BaseTabFragmentActivity;
import fr.ybo.transportscommun.activity.commun.BaseActivity.OnFragmentChange;
import fr.ybo.transportscommun.activity.commun.ChangeIconActionBar;
import fr.ybo.transportscommun.donnees.modele.Ligne;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.application.TransportsRennesApplication;
import fr.ybo.transportsrennes.fragments.bus.ListArretFragment;

/**
 * Liste des arrêts d'une ligne de bus.
 * 
 * @author ybonnel
 */
public class ListArret extends BaseTabFragmentActivity implements ChangeIconActionBar {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(fr.ybo.transportsrennes.R.layout.listearrets);
		getActivityHelper().setupActionBar(R.menu.listarrets_menu_items, R.menu.holo_listarrets_menu_items);
		configureTabs();
		for (Ligne ligne : TransportsRennesApplication.getDataBaseHelper().selectAll(Ligne.class)) {
			Bundle args = new Bundle();
			args.putSerializable("ligne", ligne);
			addTab(ligne.id, ligne.nomCourt, ListArretFragment.class, args);
		}

		setOnFragmentChange(new OnFragmentChange() {

			@Override
			public void onFragmentChanged(Fragment currentFragment) {
				if (!((ListArretFragment) currentFragment).isConstruct()
						|| orderDirection != ((ListArretFragment) currentFragment).isLastOrderDirection()) {
					((ListArretFragment) currentFragment).construireListe();
				}
			}
		});
		Ligne myLigne = (Ligne) getIntent().getExtras().getSerializable("ligne");
		if (myLigne == null) {
			myLigne = new Ligne();
			myLigne.id = getIntent().getStringExtra("ligneId");
			myLigne = TransportsRennesApplication.getDataBaseHelper().selectSingle(myLigne);
		}
		if (savedInstanceState != null) {
			setCurrentTab(savedInstanceState);
		} else {
			setCurrentTab(myLigne.id);
		}

	}

	public String getCurrrentTabTag() {
		return getCurrentTab();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
	}

	private boolean orderDirection = true;

	public boolean isOrderDirection() {
		return orderDirection;
	}

	@Override
	public void changeIconActionBar(ImageButton imageButton) {
		if (imageButton.getId() == R.id.menu_order) {
			imageButton.setImageResource(orderDirection ? android.R.drawable.ic_menu_sort_alphabetically
					: android.R.drawable.ic_menu_sort_by_size);
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		if (menu.findItem(R.id.menu_order) != null) {
			menu.findItem(R.id.menu_order).setTitle(
					orderDirection ? R.string.menu_orderByName : R.string.menu_orderBySequence);
			menu.findItem(R.id.menu_order).setIcon(
					orderDirection ? android.R.drawable.ic_menu_sort_alphabetically
							: android.R.drawable.ic_menu_sort_by_size);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		if (item.getItemId() == R.id.menu_order) {
			orderDirection = !orderDirection;
			ListArretFragment fragment = (ListArretFragment) getCurrentFragment();
			fragment.construireListe();
			getActivityHelper().invalidateOptionsMenu();
			return true;
		} else if (item.getItemId() == R.id.menu_google_map) {
			Intent intent = new Intent(ListArret.this, ArretsOnMap.class);
			ListArretFragment fragment = (ListArretFragment) getCurrentFragment();
			intent.putExtra("ligne", fragment.getMyLigne());
			if (fragment.getCurrentDirection() != null) {
				intent.putExtra("direction", fragment.getCurrentDirection());
			}
			startActivity(intent);
		}
		return false;
	}
}
