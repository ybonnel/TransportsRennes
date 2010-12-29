package fr.ybo.transportsrennes;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
public class DetailAlert extends Activity {

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
		for (String ligne : alert.getLines()) {
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
		for (String line : alert.getLines()) {
			StringBuilder requete = new StringBuilder();
			requete.append("select Arret.nom from Arret, Route, ArretRoute ");
			requete.append("where Route.nomCourt = :nomCourt and ArretRoute.routeId = Route.id ");
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
