package fr.ybo.transportsrennes;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class DetailArretAdapter extends CursorAdapter {

	private final int now;

	public DetailArretAdapter(final Context context, final Cursor cursor, final int now) {
		super(context, cursor);
		this.now = now;
	}

	@Override
	public void bindView(final View view, final Context context, final Cursor cursor) {
		final int prochainDepartCol = cursor.getColumnIndex("_id");
		final int prochainDepart = cursor.getInt(prochainDepartCol);
		((TextView) view.findViewById(R.id.detailArret_heureProchain)).setText("A " + formatterCalendar(prochainDepart, 0)
				+ " dans " + formatterCalendar(prochainDepart, now));
	}

	private String formatterCalendar(final int prochainDepart, final int now) {
		final StringBuilder stringBuilder = new StringBuilder();
		final int tempsEnMinutes = prochainDepart - now;
		final int heures = tempsEnMinutes / 60;
		final int minutes = tempsEnMinutes - heures * 60;
		if (heures > 0) {
			stringBuilder.append(heures);
			stringBuilder.append(" heures ");
		}
		if (minutes > 0) {
			stringBuilder.append(minutes);
			stringBuilder.append(" minutes ");
		}
		if (stringBuilder.length() == 0) {
			stringBuilder.append("0 minutes");
		}
		return stringBuilder.toString();
	}

	@Override
	public View newView(final Context context, final Cursor cursor, final ViewGroup parent) {
		final LayoutInflater inflater = LayoutInflater.from(context);
		return inflater.inflate(R.layout.detailarretliste, parent, false);

	}

}
