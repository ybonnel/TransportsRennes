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

	public DetailArretAdapter(final Context context, final Cursor cursor, final int now) {
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
	public View newView(final Context context, final Cursor cursor, final ViewGroup parent) {
		final View view = inflater.inflate(R.layout.detailarretliste, parent, false);
		final DetailArretAdapter.ViewHolder holder = new DetailArretAdapter.ViewHolder();
		holder.heureProchain = (TextView) view.findViewById(R.id.detailArret_heureProchain);
		holder.tempsRestant = (TextView) view.findViewById(R.id.detailArret_tempsRestant);
		view.setTag(holder);
		return view;
	}

	@Override
	public void bindView(final View view, final Context context, final Cursor cursor) {
		final int prochainDepart = cursor.getInt(prochainDepartCol);
		((DetailArretAdapter.ViewHolder) view.getTag()).heureProchain.setText(formatterCalendarHeure(prochainDepart));
		((DetailArretAdapter.ViewHolder) view.getTag()).tempsRestant.setText(formatterCalendar(prochainDepart, now));
	}

	private CharSequence formatterCalendar(final int prochainDepart, final int now) {
		final StringBuilder stringBuilder = new StringBuilder();
		final int tempsEnMinutes = prochainDepart - now;
		if (tempsEnMinutes < 0) {
			stringBuilder.append(myContext.getString(R.string.tropTard));
		} else {
			stringBuilder.append(myContext.getString(R.string.dans));
			stringBuilder.append(' ');
			final int heures = tempsEnMinutes / 60;
			final int minutes = tempsEnMinutes - heures * 60;
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

	private CharSequence formatterCalendarHeure(final int prochainDepart) {
		final StringBuilder stringBuilder = new StringBuilder();
		int heures = prochainDepart / 60;
		final int minutes = prochainDepart - heures * 60;
		if (heures >= 24) {
			heures -= 24;
		}
		final String heuresChaine = Integer.toString(heures);
		final String minutesChaine = Integer.toString(minutes);
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
