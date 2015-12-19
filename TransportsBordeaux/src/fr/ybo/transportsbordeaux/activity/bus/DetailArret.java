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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.google.ads.Ad;
import com.google.ads.AdRequest;

import fr.ybo.transportsbordeaux.R;
import fr.ybo.transportsbordeaux.activity.alerts.ListAlertsForOneLine;
import fr.ybo.transportsbordeaux.adapters.bus.DetailArretAdapter;
import fr.ybo.transportsbordeaux.application.TransportsBordeauxApplication;
import fr.ybo.transportscommun.activity.bus.AbstractDetailArret;
import fr.ybo.transportscommun.activity.commun.BaseActivity.BaseFragmentActivity;
import fr.ybo.transportscommun.activity.commun.BaseActivity.BaseListActivity;
import fr.ybo.transportscommun.donnees.modele.Calendrier;
import fr.ybo.transportscommun.donnees.modele.DetailArretConteneur;
import fr.ybo.transportscommun.donnees.modele.Horaire;

/**
 * Activitée permettant d'afficher les détails d'une station.
 *
 * @author ybonnel
 */
public class DetailArret extends AbstractDetailArret {

	@Override
	protected ListAdapter construireAdapter() {
        final int now = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
		final List<DetailArretConteneur> horaires = Horaire.getAllHorairesAsList(favori.ligneId, favori.arretId, calendar, null);

		
		if (horaires.isEmpty()) {
			String maxCalendrier = "00000000";
			for (final Calendrier calendrier : TransportsBordeauxApplication.getDataBaseHelper().selectAll(Calendrier.class)) {
				if (calendrier.dateFin.compareTo(maxCalendrier) > 0) {
					maxCalendrier = calendrier.dateFin;
				}
			}
			final String calendrierCourant = new SimpleDateFormat("yyyyMMdd").format(calendar.getTime());
			if (maxCalendrier.compareTo(calendrierCourant) < 0) {
				((TextView) findViewById(android.R.id.empty)).setText(R.string.messageTbcEnRetard);
			}
		}

        final Calendar veille = Calendar.getInstance();
        veille.setTime(calendar.getTime());
        veille.add(Calendar.DAY_OF_MONTH, -1);
        
        for (final DetailArretConteneur horaireVeille : Horaire.getAllHorairesAsList(favori.ligneId, favori.arretId, veille, null)) {
        	if (horaireVeille.getHoraire() > 24*60) {
        		horaireVeille.setHoraire(horaireVeille.getHoraire() - 24*60);
        		horaires.add(horaireVeille);
        	}
        }
        
        Collections.sort(horaires, new DetailArretConteneurComparator());
		
		
		return new DetailArretAdapter(getApplicationContext(), horaires, now, isToday(), favori.direction);
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
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Look up the AdView as a resource and load a request.
		((Ad) findViewById(R.id.adView)).loadAd(new AdRequest());
	}

	private final Set<Integer> secondsToUpdate = new HashSet<Integer>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.ybo.transportscommun.activity.bus.AbstractDetailArret#getSecondsToUpdate
	 * ()
	 */
	@Override
	protected Set<Integer> getSecondsToUpdate() {
		return secondsToUpdate;
	}

	private static class DetailArretConteneurComparator implements Comparator<DetailArretConteneur> {

		@Override
        public int compare(final DetailArretConteneur lhs,
                final DetailArretConteneur rhs) {
            return lhs.getHoraire() < rhs.getHoraire() ? -1 : lhs.getHoraire() == rhs.getHoraire() ? 0 : 1;
        }
	}
}
