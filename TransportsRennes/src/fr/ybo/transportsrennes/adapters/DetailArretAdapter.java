/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
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

public class DetailArretAdapter extends CursorAdapter {

	private final int now;

	private final LayoutInflater inflater;

	private final Context myContext;

	public DetailArretAdapter(Context context, Cursor cursor, int now) {
		super(context, cursor);
		myContext = context;
		this.now = now;
		inflater = LayoutInflater.from(context);
		prochainDepartCol = cursor.getColumnIndex("_id");
	}

	private static class ViewHolder {
		TextView heureProchain;
		TextView tempsRestant;
	}

	private final int prochainDepartCol;

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = inflater.inflate(R.layout.detailarretliste, parent, false);
		DetailArretAdapter.ViewHolder holder = new DetailArretAdapter.ViewHolder();
		holder.heureProchain = (TextView) view.findViewById(R.id.detailArret_heureProchain);
		holder.tempsRestant = (TextView) view.findViewById(R.id.detailArret_tempsRestant);
		view.setTag(holder);
		return view;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		int prochainDepart = cursor.getInt(prochainDepartCol);
		((DetailArretAdapter.ViewHolder) view.getTag()).heureProchain.setText(formatterCalendarHeure(prochainDepart));
		((DetailArretAdapter.ViewHolder) view.getTag()).tempsRestant.setText(formatterCalendar(prochainDepart, now));
	}

	@SuppressWarnings({"TypeMayBeWeakened"})
	private CharSequence formatterCalendar(int prochainDepart, int now) {
		StringBuilder stringBuilder = new StringBuilder();
		int tempsEnMinutes = prochainDepart - now;
		if (tempsEnMinutes < 0) {
			stringBuilder.append(myContext.getString(R.string.tropTard));
		} else {
			stringBuilder.append(myContext.getString(R.string.dans));
			stringBuilder.append(' ');
			int heures = tempsEnMinutes / 60;
			int minutes = tempsEnMinutes - heures * 60;
			boolean tempsAjoute = false;
			if (heures > 0) {
				stringBuilder.append(heures);
				stringBuilder.append(' ');
				stringBuilder.append(myContext.getString(R.string.heures));
				stringBuilder.append(' ');
				tempsAjoute = true;
			}
			if (minutes > 0) {
				stringBuilder.append(minutes);
				stringBuilder.append(' ');
				stringBuilder.append(myContext.getString(R.string.minutes));
				tempsAjoute = true;
			}
			if (!tempsAjoute) {
				stringBuilder.append("0 ");
				stringBuilder.append(myContext.getString(R.string.minutes));
			}
		}
		return stringBuilder.toString();
	}

	@SuppressWarnings({"TypeMayBeWeakened"})
	private CharSequence formatterCalendarHeure(int prochainDepart) {
		StringBuilder stringBuilder = new StringBuilder();
		int heures = prochainDepart / 60;
		int minutes = prochainDepart - heures * 60;
		if (heures >= 24) {
			heures -= 24;
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

}
