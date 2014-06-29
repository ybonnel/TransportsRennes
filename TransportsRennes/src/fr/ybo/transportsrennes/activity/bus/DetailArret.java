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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import fr.ybo.transportscommun.activity.bus.AbstractDetailArret;
import fr.ybo.transportscommun.activity.commun.BaseActivity.BaseFragmentActivity;
import fr.ybo.transportscommun.activity.commun.BaseActivity.BaseListActivity;
import fr.ybo.transportscommun.activity.commun.Refreshable;
import fr.ybo.transportscommun.donnees.modele.Calendrier;
import fr.ybo.transportscommun.donnees.modele.DetailArretConteneur;
import fr.ybo.transportscommun.donnees.modele.Horaire;
import fr.ybo.transportscommun.util.ErreurReseau;
import fr.ybo.transportscommun.util.LogYbo;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.activity.alerts.ListAlertsForOneLine;
import fr.ybo.transportsrennes.adapters.bus.DetailArretAdapter;
import fr.ybo.transportsrennes.application.TransportsRennesApplication;
import fr.ybo.transportsrennes.keolis.Keolis;
import fr.ybo.transportsrennes.keolis.modele.bus.Departure;
import fr.ybo.transportsrennes.keolis.modele.bus.ResultDeparture;

/**
 * Activitée permettant d'afficher les détails d'une station.
 *
 * @author ybonnel
 */
public class DetailArret extends AbstractDetailArret implements Refreshable {

	private final static LogYbo LOG = new LogYbo(DetailArret.class);

	private LinearLayout infoBar;

	private List<Departure> departures = new ArrayList<Departure>();

	private class GetDeparture extends AsyncTask<Void, Void, ResultDeparture> {
		protected void onPreExecute() {
			infoBar.setVisibility(View.VISIBLE);
		};

		@Override
		protected ResultDeparture doInBackground(Void... params) {
			try {
				return Keolis.getInstance().getDepartues(favori);
			} catch (ErreurReseau e) {
				e.printStackTrace();
				return null;
			}
		}

		protected void onPostExecute(ResultDeparture result) {
			if (result == null) {
				Toast.makeText(getApplicationContext(), R.string.erreurReseau, Toast.LENGTH_LONG).show();
			} else {
				Set<Integer> secondsToUpdateTmp = new HashSet<Integer>();
				synchronized (departures) {
					departures.clear();
					for (Departure departure : result.getDepartures()) {
						LOG.debug(departure.toString());
						departures.add(departure);
						secondsToUpdateTmp.add(departure.getTime().get(Calendar.SECOND));
					}
				}

				synchronized (secondsToUpdate) {
					secondsToUpdate.clear();
					secondsToUpdate.addAll(secondsToUpdateTmp);
				}
				updateTime.update(Calendar.getInstance());
				long apiTime = result.getApiTime().getTimeInMillis();
				long currentTime = Calendar.getInstance().getTimeInMillis();
				long diffMs = Math.abs(apiTime - currentTime);
				long diffSeconds = diffMs / 1000;
				if (diffSeconds > 30) {
					Toast.makeText(getApplicationContext(),
							getResources().getString(R.string.diffSecondsToHigh, diffSeconds), Toast.LENGTH_LONG)
							.show();
				}
			}
			infoBar.setVisibility(View.INVISIBLE);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.ybo.transportscommun.activity.bus.AbstractDetailArret#onCreate(android
	 * .os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isToday()) {
			infoBar = (LinearLayout) findViewById(R.id.infobar);
			new GetDeparture().execute();
		}
	}

	@Override
	protected ListAdapter construireAdapter() {
        int now = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
        int secondesNow = calendar.get(Calendar.SECOND);
		List<DetailArretConteneur> horaires = Horaire.getAllHorairesAsList(favori.ligneId, favori.arretId, calendar, favori.macroDirection);

		
		if (horaires.isEmpty()) {
			String maxCalendrier = "00000000";
			for (Calendrier calendrier : TransportsRennesApplication.getDataBaseHelper().selectAll(Calendrier.class)) {
				if (calendrier.dateFin != null && calendrier.dateFin.compareTo(maxCalendrier) > 0) {
					maxCalendrier = calendrier.dateFin;
				}
			}
			String calendrierCourant = new SimpleDateFormat("yyyyMMdd").format(calendar.getTime());
			if (maxCalendrier.compareTo(calendrierCourant) < 0) {
				((TextView) findViewById(android.R.id.empty)).setText(R.string.messageStarEnRetard);
			}
		}

        Calendar veille = Calendar.getInstance();
        veille.setTime(calendar.getTime());
        veille.add(Calendar.DAY_OF_MONTH, -1);
        
        for (DetailArretConteneur horaireVeille : Horaire.getAllHorairesAsList(favori.ligneId, favori.arretId, veille, favori.macroDirection)) {
        	if (horaireVeille.getHoraire() > 24*60) {
        		horaireVeille.setHoraire(horaireVeille.getHoraire() - 24*60);
        		horaires.add(horaireVeille);
        	}
        }
        
        Collections.sort(horaires, new Comparator<DetailArretConteneur>(){

			@Override
			public int compare(DetailArretConteneur lhs,
					DetailArretConteneur rhs) {
				return (lhs.getHoraire() < rhs.getHoraire()) ? -1 : ((lhs.getHoraire() == rhs.getHoraire()) ? 0 : 1);
			}
		});
        
        if (isToday()) {
			synchronized (departures) {
				// Pour chaque departure
				for (Departure departure : departures) {
					int diffCourante = -1;
					DetailArretConteneur departProche = null;
					// Trouve le prochain depart le plus près.
					for (DetailArretConteneur depart : horaires) {
						if (diffCourante == -1
								|| (Math.abs(departure.getHoraire() - depart.getHoraire()) < diffCourante)) {
							diffCourante = Math.abs(departure.getHoraire() - depart.getHoraire());
							departProche = depart;
						}
					}
					if (departProche != null) {
						departProche.setAccurate(departure.isAccurate());
						departProche.setHoraire(departure.getHoraire());
						departProche.setSecondes(departure.getTime().get(Calendar.SECOND));
					}
				}
			}
		}
		
		
		return new DetailArretAdapter(getApplicationContext(), horaires, now, isToday(), favori.direction, secondesNow);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.ybo.transportscommun.activity.commun.Refreshable#refresh()
	 */
	@Override
	public void refresh() {
		new GetDeparture().execute();
	}

	private Set<Integer> secondsToUpdate = new HashSet<Integer>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.ybo.transportscommun.activity.bus.AbstractDetailArret#getSecondsToUpdate
	 * ()
	 */
	@Override
	protected Set<Integer> getSecondsToUpdate() {
		synchronized (secondsToUpdate) {
			return new HashSet<Integer>(secondsToUpdate);
		}
	}

}
