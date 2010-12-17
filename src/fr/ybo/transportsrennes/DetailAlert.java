package fr.ybo.transportsrennes;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import fr.ybo.transportsrennes.keolis.modele.bus.Alert;

/**
 * Activitée permettant d'afficher les détails d'une station.
 *
 * @author ybonnel
 */
public class DetailAlert extends Activity {

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detailalert);
		final Alert alert = (Alert) getIntent().getExtras().getSerializable("alert");
		((TextView) findViewById(R.id.detailAlert_Titre)).setText(alert.getTitle());
		final StringBuilder lignes = new StringBuilder();
		if (alert.getLines().isEmpty()) {
			findViewById(R.id.detailAlert_Lignes).setVisibility(View.INVISIBLE);
		} else {
			findViewById(R.id.detailAlert_Lignes).setVisibility(View.VISIBLE);
			boolean premier = true;
			for (final String ligne : alert.getLines()) {
				if (premier) {
					premier = false;
				} else {
					lignes.append(", ");
				}
				lignes.append(ligne);
			}
			((TextView) findViewById(R.id.detailAlert_Lignes)).setText("Lignes concernée : " + lignes);
		}
		((TextView) findViewById(R.id.detailAlert_Detail)).setText(alert.getDetailFormatte(lignes.toString()));
	}

}
