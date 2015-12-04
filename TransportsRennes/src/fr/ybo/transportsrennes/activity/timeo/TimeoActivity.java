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
package fr.ybo.transportsrennes.activity.timeo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import fr.ybo.transportscommun.AbstractTransportsApplication;
import fr.ybo.transportscommun.activity.commun.BaseActivity.BaseListActivity;
import fr.ybo.transportscommun.donnees.modele.Arret;
import fr.ybo.transportscommun.donnees.modele.ArretFavori;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.activity.bus.DetailArret;
import fr.ybo.transportsrennes.adapters.timeo.TimeoAdapter;

/**
 * Activité utilisé pour les scan par QR Code.
 * 
 * @author ybonnel
 */
public class TimeoActivity extends BaseListActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.timeo);
		getActivityHelper().setupActionBar(R.menu.default_menu_items, R.menu.holo_default_menu_items);

		final Uri uri = getIntent().getData();
		final List<Arret> arrets = construireListeArrets(uri.getLastPathSegment());
		
		if (arrets.isEmpty()) {
			// Erreur
			Toast.makeText(getApplicationContext(), R.string.erreurTimeoNotFound, Toast.LENGTH_LONG).show();
			finish();
		} else if (arrets.size() == 1) {
			// Lancer detailArret
			final Intent intent = new Intent(this, DetailArret.class);
			intent.putExtra("favori", arrets.get(0).favori);
			startActivity(intent);
			finish();
		} else {
			// Construire adapter
			setListAdapter(new TimeoAdapter(this, arrets));
			final ListView listView = getListView();
			listView.setFastScrollEnabled(true);
			listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(final AdapterView<?> adapterView, final View view, final int position, final long id) {
					final Arret arret = (Arret) getListAdapter().getItem(position);
					final Intent intent = new Intent(TimeoActivity.this, DetailArret.class);
					intent.putExtra("favori", arret.favori);
					startActivity(intent);
				}
			});

			listView.setTextFilterEnabled(true);
			registerForContextMenu(listView);
		}
    }

	private static List<Arret> construireListeArrets(final String idTimeo) {
		final Cursor cursor =
				AbstractTransportsApplication.getDataBaseHelper().executeSelectQuery("SELECT" + " Arret.id as arretId," + " Arret.nom as arretNom," + " Arret.latitude as arretLatitude," + " Arret.longitude as arretLongitude," + " Direction.direction as favoriDirection," + " Ligne.id as ligneId," + " Ligne.nomCourt as nomCourt," + " Ligne.nomLong as nomLong, " + " ArretRoute.macroDirection as macroDirection " + "FROM Arret, ArretRoute, Ligne, Direction " + "WHERE Arret.id = ArretRoute.arretId" + " AND ArretRoute.ligneId = Ligne.id" + " AND ArretRoute.directionId = Direction.id" + " AND Arret.id = :idTimeo",
						Collections.singletonList(idTimeo));

		final int arretIdIndex = cursor.getColumnIndex("arretId");
		final int arretNomIndex = cursor.getColumnIndex("arretNom");
		final int latitudeIndex = cursor.getColumnIndex("arretLatitude");
		final int longitudeIndex = cursor.getColumnIndex("arretLongitude");
		final int directionIndex = cursor.getColumnIndex("favoriDirection");
		final int ligneIdIndex = cursor.getColumnIndex("ligneId");
		final int nomCourtIndex = cursor.getColumnIndex("nomCourt");
		final int nomLongIndex = cursor.getColumnIndex("nomLong");
		final int macroDirectionIndex = cursor.getColumnIndex("macroDirection");
		final List<Arret> arrets = new ArrayList<Arret>();
		while (cursor.moveToNext()) {
			final Arret arret = new Arret();
			arret.id = cursor.getString(arretIdIndex);
			arret.nom = cursor.getString(arretNomIndex);
			arret.latitude = cursor.getDouble(latitudeIndex);
			arret.longitude = cursor.getDouble(longitudeIndex);
			arret.favori = new ArretFavori();
			arret.favori.direction = cursor.getString(directionIndex);
			arret.favori.ligneId = cursor.getString(ligneIdIndex);
			arret.favori.nomCourt = cursor.getString(nomCourtIndex);
			arret.favori.nomLong = cursor.getString(nomLongIndex);
			arret.favori.nomArret = arret.nom;
			arret.favori.arretId = arret.id;
			arret.favori.macroDirection = cursor.getInt(macroDirectionIndex);
			arrets.add(arret);
		}
		cursor.close();
		return arrets;
	}
}
