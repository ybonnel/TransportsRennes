package fr.ybo.transportsrennes.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Route;
import fr.ybo.transportsrennes.util.Formatteur;

/**
 * Adapteur pour les arrêts.
 */
public class ArretAdapter extends CursorAdapter {

	private String nomCourtRoute;

	public ArretAdapter(Context context, Cursor cursor, Route route) {
		super(context, cursor);
		nomCourtRoute = route.getNomCourt();
	}

	@Override
	public void bindView(final View view, final Context context, final Cursor cursor) {
		final int nameCol = cursor.getColumnIndex("arretName");
		final String name = cursor.getString(nameCol);
		final int directionCol = cursor.getColumnIndex("direction");
		final String direction = cursor.getString(directionCol);
		((TextView) view.findViewById(R.id.nomArret)).setText(name);
		((TextView) view.findViewById(R.id.directionArret)).setText(Formatteur.formatterChaine(direction.replaceAll(nomCourtRoute, "")));
	}

	@Override
	public View newView(final Context context, final Cursor cursor, final ViewGroup parent) {
		final LayoutInflater inflater = LayoutInflater.from(context);
		return inflater.inflate(R.layout.arret, parent, false);

	}

}
