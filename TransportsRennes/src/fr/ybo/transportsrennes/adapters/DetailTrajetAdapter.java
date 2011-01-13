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

package fr.ybo.transportsrennes.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import fr.ybo.transportsrennes.R;

public class DetailTrajetAdapter extends CursorAdapter {

	int arretNomCol;
	int prochainDepartCol;

	public DetailTrajetAdapter(Context context, Cursor cursor) {
		super(context, cursor);
		arretNomCol = cursor.getColumnIndex("nom");
		prochainDepartCol = cursor.getColumnIndex("heureDepart");
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		int prochainDepart = cursor.getInt(prochainDepartCol);
		String arretNom = cursor.getString(arretNomCol);
		((TextView) view.findViewById(R.id.detailTrajet_arretNom)).setText(arretNom);
		((TextView) view.findViewById(R.id.detailTrajet_heurePassage)).setText(formatterCalendarHeure(prochainDepart));
	}

	private String formatterCalendarHeure(int prochainDepart) {
		StringBuilder stringBuilder = new StringBuilder();
		int tempsEnMinutes = prochainDepart;
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
		return inflater.inflate(R.layout.detailtrajetliste, parent, false);
	}

}
