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

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import fr.ybo.transportsrennes.activity.MenuAccueil;
import fr.ybo.transportsrennes.keolis.modele.bus.Alert;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Activitée permettant d'afficher les détails d'une station.
 *
 * @author ybonnel
 */
public class DetailAlert extends MenuAccueil.Activity {

	private static final Class<R.drawable> classDrawable = R.drawable.class;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detailalert);
		final Alert alert = (Alert) getIntent().getExtras().getSerializable("alert");

		LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());

		TextView titreAlert = (TextView) findViewById(R.id.titreAlert);
		LinearLayout conteneur = (LinearLayout) findViewById(R.id.conteneurImage);
		titreAlert.setText(alert.getTitleFormate());
		for (String ligne : alert.lines) {
			try {
				Field fieldIcon = classDrawable.getDeclaredField("i" + ligne.toLowerCase());
				int ressourceImg = fieldIcon.getInt(null);
				ImageView imgView = (ImageView) layoutInflater.inflate(R.layout.imagebus, null);
				imgView.setImageResource(ressourceImg);
				conteneur.addView(imgView);
			} catch (NoSuchFieldException e) {
				TextView textView = new TextView(getApplicationContext());
				textView.setTextSize(16);
				textView.setText(ligne);
				conteneur.addView(textView);
			} catch (IllegalAccessException e) {
				TextView textView = new TextView(getApplicationContext());
				textView.setTextSize(16);
				textView.setText(ligne);
				conteneur.addView(textView);
			}
		}
		Set<String> arretsToBold = new HashSet<String>();
		for (String line : alert.lines) {
			StringBuilder requete = new StringBuilder();
			requete.append("select Arret.nom from Arret, Ligne, ArretRoute ");
			requete.append("where Ligne.nomCourt = :nomCourt and ArretRoute.ligneId = Ligne.id ");
			requete.append("and Arret.id = ArretRoute.arretId");
			Cursor cursor = TransportsRennesApplication.getDataBaseHelper().executeSelectQuery(requete.toString(), Collections.singletonList(line));
			while (cursor.moveToNext()) {
				arretsToBold.add(cursor.getString(0));
			}
			cursor.close();
		}
		((TextView) findViewById(R.id.detailAlert_Detail)).setText(Html.fromHtml(alert.getDetailFormatte(arretsToBold)));
	}

}
