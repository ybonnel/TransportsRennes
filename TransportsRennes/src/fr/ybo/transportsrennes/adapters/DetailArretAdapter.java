package fr.ybo.transportsrennes.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import fr.ybo.transportsrennes.R;

public class DetailArretAdapter extends CursorAdapter {

	private int now;

	public DetailArretAdapter(Context context, Cursor cursor, int now) {
		super(context, cursor);
		this.now = now;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		int prochainDepartCol = cursor.getColumnIndex("_id");
		int prochainDepart = cursor.getInt(prochainDepartCol);
		((TextView) view.findViewById(R.id.detailArret_heureProchain)).setText(formatterCalendarHeure(prochainDepart, 0));
		((TextView) view.findViewById(R.id.detailArret_tempsRestant)).setText(formatterCalendar(prochainDepart, now));
	}

	private String formatterCalendar(int prochainDepart, int now) {
		StringBuilder stringBuilder = new StringBuilder();
		int tempsEnMinutes = prochainDepart - now;
		if (tempsEnMinutes < 0) {
			stringBuilder.append("Trop tard!");
		} else {
			stringBuilder.append("dans ");
			int heures = tempsEnMinutes / 60;
			int minutes = tempsEnMinutes - heures * 60;
			boolean tempsAjoute = false;
			if (heures > 0) {
				stringBuilder.append(heures);
				stringBuilder.append(" heures ");
				tempsAjoute = true;
			}
			if (minutes > 0) {
				stringBuilder.append(minutes);
				stringBuilder.append(" minutes");
				tempsAjoute = true;
			}
			if (!tempsAjoute) {
				stringBuilder.append("0 minutes");
			}
		}
		return stringBuilder.toString();
	}

	private String formatterCalendarHeure(int prochainDepart, int now) {
		StringBuilder stringBuilder = new StringBuilder();
		int tempsEnMinutes = prochainDepart - now;
		int heures = tempsEnMinutes / 60;
		int minutes = tempsEnMinutes - heures * 60;
		if (heures >= 24) {
			heures = heures - 24;
		}
		String heuresChaine = Integer.toString(heures);
		String minutesChaine = Integer.toString(minutes);
		if (heuresChaine.length() < 2) {
			stringBuilder.append('0');
		}
		stringBuilder.append(heuresChaine);
		stringBuilder.append(':');
		if (minutesChaine.length() < 2) {
			stringBuilder.append('0');
		}
		stringBuilder.append(minutesChaine);
		return stringBuilder.toString();
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		final LayoutInflater inflater = LayoutInflater.from(context);
		return inflater.inflate(R.layout.detailarretliste, parent, false);

	}

}
