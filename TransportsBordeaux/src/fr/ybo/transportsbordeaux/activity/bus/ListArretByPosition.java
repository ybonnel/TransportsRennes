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
package fr.ybo.transportsbordeaux.activity.bus;

import android.os.Bundle;
import android.widget.Toast;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

import fr.ybo.transportsbordeaux.R;
import fr.ybo.transportsbordeaux.activity.widgets.TransportsWidget11Configure;
import fr.ybo.transportsbordeaux.activity.widgets.TransportsWidget21Configure;
import fr.ybo.transportscommun.AbstractTransportsApplication;
import fr.ybo.transportscommun.activity.bus.AbstractDetailArret;
import fr.ybo.transportscommun.activity.bus.AbstractListArretByPosition;
import fr.ybo.transportscommun.donnees.modele.ArretFavori;

/**
 * Activité de type liste permettant de lister les arrêts de bus par distances
 * de la position actuelle.
 *
 * @author ybonnel
 */
public class ListArretByPosition extends AbstractListArretByPosition {

	@Override
	protected int getLayout() {
		return R.layout.listarretgps;
	}

	@Override
	protected void setupActionBar() {
		getActivityHelper().setupActionBar(R.menu.default_menu_items_with_search,
				R.menu.holo_default_menu_items_with_search);
	}

	@Override
	protected Class<? extends AbstractDetailArret> getDetailArret() {
		return DetailArret.class;
	}

	@Override
	protected void deleteFavori(ArretFavori favori) {
		if (TransportsWidget11Configure.isNotUsed(this, favori) && TransportsWidget21Configure.isNotUsed(this, favori)) {
			AbstractTransportsApplication.getDataBaseHelper().delete(favori);
		} else {
			Toast.makeText(this, getString(R.string.favoriUsedByWidget), Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected Class<?> getRawClass() {
		return R.raw.class;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Look up the AdView as a resource and load a request.
		((AdView) this.findViewById(R.id.adView)).loadAd(new AdRequest());
	}
}
