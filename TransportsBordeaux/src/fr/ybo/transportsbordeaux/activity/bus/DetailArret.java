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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

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
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

import fr.ybo.transportsbordeaux.R;
import fr.ybo.transportsbordeaux.activity.alerts.ListAlertsForOneLine;
import fr.ybo.transportsbordeaux.activity.commun.MenuAccueil;
import fr.ybo.transportsbordeaux.adapters.bus.DetailArretAdapter;
import fr.ybo.transportsbordeaux.application.TransportsBordeauxApplication;
import fr.ybo.transportsbordeaux.util.IconeLigne;
import fr.ybo.transportsbordeaux.util.TacheAvecProgressDialog;
import fr.ybo.transportsbordeaux.util.UpdateTimeUtil;
import fr.ybo.transportsbordeaux.util.UpdateTimeUtil.UpdateTime;
import fr.ybo.transportscommun.donnees.manager.LigneInexistanteException;
import fr.ybo.transportscommun.donnees.manager.gtfs.UpdateDataBase;
import fr.ybo.transportscommun.donnees.modele.Arret;
import fr.ybo.transportscommun.donnees.modele.ArretFavori;
import fr.ybo.transportscommun.donnees.modele.DetailArretConteneur;
import fr.ybo.transportscommun.donnees.modele.Horaire;
import fr.ybo.transportscommun.donnees.modele.Ligne;
import fr.ybo.transportscommun.donnees.modele.Notification;
import fr.ybo.transportscommun.util.NoSpaceLeftException;

/**
 * Activitée permettant d'afficher les détails d'une station.
 *
 * @author ybonnel
 */
public class DetailArret extends MenuAccueil.ListActivity {

    private static final double DISTANCE_RECHERCHE_METRE = 1000.0;
    private static final double DEGREE_LATITUDE_EN_METRES = 111192.62;
    private static final double DISTANCE_LAT_IN_DEGREE = DISTANCE_RECHERCHE_METRE / DEGREE_LATITUDE_EN_METRES;
    private static final double DEGREE_LONGITUDE_EN_METRES = 74452.10;
    private static final double DISTANCE_LNG_IN_DEGREE = DISTANCE_RECHERCHE_METRE / DEGREE_LONGITUDE_EN_METRES;
    private static final int DISTANCE_MAX_METRE = 151;

    private boolean prochainArrets = true;

    private Calendar today = Calendar.getInstance();
    private Calendar calendar = Calendar.getInstance();
    private Calendar calendarLaVeille = Calendar.getInstance();

    private ArretFavori favori;

    private boolean isToday() {
        return calendar.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)
                && calendar.get(Calendar.MONTH) == today.get(Calendar.MONTH)
                && calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR);
    }

    private void recuperationDonneesIntent() {
        favori = (ArretFavori) getIntent().getExtras().getSerializable("favori");
        if (favori == null) {
            favori = new ArretFavori();
            favori.arretId = getIntent().getExtras().getString("idArret");
            favori.nomArret = getIntent().getExtras().getString("nomArret");
            favori.direction = getIntent().getExtras().getString("direction");
			favori.macroDirection = 0;
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

    private ListAdapter construireAdapter() {
        if (prochainArrets && isToday()) {
            return construireAdapterProchainsDeparts();
        }
        return construireAdapterAllDeparts();
    }

    private ListAdapter construireAdapterAllDeparts() {
        int now = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
        return new DetailArretAdapter(getApplicationContext(), Horaire.getAllHorairesAsList(favori.ligneId,
                favori.arretId, calendar), now, isToday());
    }

    private ListAdapter construireAdapterProchainsDeparts() {
        int now = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
        return new DetailArretAdapter(getApplicationContext(), Horaire.getProchainHorairesAsList(favori.ligneId,
                favori.arretId, null, calendar), now, isToday());
    }

    private Ligne myLigne;
    private LayoutInflater mInflater;

    private UpdateTimeUtil updateTimeUtil;

    private boolean firstUpdate = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInflater = LayoutInflater.from(this);
        calendar = Calendar.getInstance();
        today = Calendar.getInstance();
        calendarLaVeille = Calendar.getInstance();
        calendarLaVeille.add(Calendar.DATE, -1);
        setContentView(R.layout.detailarret);
        recuperationDonneesIntent();
        if (favori.ligneId == null) {
            return;
        }
        gestionViewsTitle();
        ImageView imageGoogleMap = (ImageView) findViewById(R.id.googlemap);
        imageGoogleMap.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Arret arret = new Arret();
                arret.id = favori.arretId;
                arret = TransportsBordeauxApplication.getDataBaseHelper().selectSingle(arret);
                String lat = Double.toString(arret.getLatitude());
                String lon = Double.toString(arret.getLongitude());
                Uri uri = Uri.parse("geo:0,0?q=" + favori.nomArret + "+@" + lat + ',' + lon);
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                } catch (ActivityNotFoundException activityNotFound) {
                    Toast.makeText(DetailArret.this, R.string.noGoogleMap, Toast.LENGTH_LONG).show();
                }
            }
        });
        myLigne = new Ligne();
        myLigne.id = favori.ligneId;
        myLigne = TransportsBordeauxApplication.getDataBaseHelper().selectSingle(myLigne);
        if (myLigne == null) {
            Toast.makeText(DetailArret.this, R.string.erreurLigneInconue, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        updateTimeUtil = new UpdateTimeUtil(new UpdateTime() {

            public void update(Calendar calendar) {
                if (isToday()) {
                    DetailArret.this.calendar = calendar;
                    today = Calendar.getInstance();
                    calendarLaVeille = Calendar.getInstance();
                    calendarLaVeille.add(Calendar.DATE, -1);
                    setListAdapter(construireAdapter());
                    getListView().invalidate();
                }
            }
        }, this);
        if (!myLigne.isChargee()) {
            chargerLigne();
        } else {
            setListAdapter(construireAdapter());
            updateTimeUtil.start();
            firstUpdate = true;
        }
        ListView lv = getListView();
        lv.setFastScrollEnabled(true);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                DetailArretAdapter arretAdapter = (DetailArretAdapter) adapterView.getAdapter();
                DetailArretConteneur horaire = arretAdapter.getItem(position);
                Intent intent = new Intent(DetailArret.this, DetailTrajet.class);
                intent.putExtra("trajetId", horaire.getTrajetId());
                intent.putExtra("sequence", horaire.getSequence());
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
        if (TransportsBordeauxApplication.hasAlert(myLigne.nomCourt)) {
            findViewById(R.id.alerte).setVisibility(View.VISIBLE);
            findViewById(R.id.alerte).setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
					Intent intent = new Intent(DetailArret.this, ListAlertsForOneLine.class);
                    intent.putExtra("ligne", myLigne);
                    startActivity(intent);
                }
            });
        } else {
            findViewById(R.id.alerte).setVisibility(View.GONE);
        }

        registerForContextMenu(lv);
        // Look up the AdView as a resource and load a request.
        ((AdView) this.findViewById(R.id.adView)).loadAd(new AdRequest());
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
        arretCourant = TransportsBordeauxApplication.getDataBaseHelper().selectSingle(arretCourant);
        Location locationArret = new Location("myProvider");
        locationArret.setLatitude(arretCourant.latitude);
        locationArret.setLongitude(arretCourant.longitude);

        /** Construction requête. */
        StringBuilder requete = new StringBuilder();
        requete.append("SELECT Arret.id as arretId, ArretRoute.ligneId as ligneId, Direction.direction as direction,");
        requete.append(" Arret.nom as arretNom, Arret.latitude as latitude, Arret.longitude as longitude,");
        requete.append(" Ligne.nomCourt as nomCourt, Ligne.nomLong as nomLong ");
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

        Cursor cursor = TransportsBordeauxApplication.getDataBaseHelper().executeSelectQuery(requete.toString(), selectionArgs);

        /** Recuperation des index dans le cussor */
        int arretIdIndex = cursor.getColumnIndex("arretId");
        int ligneIdIndex = cursor.getColumnIndex("ligneId");
        int directionIndex = cursor.getColumnIndex("direction");
        int arretNomIndex = cursor.getColumnIndex("arretNom");
        int latitudeIndex = cursor.getColumnIndex("latitude");
        int longitudeIndex = cursor.getColumnIndex("longitude");
        int nomCourtIndex = cursor.getColumnIndex("nomCourt");
        int nomLongIndex = cursor.getColumnIndex("nomLong");

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
			arret.favori.macroDirection = 0;
            arret.latitude = cursor.getDouble(latitudeIndex);
            arret.longitude = cursor.getDouble(longitudeIndex);
            arret.favori.nomCourt = cursor.getString(nomCourtIndex);
            arret.favori.nomLong = cursor.getString(nomLongIndex);
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
            RelativeLayout relativeLayout = (RelativeLayout) mInflater.inflate(R.layout.arretgps, null);
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
                    Intent intent = new Intent(DetailArret.this, DetailArret.class);
                    intent.putExtra("favori", arret.favori);
                    startActivity(intent);
                }
            });
            detailCorrespondance.addView(relativeLayout);
        }
    }

    private void chargerLigne() {
        new TacheAvecProgressDialog<Void, Void, Void>(this, getString(R.string.premierAccesLigne, myLigne.nomCourt)) {

            private boolean erreurNoSpaceLeft = false;
			private boolean erreurLigneNonTrouvee = false;

            @Override
            protected Void doInBackground(Void... pParams) {
                try {
					UpdateDataBase.chargeDetailLigne(R.raw.class, myLigne, getResources());
                } catch (NoSpaceLeftException e) {
                    erreurNoSpaceLeft = true;
				} catch (LigneInexistanteException e) {
					erreurLigneNonTrouvee = true;
				}
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
				if (erreurLigneNonTrouvee) {
					Toast.makeText(DetailArret.this, getString(R.string.erreurLigneInconue, myLigne.nomCourt),
							Toast.LENGTH_LONG).show();
					finish();
				} else if (erreurNoSpaceLeft) {
                    Toast.makeText(DetailArret.this, R.string.erreurNoSpaceLeft, Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    setListAdapter(construireAdapter());
                    getListView().invalidate();
                    updateTimeUtil.start();
                    firstUpdate = true;
                }
            }

        }.execute((Void) null);

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
                setListAdapter(construireAdapter());
                getListView().invalidate();
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
            calendarLaVeille.set(Calendar.YEAR, year);
            calendarLaVeille.set(Calendar.MONTH, monthOfYear);
            calendarLaVeille.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            calendarLaVeille.add(Calendar.DATE, -1);
            setListAdapter(construireAdapter());
            getListView().invalidate();
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
        switch (item.getItemId()) {
            case R.id.creerNotif:
                final DetailArretConteneur detailArretConteneur = (DetailArretConteneur) getListAdapter().getItem(info.position);
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
                        TransportsBordeauxApplication.getDataBaseHelper().delete(notification);
                        TransportsBordeauxApplication.getDataBaseHelper().insert(notification);
                        Calendar calendar = Calendar.getInstance();
                        int now = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
                        int tempsRestant = (heureNotif) - now;
                        if (tempsRestant <= 0) {
                            tempsRestant += (24 * 60);
                        }
                        Toast.makeText(DetailArret.this, getResources().getString(R.string.tempsRestant, formatterCalendar(tempsRestant)), Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setCancelable(true);
                builder.create().show();

                return true;
            default:
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
