/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package fr.ybo.transportsrennes;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import fr.ybo.transportsrennes.activity.MenuAccueil;
import fr.ybo.transportsrennes.adapters.DetailTrajetAdapter;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Direction;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Ligne;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Trajet;
import fr.ybo.transportsrennes.util.LogYbo;

import java.lang.reflect.Field;
import java.util.Collections;

/**
 * Activitée permettant d'afficher le détail d'un trajet
 *
 * @author ybonnel
 */
public class DetailTrajet extends MenuAccueil.ListActivity {

	private final static Class<?> classDrawable = R.drawable.class;

	private static final LogYbo LOG_YBO = new LogYbo(DetailTrajet.class);

	private Cursor currentCursor;

	private Trajet trajet;
	private Direction direction;
	private Ligne ligne;

	private void recuperationDonneesIntent() {
		trajet = new Trajet();
		trajet.id = Integer.valueOf(getIntent().getExtras().getString("trajetId"));
		trajet = TransportsRennesApplication.getDataBaseHelper().selectSingle(trajet);
		direction = new Direction();
		direction.id = trajet.directionId;
		direction = TransportsRennesApplication.getDataBaseHelper().selectSingle(direction);
		ligne = new Ligne();
		ligne.id = trajet.ligneId;
		ligne = TransportsRennesApplication.getDataBaseHelper().selectSingle(ligne);
	}

	private void gestionViewsTitle() {
		LinearLayout conteneur = (LinearLayout) findViewById(R.id.conteneurImage);
		TextView nomLong = (TextView) findViewById(R.id.nomLong);
		nomLong.setText(ligne.nomLong);
		try {
			Field fieldIcon = classDrawable.getDeclaredField("i" + ligne.nomCourt.toLowerCase());
			int ressourceImg = fieldIcon.getInt(null);
			ImageView imgView = new ImageView(getApplicationContext());
			imgView.setImageResource(ressourceImg);
			conteneur.addView(imgView);
		} catch (Exception ignore) {
		}
		((TextView) findViewById(R.id.detailTrajet_nomTrajet)).setText(direction.direction);
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detailtrajet);
		recuperationDonneesIntent();
		gestionViewsTitle();
		construireListe();
		ListView lv = getListView();
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				DetailTrajetAdapter arretAdapter = (DetailTrajetAdapter) ((ListView) adapterView).getAdapter();
				Cursor cursor = (Cursor) arretAdapter.getItem(position);
				Intent intent = new Intent(DetailTrajet.this, DetailArret.class);
				intent.putExtra("idArret", cursor.getString(cursor.getColumnIndex("_id")));
				intent.putExtra("nomArret", cursor.getString(cursor.getColumnIndex("arretNom")));
				intent.putExtra("direction", direction.direction);
				intent.putExtra("ligne", ligne);
				startActivity(intent);
			}
		});
		lv.setTextFilterEnabled(true);
	}

	private void construireListe() {
		StringBuilder requete = new StringBuilder();
		requete.append("SELECT Arret.id as _id, Horaire.heureDepart as heureDepart, Arret.nom as nom ");
		requete.append("FROM Arret, Horaire ");
		requete.append("WHERE Arret.id = Horaire.arretId");
		requete.append(" AND Horaire.trajetId = ");
		requete.append(trajet.id);
		requete.append(" ORDER BY stopSequence;");
		LOG_YBO.debug("Exécution de " + requete.toString());
		currentCursor = TransportsRennesApplication.getDataBaseHelper()
				.executeSelectQuery(requete.toString(), null);
		LOG_YBO.debug("Résultat : " + currentCursor.getCount());
		setListAdapter(new DetailTrajetAdapter(this, currentCursor));
	}

	private void closeCurrentCursor() {
		if (currentCursor != null && !currentCursor.isClosed()) {
			currentCursor.close();
		}
	}

	@Override
	protected void onDestroy() {
		closeCurrentCursor();
		super.onDestroy();
	}
}
