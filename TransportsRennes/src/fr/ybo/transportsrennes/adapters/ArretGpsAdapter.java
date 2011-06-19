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
 * 
 * Contributors:
 *     ybonnel - initial API and implementation
 */
package fr.ybo.transportsrennes.adapters;

import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Arret;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Horaire;
import fr.ybo.transportsrennes.util.IconeLigne;
import fr.ybo.transportsrennes.util.WidgetUpdateUtil;

/**
 * Adapteur pour les arrets pas positionnement GPS..
 */
public class ArretGpsAdapter extends ArrayAdapter<Arret> {

	private final List<Arret> arrets;
	private final LayoutInflater inflater;
	private final Calendar calendar;
	private final int now;
	private final Context myContext;

	public ArretGpsAdapter(Context context, List<Arret> objects) {
		super(context, R.layout.arretgps, objects);
		calendar = Calendar.getInstance();
		now = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
		arrets = objects;
		inflater = LayoutInflater.from(getContext());
		myContext = context;
	}

	private static class ViewHolder {
		ImageView iconeLigne;
		TextView arretDirection;
		TextView nomArret;
		TextView distance;
		TextView tempsRestant;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View convertView1 = convertView;
		Arret arret = arrets.get(position);
		ArretGpsAdapter.ViewHolder holder;
		if (convertView1 == null) {
			convertView1 = inflater.inflate(R.layout.arretgps, null);
			holder = new ArretGpsAdapter.ViewHolder();
			holder.iconeLigne = (ImageView) convertView1.findViewById(R.id.iconeLigne);
			holder.arretDirection = (TextView) convertView1.findViewById(R.id.arretgps_direction);
			holder.nomArret = (TextView) convertView1.findViewById(R.id.arretgps_nomArret);
			holder.distance = (TextView) convertView1.findViewById(R.id.arretgps_distance);
			holder.tempsRestant = (TextView) convertView1.findViewById(R.id.arretgps_tempsRestant);
			convertView1.setTag(holder);
		} else {
			holder = (ArretGpsAdapter.ViewHolder) convertView1.getTag();
		}
		holder.iconeLigne.setImageResource(IconeLigne.getIconeResource(arret.favori.nomCourt));
		holder.arretDirection.setText(arret.favori.direction);
		holder.nomArret.setText(arret.nom);
		holder.distance.setText(arret.formatDistance());
		holder.tempsRestant.setText(getTempsRestant(arret));
		return convertView1;
	}

	private CharSequence getTempsRestant(Arret arret) {
		try {
			List<Integer> prochainsDeparts = Horaire.getProchainHorairesAsList(arret.favori.ligneId,
					arret.favori.arretId, arret.favori.macroDirection, 1, calendar);
			return prochainsDeparts.isEmpty() ? "" : WidgetUpdateUtil.formatterCalendar(myContext,
					prochainsDeparts.get(0), now);
		} catch (SQLiteException ignore) {
			return "";
		}
	}
}
