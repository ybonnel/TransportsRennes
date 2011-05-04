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

package fr.ybo.transportsbordeaux;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import fr.ybo.transportsbordeaux.activity.MenuAccueil;
import fr.ybo.transportsbordeaux.adapters.DetailArretAdapter;
import fr.ybo.transportsbordeaux.modele.ArretFavori;
import fr.ybo.transportsbordeaux.modele.Ligne;
import fr.ybo.transportsbordeaux.tbc.Horaire;
import fr.ybo.transportsbordeaux.util.IconeLigne;
import fr.ybo.transportsbordeaux.util.LogYbo;

/**
 * Activitée permettant d'afficher les détails d'une station.
 *
 * @author ybonnel
 */
public class DetailArret extends MenuAccueil.ListActivity {

	private final static LogYbo LOG_YBO = new LogYbo(DetailArret.class);

	private boolean prochainArrets = true;

	private Calendar calendar = Calendar.getInstance();
	private int now = 0;

	private ArretFavori favori;

	private List<Horaire> horaires = new ArrayList<Horaire>();
	private List<Horaire> horairesJournee = new ArrayList<Horaire>();

	private void recuperationDonneesIntent() {
		favori = (ArretFavori) getIntent().getExtras().getSerializable("favori");
		if (favori == null) {
			favori = new ArretFavori();
			favori.arretId = getIntent().getExtras().getString("idArret");
			favori.nomArret = getIntent().getExtras().getString("nomArret");
			favori.direction = getIntent().getExtras().getString("direction");
			favori.macroDirection = getIntent().getExtras().getInt("macroDirection");
			Ligne ligne = (Ligne) getIntent().getExtras().getSerializable("ligne");
			if (ligne == null) {
				finish();
				return;
			}
			favori.ligneId = ligne.id;
			favori.nomCourt = ligne.nomCourt;
			favori.nomLong = ligne.nomLong;
		}
	}

	private void gestionViewsTitle() {
		((TextView) findViewById(R.id.nomLong)).setText(favori.nomLong);
		((ImageView) findViewById(R.id.iconeLigne)).setImageResource(IconeLigne.getIconeResource(favori.nomCourt));
		((TextView) findViewById(R.id.detailArret_nomArret)).setText(favori.nomArret + ' ' + getString(R.string.vers) + ' ' + favori.direction);
	}

	private void recupererHoraires(final boolean changementJournee) {
		new AsyncTask<Void, Void, Void>() {

			private boolean erreur;

			private ProgressDialog myProgressDialog;

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				myProgressDialog = ProgressDialog.show(DetailArret.this, "", getString(R.string.recuperationHoraires),
						true);
			}

			@Override
			protected Void doInBackground(Void... pParams) {
				try {
					if (changementJournee) {
						horairesJournee.clear();
						horairesJournee.addAll(getHorairesTriees());
					}
					if (prochainArrets) {
						recupererProchainsDeparts();
					} else {
						recupererHorairesAllDeparts();
					}
				} catch (Exception exception) {
					LOG_YBO.erreur("Erreur dans la récupération des horaires", exception);
					erreur = true;
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				((BaseAdapter) getListAdapter()).notifyDataSetChanged();
				myProgressDialog.dismiss();
				if (erreur) {
					Toast toast = Toast.makeText(getApplicationContext(),
							getString(R.string.erreur_recuperationHoraires),
							Toast.LENGTH_LONG);
					toast.show();
					finish();
				}
				super.onPostExecute(result);
			}
		}.execute();
	}
	
	private List<Horaire> getHorairesTriees() {
		List<Horaire> horairesTbc = Horaire.getHoraires(calendar.getTime(), favori);
		Collections.sort(horairesTbc, new Comparator<Horaire>() {
			@Override
			public int compare(Horaire pObject1, Horaire pObject2) {
				return pObject1.horaire.compareTo(pObject2.horaire);
			}});
		return horairesTbc;
	}

	private void recupererHorairesAllDeparts() {
		horaires.clear();
		horaires.addAll(horairesJournee);
	}

	private void recupererProchainsDeparts() {
		int now = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
		horaires.clear();
		for (Horaire horaire : horairesJournee) {
			if (horaire.horaire > now) {
				horaires.add(horaire);
			}
		}
	}

	private Ligne myLigne;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		calendar = Calendar.getInstance();
		now = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
		setContentView(R.layout.detailarret);
		recuperationDonneesIntent();
		if (favori.ligneId == null) {
			return;
		}
		gestionViewsTitle();
		myLigne = new Ligne();
		myLigne.id = favori.ligneId;
		myLigne = TransportsBordeauxApplication.getDataBaseHelper().selectSingle(myLigne);
		setListAdapter(new DetailArretAdapter(getApplicationContext(), horaires, now));
		ListView lv = getListView();
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				/*Adapter arretAdapter = ((AdapterView<ListAdapter>) adapterView).getAdapter();
				Cursor cursor = (Cursor) arretAdapter.getItem(position);
				Intent intent = new Intent(DetailArret.this, DetailTrajet.class);
				intent.putExtra("trajetId", cursor.getInt(cursor.getColumnIndex("trajetId")));
				intent.putExtra("sequence", cursor.getInt(cursor.getColumnIndex("sequence")));
				startActivity(intent);*/
			}
		});
		lv.setTextFilterEnabled(true);
		recupererHoraires(true);
	}

	private static final int GROUP_ID = 0;
	private static final int MENU_ALL_STOPS = Menu.FIRST;
	private static final int MENU_SELECT_DAY = MENU_ALL_STOPS + 1;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(GROUP_ID, MENU_ALL_STOPS, Menu.NONE, R.string.menu_prochainArrets).setIcon(android.R.drawable.ic_menu_view);
		menu.add(GROUP_ID, MENU_SELECT_DAY, Menu.NONE, R.string.menu_selectDay).setIcon(android.R.drawable.ic_menu_month);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		menu.findItem(MENU_ALL_STOPS).setTitle(prochainArrets ? R.string.menu_allArrets : R.string.menu_prochainArrets);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		switch (item.getItemId()) {
			case MENU_ALL_STOPS:
				prochainArrets = !prochainArrets;
				recupererHoraires(false);
				return true;
			case MENU_SELECT_DAY:
				showDialog(DATE_DIALOG_ID);
				return true;
		}
		return false;
	}

	private static final int DATE_DIALOG_ID = 0;

	private final DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			calendar.set(Calendar.YEAR, year);
			calendar.set(Calendar.MONTH, monthOfYear);
			calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			recupererHoraires(true);
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == DATE_DIALOG_ID) {
			return new DatePickerDialog(this, mDateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
					calendar.get(Calendar.DAY_OF_MONTH));
		}
		return null;
	}

}
