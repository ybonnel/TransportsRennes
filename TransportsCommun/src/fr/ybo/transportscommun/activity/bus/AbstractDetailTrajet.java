package fr.ybo.transportscommun.activity.bus;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import fr.ybo.transportscommun.AbstractTransportsApplication;
import fr.ybo.transportscommun.R;
import fr.ybo.transportscommun.activity.commun.BaseActivity.BaseListActivity;
import fr.ybo.transportscommun.adapters.bus.DetailTrajetAdapter;
import fr.ybo.transportscommun.donnees.modele.Direction;
import fr.ybo.transportscommun.donnees.modele.Ligne;
import fr.ybo.transportscommun.donnees.modele.Trajet;
import fr.ybo.transportscommun.util.IconeLigne;

public abstract class AbstractDetailTrajet extends BaseListActivity {

	private Cursor currentCursor;

	private Trajet trajet;
	private Direction direction;
	private Ligne ligne;
	private int sequence;

	private void recuperationDonneesIntent() {
		trajet = new Trajet();
		trajet.id = getIntent().getExtras().getInt("trajetId");
		sequence = getIntent().getExtras().getInt("sequence");
		trajet = AbstractTransportsApplication.getDataBaseHelper().selectSingle(trajet);
		direction = new Direction();
		direction.id = trajet.directionId;
		direction = AbstractTransportsApplication.getDataBaseHelper().selectSingle(direction);
		ligne = new Ligne();
		ligne.id = trajet.ligneId;
		ligne = AbstractTransportsApplication.getDataBaseHelper().selectSingle(ligne);
	}

	private void gestionViewsTitle() {
		((TextView) findViewById(R.id.nomLong)).setText(ligne.nomLong);
		((ImageView) findViewById(R.id.iconeLigne)).setImageResource(IconeLigne.getIconeResource(ligne.nomCourt));
		((TextView) findViewById(R.id.detailTrajet_nomTrajet)).setText(getString(R.string.vers) + ' '
				+ direction.direction);
	}

	protected abstract int getLayout();

	protected abstract void setupActionBar();

	protected abstract Class<? extends AbstractDetailArret> getDetailArretClass();

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayout());
		setupActionBar();
		recuperationDonneesIntent();
		gestionViewsTitle();
		construireListe();
		final ListView lv = getListView();
		lv.setFastScrollEnabled(true);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(final AdapterView<?> adapterView, final View view, final int position, final long id) {
				final Adapter arretAdapter = adapterView.getAdapter();
				final Cursor cursor = (Cursor) arretAdapter.getItem(position);
				final Intent intent = new Intent(AbstractDetailTrajet.this, getDetailArretClass());
				intent.putExtra("idArret", cursor.getString(cursor.getColumnIndex("_id")));
				intent.putExtra("nomArret", cursor.getString(cursor.getColumnIndex("nom")));
				intent.putExtra("direction", direction.direction);
				intent.putExtra("macroDirection", trajet.macroDirection);
				intent.putExtra("ligne", ligne);
				startActivity(intent);
			}
		});
		lv.setTextFilterEnabled(true);
	}

	private void construireListe() {
		final List<String> selectionArgs = new ArrayList<String>(2);
		selectionArgs.add(String.valueOf(trajet.id));
		selectionArgs.add(String.valueOf(sequence));

		currentCursor = AbstractTransportsApplication.getDataBaseHelper().executeSelectQuery("SELECT Arret.id as _id, Horaire.heureDepart as heureDepart, Arret.nom as nom " + "FROM Arret, Horaire_" + ligne.id + " as Horaire " + "WHERE Arret.id = Horaire.arretId" + " AND Horaire.trajetId = :trajetId" + " AND Horaire.stopSequence > :sequence " + "ORDER BY stopSequence;",
				selectionArgs);

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
