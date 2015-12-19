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
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import fr.ybo.transportscommun.AbstractTransportsApplication;
import fr.ybo.transportscommun.R;
import fr.ybo.transportscommun.activity.commun.BaseActivity.BaseFragmentActivity;
import fr.ybo.transportscommun.activity.commun.BaseActivity.BaseListActivity;
import fr.ybo.transportscommun.adapters.bus.AbstractDetailArretAdapter;
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

    private Calendar today = Calendar.getInstance();
    protected Calendar calendar = Calendar.getInstance();
    private Calendar calendarLaVeille = Calendar.getInstance();

    protected ArretFavori favori;

    private void recuperationDonneesIntent() {
        favori = (ArretFavori) getIntent().getExtras().getSerializable("favori");
        if (favori == null) {
            favori = new ArretFavori();
            favori.arretId = getIntent().getExtras().getString("idArret");
            favori.nomArret = getIntent().getExtras().getString("nomArret");
            favori.direction = getIntent().getExtras().getString("direction");
            favori.macroDirection = getIntent().getExtras().getInt("macroDirection", 0);
            final Ligne ligne = (Ligne) getIntent().getExtras().getSerializable("ligne");
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

    private boolean firstUpdate;

    protected abstract Set<Integer> getSecondsToUpdate();


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
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

            @Override
            public void update(final Calendar calendar) {
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
                return getSecondsToUpdate();
            }
        };
        updateTimeUtil = new UpdateTimeUtil(updateTime, this);
        if (myLigne.isChargee()) {
            setListAdapter(construireAdapter());
            if (getListAdapter().getCount() != 0) {
                setSelection(((AbstractDetailArretAdapter) getListAdapter()).getPositionToMove());
            }
            updateTimeUtil.start();
            firstUpdate = true;
        } else {
            chargerLigne();
        }
        final ListView lv = getListView();
        lv.setFastScrollEnabled(true);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, final View view, final int position, final long id) {
                final Adapter arretAdapter = ((AdapterView<ListAdapter>) adapterView).getAdapter();
                final DetailArretConteneur detailArretConteneur = (DetailArretConteneur) arretAdapter.getItem(position);
                final Intent intent = new Intent(AbstractDetailArret.this, getDetailTrajetClass());
                intent.putExtra("trajetId", detailArretConteneur.getTrajetId());
                intent.putExtra("sequence", detailArretConteneur.getSequence());
                startActivity(intent);
            }
        });
        lv.setTextFilterEnabled(true);

        final ImageView correspondance = (ImageView) findViewById(R.id.imageCorrespondance);
        final ViewGroup detailCorrespondance = (ViewGroup) findViewById(R.id.detailCorrespondance);
        correspondance.setImageResource(R.drawable.arrow_right_float);
        detailCorrespondance.removeAllViews();
        detailCorrespondance.setVisibility(View.INVISIBLE);
        correspondance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
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
                @Override
                public void onClick(final View view) {
                    final Intent intent = new Intent(AbstractDetailArret.this, getListAlertsForOneLineClass());
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

    private void construireCorrespondance(final ViewGroup detailCorrespondance) {
        /* Recuperation de l'arretCourant */
        Arret arretCourant = new Arret();
        arretCourant.id = favori.arretId;
        arretCourant = AbstractTransportsApplication.getDataBaseHelper().selectSingle(arretCourant);
        final Location locationArret = new Location("myProvider");
        locationArret.setLatitude(arretCourant.latitude);
        locationArret.setLongitude(arretCourant.longitude);

        /** Construction requête. */

        /** Paramètres de la requête */
        final double minLatitude = arretCourant.latitude - DISTANCE_LAT_IN_DEGREE;
        final double maxLatitude = arretCourant.latitude + DISTANCE_LAT_IN_DEGREE;
        final double minLongitude = arretCourant.longitude - DISTANCE_LNG_IN_DEGREE;
        final double maxLongitude = arretCourant.longitude + DISTANCE_LNG_IN_DEGREE;
        final List<String> selectionArgs = new ArrayList<String>(4);
        selectionArgs.add(String.valueOf(minLatitude));
        selectionArgs.add(String.valueOf(maxLatitude));
        selectionArgs.add(String.valueOf(minLongitude));
        selectionArgs.add(String.valueOf(maxLongitude));

        final Cursor cursor = AbstractTransportsApplication.getDataBaseHelper().executeSelectQuery("SELECT Arret.id as arretId, ArretRoute.ligneId as ligneId, Direction.direction as direction," + " Arret.nom as arretNom, Arret.latitude as latitude, Arret.longitude as longitude," + " Ligne.nomCourt as nomCourt, Ligne.nomLong as nomLong, ArretRoute.macroDirection as macroDirection " + "FROM Arret, ArretRoute, Direction, Ligne " + "WHERE Arret.id = ArretRoute.arretId and Direction.id = ArretRoute.directionId AND Ligne.id = ArretRoute.ligneId" + " AND Arret.latitude > :minLatitude AND Arret.latitude < :maxLatitude" + " AND Arret.longitude > :minLongitude AND Arret.longitude < :maxLongitude",
                selectionArgs);

        /** Recuperation des index dans le cussor */
        final int arretIdIndex = cursor.getColumnIndex("arretId");
        final int ligneIdIndex = cursor.getColumnIndex("ligneId");
        final int directionIndex = cursor.getColumnIndex("direction");
        final int arretNomIndex = cursor.getColumnIndex("arretNom");
        final int latitudeIndex = cursor.getColumnIndex("latitude");
        final int longitudeIndex = cursor.getColumnIndex("longitude");
        final int nomCourtIndex = cursor.getColumnIndex("nomCourt");
        final int nomLongIndex = cursor.getColumnIndex("nomLong");
        final int macroDirectionIndex = cursor.getColumnIndex("macroDirection");

        final List<Arret> arrets = new ArrayList<Arret>(20);

        while (cursor.moveToNext()) {
            final Arret arret = new Arret();
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
            final View relativeLayout = mInflater.inflate(getLayoutArretGps(), null);
            final ImageView iconeLigne = (ImageView) relativeLayout.findViewById(R.id.iconeLigne);
            iconeLigne.setImageResource(IconeLigne.getIconeResource(arret.favori.nomCourt));
            final TextView arretDirection = (TextView) relativeLayout.findViewById(R.id.arretgps_direction);
            arretDirection.setText(arret.favori.direction);
            final TextView nomArret = (TextView) relativeLayout.findViewById(R.id.arretgps_nomArret);
            nomArret.setText(arret.nom);
            final TextView distance = (TextView) relativeLayout.findViewById(R.id.arretgps_distance);
            distance.setText(arret.formatDistance());
            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    final Intent intent = new Intent(AbstractDetailArret.this, AbstractDetailArret.this.getClass());
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

            private boolean erreurNoSpaceLeft;

            @Override
            protected void myDoBackground() {
                try {
                    UpdateDataBase.chargeDetailLigne(getRawClass(), myLigne, getResources());
                } catch (final NoSpaceLeftException e) {
                    erreurNoSpaceLeft = true;
                }
            }

            @Override
            protected void onPostExecute(final Void result) {
                super.onPostExecute(result);
                if (erreurNoSpaceLeft) {
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
    public boolean onOptionsItemSelected(final MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.menu_google_map) {
            Arret arret = new Arret();
            arret.id = favori.arretId;
            arret = AbstractTransportsApplication.getDataBaseHelper().selectSingle(arret);
            final String lat = Double.toString(arret.getLatitude());
            final String lon = Double.toString(arret.getLongitude());
            final Uri uri = Uri.parse("geo:" + lat + ',' + lon + "?q=" + lat + ',' + lon);
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            } catch (final ActivityNotFoundException activityNotFound) {
                Toast.makeText(this, R.string.noGoogleMap, Toast.LENGTH_LONG).show();
            }
            return true;
        }
        if (item.getItemId() == R.id.menu_choix_date) {
            showDialog(DATE_DIALOG_ID);
            return true;
        }
        return false;
    }

    private static final int DATE_DIALOG_ID = 0;

    private final DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(final DatePicker view, final int year, final int monthOfYear, final int dayOfMonth) {
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
    protected Dialog onCreateDialog(final int id) {
        if (id == DATE_DIALOG_ID) {
            return new DatePickerDialog(this, mDateSetListener, calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        }
        return null;
    }

    @Override
    public void onCreateContextMenu(final ContextMenu menu, final View v, final ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == android.R.id.list) {
            final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            final DetailArretConteneur detailArretConteneur = (DetailArretConteneur) getListAdapter().getItem(info.position);
            menu.setHeaderTitle(formatterCalendarHeure(detailArretConteneur.getHoraire()));
            menu.add(Menu.NONE, R.id.creerNotif, 0, getString(R.string.creerNotif));
        }
    }

    @Override
    public boolean onContextItemSelected(final MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (item.getItemId() == R.id.creerNotif) {
            final DetailArretConteneur detailArretConteneur = (DetailArretConteneur) getListAdapter().getItem(
                    info.position);
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(formatterCalendarHeure(detailArretConteneur.getHoraire()));
            builder.setItems(getResources().getStringArray(R.array.choixTemps), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, final int item) {
                    final int minutes = getResources().getIntArray(R.array.choixTempInt)[item];
                    final String ligneId = favori.ligneId;
                    final String arretId = favori.arretId;
                    int heure = detailArretConteneur.getHoraire();
                    if (heure >= 24 * 60) {
                        heure -= 24 * 60;
                    }
                    int heureNotif = heure - minutes;
                    if (heureNotif < 0) {
                        heureNotif += 24 * 60;
                    }
                    final Notification notification = new Notification();
                    notification.setLigneId(ligneId);
                    notification.setArretId(arretId);
                    notification.setHeure(heureNotif);
                    notification.setTempsAttente(minutes);
                    notification.setDirection(favori.direction);
                    notification.setMacroDirection(favori.macroDirection);
                    AbstractTransportsApplication.getDataBaseHelper().delete(notification);
                    AbstractTransportsApplication.getDataBaseHelper().insert(notification);
                    final Calendar calendar = Calendar.getInstance();
                    final int now = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
                    int tempsRestant = heureNotif - now;
                    if (tempsRestant <= 0) {
                        tempsRestant += 24 * 60;
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

    private static CharSequence formatterCalendarHeure(final int prochainDepart) {
        final StringBuilder stringBuilder = new StringBuilder();
        int heures = prochainDepart / 60;
        final int minutes = prochainDepart - heures * 60;
        if (heures >= 24) {
            heures -= 24;
        }
        final String heuresChaine = Integer.toString(heures);
        final String minutesChaine = Integer.toString(minutes);
        if (heuresChaine.length() < 2) {
            stringBuilder.append('0');
        }
        stringBuilder.append(heuresChaine);
        stringBuilder.append(':');
        if (minutesChaine.length() < 2) {
            stringBuilder.append('0');
        }
        stringBuilder.append(minutesChaine);
        return stringBuilder;
    }

    private CharSequence formatterCalendar(final int tempsRestant) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getString(R.string.dans));
        stringBuilder.append(' ');
        final int heures = tempsRestant / 60;
        final int minutes = tempsRestant - heures * 60;
        boolean tempsAjoute = false;
        if (heures > 0) {
            stringBuilder.append(heures);
            stringBuilder.append(' ');
            stringBuilder.append(getString(R.string.heures));
            stringBuilder.append(' ');
            tempsAjoute = true;
        }
        if (minutes > 0) {
            stringBuilder.append(minutes);
            stringBuilder.append(' ');
            stringBuilder.append(getString(R.string.minutes));
            tempsAjoute = true;
        }
        if (!tempsAjoute) {
            stringBuilder.append("0 ");
            stringBuilder.append(getString(R.string.minutes));
        }
        return stringBuilder;
    }
}
