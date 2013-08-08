package fr.ybo.transportscommun.activity.bus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import fr.ybo.transportscommun.AbstractTransportsApplication;
import fr.ybo.transportscommun.R;
import fr.ybo.transportscommun.activity.commun.BaseActivity.BaseFragmentActivity;
import fr.ybo.transportscommun.activity.commun.BaseActivity.BaseListActivity;
import fr.ybo.transportscommun.adapters.bus.AbstractDetailArretAdapter;
import fr.ybo.transportscommun.donnees.manager.LigneInexistanteException;
import fr.ybo.transportscommun.donnees.manager.gtfs.UpdateDataBase;
import fr.ybo.transportscommun.donnees.modele.Arret;
import fr.ybo.transportscommun.donnees.modele.ArretFavori;
import fr.ybo.transportscommun.donnees.modele.DetailArretConteneur;
import fr.ybo.transportscommun.donnees.modele.Ligne;
import fr.ybo.transportscommun.donnees.modele.Notification;
import fr.ybo.transportscommun.util.IconeLigne;
import fr.ybo.transportscommun.util.NoSpaceLeftException;
import fr.ybo.transportscommun.util.TacheAvecProgressDialog;
import fr.ybo.transportscommun.util.UpdateTimeUtil;
import fr.ybo.transportscommun.util.UpdateTimeUtil.UpdateTime;

public abstract class AbstractDetailArret extends BaseListActivity {

	private static final double DISTANCE_RECHERCHE_METRE = 1000.0;
	private static final double DEGREE_LATITUDE_EN_METRES = 111192.62;
	private static final double DISTANCE_LAT_IN_DEGREE = DISTANCE_RECHERCHE_METRE / DEGREE_LATITUDE_EN_METRES;
	private static final double DEGREE_LONGITUDE_EN_METRES = 74452.10;
	private static final double DISTANCE_LNG_IN_DEGREE = DISTANCE_RECHERCHE_METRE / DEGREE_LONGITUDE_EN_METRES;
	private static final int DISTANCE_MAX_METRE = 151;

	protected boolean isToday() {
		return calendar.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)
				&& calendar.get(Calendar.MONTH) == today.get(Calendar.MONTH)
				&& calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR);
	}

	protected Calendar today = Calendar.getInstance();
	protected Calendar calendar = Calendar.getInstance();
	protected Calendar calendarLaVeille = Calendar.getInstance();

	protected ArretFavori favori;

	private void recuperationDonneesIntent() {
		favori = (ArretFavori) getIntent().getExtras().getSerializable("favori");
		if (favori == null) {
			favori = new ArretFavori();
			favori.arretId = getIntent().getExtras().getString("idArret");
			favori.nomArret = getIntent().getExtras().getString("nomArret");
			favori.direction = getIntent().getExtras().getString("direction");
			favori.macroDirection = getIntent().getExtras().getInt("macroDirection", 0);
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
		((TextView) findViewById(R.id.detailArret_nomArret)).setText(favori.nomArret + ' ' + getString(R.string.vers)
				+ ' ' + favori.direction);
	}

	protected abstract ListAdapter construireAdapter();

	protected abstract int getLayout();

	protected abstract void setupActionBar();

	protected abstract Class<? extends BaseListActivity> getDetailTrajetClass();

	protected abstract Class<? extends BaseFragmentActivity> getListAlertsForOneLineClass();

	protected abstract int getLayoutArretGps();

	protected abstract Class<?> getRawClass();

	private Ligne myLigne;
	private LayoutInflater mInflater;

	private UpdateTimeUtil updateTimeUtil;

	protected UpdateTime updateTime;

	private boolean firstUpdate = false;

	protected abstract Set<Integer> getSecondsToUpdate();


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mInflater = LayoutInflater.from(this);
		calendar = Calendar.getInstance();
		today = Calendar.getInstance();
		calendarLaVeille = Calendar.getInstance();
		calendarLaVeille.add(Calendar.DATE, -1);
		setContentView(getLayout());
		setupActionBar();
		recuperationDonneesIntent();
		if (favori.ligneId == null) {
			return;
		}
		gestionViewsTitle();
		myLigne = new Ligne();
		myLigne.id = favori.ligneId;
		myLigne = AbstractTransportsApplication.getDataBaseHelper().selectSingle(myLigne);
		if (myLigne == null) {
			Toast.makeText(this, R.string.erreurLigneInconue, Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		updateTime = new UpdateTime() {

			public void update(Calendar calendar) {
				if (isToday()) {
					AbstractDetailArret.this.calendar = Calendar.getInstance();
					today = Calendar.getInstance();
					calendarLaVeille = Calendar.getInstance();
					calendarLaVeille.add(Calendar.DATE, -1);
					setListAdapter(construireAdapter());
					if (getListAdapter().getCount() != 0) {
						setSelection(((AbstractDetailArretAdapter) getListAdapter()).getPositionToMove());
					}
					getListView().invalidate();
				}
			}

			@Override
			public boolean updateSecond() {
				return true;
			}

			@Override
			public Set<Integer> secondesToUpdate() {
				return AbstractDetailArret.this.getSecondsToUpdate();
			}
		};
		updateTimeUtil = new UpdateTimeUtil(updateTime, this);
		if (!myLigne.isChargee()) {
			chargerLigne();
		} else {
			setListAdapter(construireAdapter());
			if (getListAdapter().getCount() != 0) {
				setSelection(((AbstractDetailArretAdapter) getListAdapter()).getPositionToMove());
			}
			updateTimeUtil.start();
			firstUpdate = true;
		}
		ListView lv = getListView();
		lv.setFastScrollEnabled(true);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@SuppressWarnings({ "unchecked" })
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				Adapter arretAdapter = ((AdapterView<ListAdapter>) adapterView).getAdapter();
				DetailArretConteneur detailArretConteneur = (DetailArretConteneur) arretAdapter.getItem(position);
				Intent intent = new Intent(AbstractDetailArret.this, getDetailTrajetClass());
				intent.putExtra("trajetId", detailArretConteneur.getTrajetId());
				intent.putExtra("sequence", detailArretConteneur.getSequence());
				startActivity(intent);
			}
		});
		lv.setTextFilterEnabled(true);

		final ImageView correspondance = (ImageView) findViewById(R.id.imageCorrespondance);
		final LinearLayout detailCorrespondance = (LinearLayout) findViewById(R.id.detailCorrespondance);
		correspondance.setImageResource(R.drawable.arrow_right_float);
		detailCorrespondance.removeAllViews();
		detailCorrespondance.setVisibility(View.INVISIBLE);
		correspondance.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (detailCorrespondance.getVisibility() == View.VISIBLE) {
					correspondance.setImageResource(R.drawable.arrow_right_float);
					detailCorrespondance.removeAllViews();
					detailCorrespondance.setVisibility(View.INVISIBLE);
				} else {
					detailCorrespondance.setVisibility(View.VISIBLE);
					detailCorrespondance.removeAllViews();
					construireCorrespondance(detailCorrespondance);
					correspondance.setImageResource(R.drawable.arrow_down_float);
				}
			}
		});
		if (AbstractTransportsApplication.hasAlert(myLigne.nomCourt)) {
			findViewById(R.id.alerte).setVisibility(View.VISIBLE);
			findViewById(R.id.alerte).setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					Intent intent = new Intent(AbstractDetailArret.this, getListAlertsForOneLineClass());
					intent.putExtra("ligne", myLigne);
					startActivity(intent);
				}
			});
		} else {
			findViewById(R.id.alerte).setVisibility(View.GONE);
		}
		registerForContextMenu(lv);
	}

	@Override
	protected void onResume() {
		if (firstUpdate) {
			updateTimeUtil.start();
		}
		super.onResume();
	}

	@Override
	protected void onPause() {
		updateTimeUtil.stop();
		super.onPause();
	}

	private void construireCorrespondance(LinearLayout detailCorrespondance) {
		/* Recuperation de l'arretCourant */
		Arret arretCourant = new Arret();
		arretCourant.id = favori.arretId;
		arretCourant = AbstractTransportsApplication.getDataBaseHelper().selectSingle(arretCourant);
		Location locationArret = new Location("myProvider");
		locationArret.setLatitude(arretCourant.latitude);
		locationArret.setLongitude(arretCourant.longitude);

		/** Construction requête. */
		StringBuilder requete = new StringBuilder();
		requete.append("SELECT Arret.id as arretId, ArretRoute.ligneId as ligneId, Direction.direction as direction,");
		requete.append(" Arret.nom as arretNom, Arret.latitude as latitude, Arret.longitude as longitude,");
		requete.append(" Ligne.nomCourt as nomCourt, Ligne.nomLong as nomLong, ArretRoute.macroDirection as macroDirection ");
		requete.append("FROM Arret, ArretRoute, Direction, Ligne ");
		requete.append("WHERE Arret.id = ArretRoute.arretId and Direction.id = ArretRoute.directionId AND Ligne.id = ArretRoute.ligneId");
		requete.append(" AND Arret.latitude > :minLatitude AND Arret.latitude < :maxLatitude");
		requete.append(" AND Arret.longitude > :minLongitude AND Arret.longitude < :maxLongitude");

		/** Paramètres de la requête */
		double minLatitude = arretCourant.latitude - DISTANCE_LAT_IN_DEGREE;
		double maxLatitude = arretCourant.latitude + DISTANCE_LAT_IN_DEGREE;
		double minLongitude = arretCourant.longitude - DISTANCE_LNG_IN_DEGREE;
		double maxLongitude = arretCourant.longitude + DISTANCE_LNG_IN_DEGREE;
		List<String> selectionArgs = new ArrayList<String>(4);
		selectionArgs.add(String.valueOf(minLatitude));
		selectionArgs.add(String.valueOf(maxLatitude));
		selectionArgs.add(String.valueOf(minLongitude));
		selectionArgs.add(String.valueOf(maxLongitude));

		Cursor cursor = AbstractTransportsApplication.getDataBaseHelper().executeSelectQuery(requete.toString(),
				selectionArgs);

		/** Recuperation des index dans le cussor */
		int arretIdIndex = cursor.getColumnIndex("arretId");
		int ligneIdIndex = cursor.getColumnIndex("ligneId");
		int directionIndex = cursor.getColumnIndex("direction");
		int arretNomIndex = cursor.getColumnIndex("arretNom");
		int latitudeIndex = cursor.getColumnIndex("latitude");
		int longitudeIndex = cursor.getColumnIndex("longitude");
		int nomCourtIndex = cursor.getColumnIndex("nomCourt");
		int nomLongIndex = cursor.getColumnIndex("nomLong");
		int macroDirectionIndex = cursor.getColumnIndex("macroDirection");

		List<Arret> arrets = new ArrayList<Arret>(20);

		while (cursor.moveToNext()) {
			Arret arret = new Arret();
			arret.id = cursor.getString(arretIdIndex);
			arret.favori = new ArretFavori();
			arret.favori.arretId = arret.id;
			arret.favori.ligneId = cursor.getString(ligneIdIndex);
			arret.favori.direction = cursor.getString(directionIndex);
			arret.nom = cursor.getString(arretNomIndex);
			arret.favori.nomArret = arret.nom;
			arret.latitude = cursor.getDouble(latitudeIndex);
			arret.longitude = cursor.getDouble(longitudeIndex);
			arret.favori.nomCourt = cursor.getString(nomCourtIndex);
			arret.favori.nomLong = cursor.getString(nomLongIndex);
			arret.favori.macroDirection = cursor.getInt(macroDirectionIndex);
			if (!arret.id.equals(favori.arretId) || !arret.favori.ligneId.equals(favori.ligneId)) {
				arret.calculDistance(locationArret);
				if (arret.distance < DISTANCE_MAX_METRE) {
					arrets.add(arret);
				}
			}
		}
		cursor.close();

		Collections.sort(arrets, new Arret.ComparatorDistance());

		for (final Arret arret : arrets) {
			RelativeLayout relativeLayout = (RelativeLayout) mInflater.inflate(getLayoutArretGps(), null);
			ImageView iconeLigne = (ImageView) relativeLayout.findViewById(R.id.iconeLigne);
			iconeLigne.setImageResource(IconeLigne.getIconeResource(arret.favori.nomCourt));
			TextView arretDirection = (TextView) relativeLayout.findViewById(R.id.arretgps_direction);
			arretDirection.setText(arret.favori.direction);
			TextView nomArret = (TextView) relativeLayout.findViewById(R.id.arretgps_nomArret);
			nomArret.setText(arret.nom);
			TextView distance = (TextView) relativeLayout.findViewById(R.id.arretgps_distance);
			distance.setText(arret.formatDistance());
			relativeLayout.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					Intent intent = new Intent(AbstractDetailArret.this, AbstractDetailArret.this.getClass());
					intent.putExtra("favori", arret.favori);
					startActivity(intent);
				}
			});
			detailCorrespondance.addView(relativeLayout);
		}
	}

	private void chargerLigne() {
		new TacheAvecProgressDialog<Void, Void, Void>(this, getString(R.string.premierAccesLigne, myLigne.nomCourt),
				false) {

			private boolean erreurLigneNonTrouvee = false;
			private boolean erreurNoSpaceLeft = false;

			@Override
			protected void myDoBackground() {
				try {
					UpdateDataBase.chargeDetailLigne(getRawClass(), myLigne, getResources());
				} catch (LigneInexistanteException e) {
					erreurLigneNonTrouvee = true;
				} catch (NoSpaceLeftException e) {
					erreurNoSpaceLeft = true;
				}
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				if (erreurLigneNonTrouvee) {
					Toast.makeText(AbstractDetailArret.this, getString(R.string.erreurLigneInconue, myLigne.nomCourt),
							Toast.LENGTH_LONG).show();
					finish();
				} else if (erreurNoSpaceLeft) {
					Toast.makeText(AbstractDetailArret.this, R.string.erreurNoSpaceLeft, Toast.LENGTH_LONG).show();
					finish();
				} else {
					setListAdapter(construireAdapter());
					if (getListAdapter().getCount() != 0) {
						setSelection(((AbstractDetailArretAdapter) getListAdapter()).getPositionToMove());
					}
					getListView().invalidate();
					updateTimeUtil.start();
					firstUpdate = true;
				}
			}

		}.execute((Void) null);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		if (item.getItemId() == R.id.menu_google_map) {
			Arret arret = new Arret();
			arret.id = favori.arretId;
			arret = AbstractTransportsApplication.getDataBaseHelper().selectSingle(arret);
			String lat = Double.toString(arret.getLatitude());
			String lon = Double.toString(arret.getLongitude());
			Uri uri = Uri.parse("geo:" + lat + ',' + lon + "?q=" + lat + "," + lon);
			try {
				startActivity(new Intent(Intent.ACTION_VIEW, uri));
			} catch (ActivityNotFoundException activityNotFound) {
				Toast.makeText(AbstractDetailArret.this, R.string.noGoogleMap, Toast.LENGTH_LONG).show();
			}
			return true;
		} else if (item.getItemId() == R.id.menu_choix_date) {
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
			calendarLaVeille.set(Calendar.YEAR, year);
			calendarLaVeille.set(Calendar.MONTH, monthOfYear);
			calendarLaVeille.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			calendarLaVeille.add(Calendar.DATE, -1);
			setListAdapter(construireAdapter());
			if (getListAdapter().getCount() != 0) {
				setSelection(((AbstractDetailArretAdapter) getListAdapter()).getPositionToMove());
			}
			getListView().invalidate();
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == DATE_DIALOG_ID) {
			return new DatePickerDialog(this, mDateSetListener, calendar.get(Calendar.YEAR),
					calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
		}
		return null;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		if (v.getId() == android.R.id.list) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			DetailArretConteneur detailArretConteneur = (DetailArretConteneur) getListAdapter().getItem(info.position);
			menu.setHeaderTitle(formatterCalendarHeure(detailArretConteneur.getHoraire()));
			menu.add(Menu.NONE, R.id.creerNotif, 0, getString(R.string.creerNotif));
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		if (item.getItemId() == R.id.creerNotif) {
			final DetailArretConteneur detailArretConteneur = (DetailArretConteneur) getListAdapter().getItem(
					info.position);
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(formatterCalendarHeure(detailArretConteneur.getHoraire()));
			builder.setItems(getResources().getStringArray(R.array.choixTemps), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					int minutes = getResources().getIntArray(R.array.choixTempInt)[item];
					String ligneId = favori.ligneId;
					String arretId = favori.arretId;
					int heure = detailArretConteneur.getHoraire();
					if (heure >= 24 * 60) {
						heure -= (24 * 60);
					}
					int heureNotif = heure - minutes;
					if (heureNotif < 0) {
						heureNotif += (24 * 60);
					}
					Notification notification = new Notification();
					notification.setLigneId(ligneId);
					notification.setArretId(arretId);
					notification.setHeure(heureNotif);
					notification.setTempsAttente(minutes);
					notification.setDirection(favori.direction);
					notification.setMacroDirection(favori.macroDirection);
					AbstractTransportsApplication.getDataBaseHelper().delete(notification);
					AbstractTransportsApplication.getDataBaseHelper().insert(notification);
					Calendar calendar = Calendar.getInstance();
					int now = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
					int tempsRestant = (heureNotif) - now;
					if (tempsRestant <= 0) {
						tempsRestant += (24 * 60);
							}
					Toast.makeText(AbstractDetailArret.this,
							getResources().getString(R.string.tempsRestant, formatterCalendar(tempsRestant)),
							Toast.LENGTH_SHORT).show();
				}
			});
			builder.setCancelable(true);
			builder.create().show();
			return true;
		} else {
			return super.onContextItemSelected(item);
		}
	}

	private CharSequence formatterCalendarHeure(int prochainDepart) {
		StringBuilder stringBuilder = new StringBuilder();
		int heures = prochainDepart / 60;
		int minutes = prochainDepart - heures * 60;
		if (heures >= 24) {
			heures -= 24;
		}
		String heuresChaine = Integer.toString(heures);
		String minutesChaine = Integer.toString(minutes);
		if (heuresChaine.length() < 2) {
			stringBuilder.append('0');
		}
		stringBuilder.append(heuresChaine);
		stringBuilder.append(':');
		if (minutesChaine.length() < 2) {
			stringBuilder.append('0');
		}
		stringBuilder.append(minutesChaine);
		return stringBuilder.toString();
	}

    private CharSequence formatterCalendar(int tempsRestant) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(this.getString(R.string.dans));
		stringBuilder.append(' ');
		int heures = tempsRestant / 60;
		int minutes = tempsRestant - heures * 60;
		boolean tempsAjoute = false;
		if (heures > 0) {
			stringBuilder.append(heures);
			stringBuilder.append(' ');
			stringBuilder.append(this.getString(R.string.heures));
			stringBuilder.append(' ');
			tempsAjoute = true;
		}
		if (minutes > 0) {
			stringBuilder.append(minutes);
			stringBuilder.append(' ');
			stringBuilder.append(this.getString(R.string.minutes));
			tempsAjoute = true;
		}
		if (!tempsAjoute) {
			stringBuilder.append("0 ");
			stringBuilder.append(this.getString(R.string.minutes));
		}
		return stringBuilder.toString();
	}
}
