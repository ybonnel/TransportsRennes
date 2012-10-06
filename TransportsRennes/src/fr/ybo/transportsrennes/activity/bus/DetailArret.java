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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.Toast;
import fr.ybo.transportscommun.activity.bus.AbstractDetailArret;
import fr.ybo.transportscommun.activity.commun.BaseActivity.BaseFragmentActivity;
import fr.ybo.transportscommun.activity.commun.BaseActivity.BaseListActivity;
import fr.ybo.transportscommun.donnees.modele.DetailArretConteneur;
import fr.ybo.transportscommun.donnees.modele.Horaire;
import fr.ybo.transportscommun.util.ErreurReseau;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.activity.alerts.ListAlertsForOneLine;
import fr.ybo.transportsrennes.adapters.bus.DetailArretAdapter;
import fr.ybo.transportsrennes.keolis.Keolis;
import fr.ybo.transportsrennes.keolis.modele.bus.Departure;

/**
 * Activitée permettant d'afficher les détails d'une station.
 *
 * @author ybonnel
 */
public class DetailArret extends AbstractDetailArret {

	private LinearLayout infoBar;

	private List<Departure> departures = new ArrayList<Departure>();

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
			new AsyncTask<Void, Void, List<Departure>>() {

				protected void onPreExecute() {
					infoBar.setVisibility(View.VISIBLE);
				};

				@Override
				protected List<Departure> doInBackground(Void... params) {
					try {
						return Keolis.getInstance().getDepartues(favori.ligneId, favori.arretId, favori.macroDirection);
					} catch (ErreurReseau e) {
						e.printStackTrace();
						return null;
					}
				}

				protected void onPostExecute(List<Departure> departuresTemp) {
					if (departuresTemp == null) {
						Toast.makeText(getApplicationContext(), R.string.erreurReseau, Toast.LENGTH_LONG).show();
					} else {
						synchronized (departures) {
							departures.clear();
							for (Departure departure : departuresTemp) {
								if (departure.isAccurate()) {
									departures.add(departure);
								}
							}
						}
						updateTime.update(Calendar.getInstance());
					}
					infoBar.setVisibility(View.INVISIBLE);
				};

			}.execute();
		}
	}

	@Override
    protected ListAdapter construireAdapter() {
		int now = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
		List<DetailArretConteneur> prochainsDeparts =
				Horaire.getAllHorairesAsList(favori.ligneId, favori.arretId, favori.macroDirection, calendar);
		if (isToday()) {
			synchronized (departures) {
				// Pour chaque departure
				for (Departure departure : departures) {
					System.out.println("Departure = " + departure);
					System.out.println("departure.getHoraire : " + departure.getHoraire());

					int diffCourante = -1;
					DetailArretConteneur departProche = null;
					// Trouve le prochain depart le plus près.
					for (DetailArretConteneur depart : prochainsDeparts) {
						System.out.println("DiffCourante : " + diffCourante);
						System.out.println("Depart : " + depart.getHoraire());

						if (diffCourante == -1
								|| (Math.abs(departure.getHoraire() - depart.getHoraire()) < diffCourante)) {
							diffCourante = Math.abs(departure.getHoraire() - depart.getHoraire());
							departProche = depart;
						}
					}
					if (departProche != null) {
						departProche.setAccurate(true);
						departProche.setHoraire(departure.getHoraire());
					}
				}
			}
		}
		return new DetailArretAdapter(getApplicationContext(), prochainsDeparts, now, isToday(), favori.direction);
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

}
