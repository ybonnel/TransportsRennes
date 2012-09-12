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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.timeo);
		getActivityHelper().setupActionBar(R.menu.default_menu_items, R.menu.holo_default_menu_items);

		Uri uri = getIntent().getData();
		List<Arret> arrets = construireListeArrets(uri.getLastPathSegment());
		
		if (arrets.isEmpty()) {
			// Erreur
			Toast.makeText(getApplicationContext(), R.string.erreurTimeoNotFound, Toast.LENGTH_LONG).show();
			finish();
		} else if (arrets.size() == 1) {
			// Lancer detailArret
			Intent intent = new Intent(this, DetailArret.class);
			intent.putExtra("favori", arrets.get(0).favori);
			startActivity(intent);
			finish();
		} else {
			// Construire adapter
			setListAdapter(new TimeoAdapter(this, arrets));
			ListView listView = getListView();
			listView.setFastScrollEnabled(true);
			listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
					Arret arret = (Arret) getListAdapter().getItem(position);
					Intent intent = new Intent(TimeoActivity.this, DetailArret.class);
					intent.putExtra("favori", arret.favori);
					startActivity(intent);
				}
			});

			listView.setTextFilterEnabled(true);
			registerForContextMenu(listView);
		}
    }

	private List<Arret> construireListeArrets(String idTimeo) {
		StringBuilder requete = new StringBuilder();
		requete.append("SELECT");
		requete.append(" Arret.id as arretId,");
		requete.append(" Arret.nom as arretNom,");
		requete.append(" Arret.latitude as arretLatitude,");
		requete.append(" Arret.longitude as arretLongitude,");
		requete.append(" Direction.direction as favoriDirection,");
		requete.append(" Ligne.id as ligneId,");
		requete.append(" Ligne.nomCourt as nomCourt,");
		requete.append(" Ligne.nomLong as nomLong, ");
		requete.append(" ArretRoute.macroDirection as macroDirection ");
		requete.append("FROM Arret, ArretRoute, Ligne, Direction ");
		requete.append("WHERE Arret.id = ArretRoute.arretId");
		requete.append(" AND ArretRoute.ligneId = Ligne.id");
		requete.append(" AND ArretRoute.directionId = Direction.id");
		requete.append(" AND Arret.id = :idTimeo");
		Cursor cursor =
				AbstractTransportsApplication.getDataBaseHelper().executeSelectQuery(requete.toString(),
						Collections.singletonList(idTimeo));

		int arretIdIndex = cursor.getColumnIndex("arretId");
		int arretNomIndex = cursor.getColumnIndex("arretNom");
		int latitudeIndex = cursor.getColumnIndex("arretLatitude");
		int longitudeIndex = cursor.getColumnIndex("arretLongitude");
		int directionIndex = cursor.getColumnIndex("favoriDirection");
		int ligneIdIndex = cursor.getColumnIndex("ligneId");
		int nomCourtIndex = cursor.getColumnIndex("nomCourt");
		int nomLongIndex = cursor.getColumnIndex("nomLong");
		int macroDirectionIndex = cursor.getColumnIndex("macroDirection");
		List<Arret> arrets = new ArrayList<Arret>();
		while (cursor.moveToNext()) {
			Arret arret = new Arret();
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
