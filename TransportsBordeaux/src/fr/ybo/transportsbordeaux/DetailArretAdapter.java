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

package fr.ybo.transportsbordeaux;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import fr.ybo.transportsbordeaux.tbc.Horaire;

/**
 * FIXME : A CODER.
 * @author ybonnel
 *
 */
public class DetailArretAdapter extends ArrayAdapter<Horaire> {

	private final int now;

	private final LayoutInflater inflater;

	private final Context myContext;

	public DetailArretAdapter(Context context, List<Horaire> horaires, int now, int resource) {
		super(context, resource, horaires);
		myContext = context;
		this.now = now;
		inflater = LayoutInflater.from(context);
	}

	private static class ViewHolder {
		TextView heureProchain;
		TextView tempsRestant;
	}
	

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
