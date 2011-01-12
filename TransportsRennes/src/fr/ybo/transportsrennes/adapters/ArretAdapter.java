package fr.ybo.transportsrennes.adapters;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.TransportsRennesApplication;
import fr.ybo.transportsrennes.activity.OnClickFavoriGestionnaire;
import fr.ybo.transportsrennes.keolis.gtfs.modele.ArretFavori;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Ligne;

/**
 * Adapteur pour les arrêts.
 */
public class ArretAdapter extends CursorAdapter {

	private Ligne ligne;
	private ArretFavori favori;
	private  Activity activity;

	public ArretAdapter(Activity activity, Cursor cursor, Ligne ligne) {
		super(activity, cursor);
		this.ligne = ligne;
		favori = new ArretFavori();
		favori.ligneId = this.ligne.id;
		this.activity = activity;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		int nameCol = cursor.getColumnIndex("arretName");
		String name = cursor.getString(nameCol);
		int directionCol = cursor.getColumnIndex("direction");
		String direction = cursor.getString(directionCol);
		int arretIdCol = cursor.getColumnIndex("_id");
		favori.arretId = cursor.getString(arretIdCol);
		((TextView) view.findViewById(R.id.nomArret)).setText(name);
		((TextView) view.findViewById(R.id.directionArret)).setText("vers " + direction);
		final ImageView imageView = ((ImageView) view.findViewById(R.id.isfavori));
		imageView.setImageResource(
				TransportsRennesApplication.getDataBaseHelper().selectSingle(favori) == null ? android.R.drawable.btn_star_big_off :
						android.R.drawable.btn_star_big_on);
		imageView.setOnClickListener(new OnClickFavoriGestionnaire(ligne, favori.arretId, name, direction, activity));
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		return inflater.inflate(R.layout.arret, parent, false);

	}

}
