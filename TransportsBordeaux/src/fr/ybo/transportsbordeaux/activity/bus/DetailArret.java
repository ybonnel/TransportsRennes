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

import java.util.Calendar;

import android.os.Bundle;
import android.widget.ListAdapter;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

import fr.ybo.transportsbordeaux.R;
import fr.ybo.transportsbordeaux.activity.alerts.ListAlertsForOneLine;
import fr.ybo.transportsbordeaux.adapters.bus.DetailArretAdapter;
import fr.ybo.transportscommun.activity.bus.AbstractDetailArret;
import fr.ybo.transportscommun.activity.commun.BaseActivity.BaseFragmentActivity;
import fr.ybo.transportscommun.activity.commun.BaseActivity.BaseListActivity;
import fr.ybo.transportscommun.donnees.modele.Horaire;

/**
 * Activitée permettant d'afficher les détails d'une station.
 *
 * @author ybonnel
 */
public class DetailArret extends AbstractDetailArret {

	@Override
	protected ListAdapter construireAdapter() {
        int now = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
        return new DetailArretAdapter(getApplicationContext(), Horaire.getAllHorairesAsList(favori.ligneId,
                favori.arretId, calendar), now, isToday());
    }

	@Override
	protected int getLayout() {
		return R.layout.detailarret;
	}

	@Override
	protected void setupActionBar() {
		getActivityHelper().setupActionBar(R.menu.detailarret_menu_items, R.menu.holo_detailarret_menu_items);
	}

	@Override
	protected Class<? extends BaseListActivity> getDetailTrajetClass() {
		return DetailTrajet.class;
	}

	@Override
	protected Class<? extends BaseFragmentActivity> getListAlertsForOneLineClass() {
		return ListAlertsForOneLine.class;
	}

	@Override
	protected int getLayoutArretGps() {
		return R.layout.arretgps;
	}

	@Override
	protected Class<?> getRawClass() {
		return R.raw.class;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Look up the AdView as a resource and load a request.
		((AdView) this.findViewById(R.id.adView)).loadAd(new AdRequest());
	}
}
