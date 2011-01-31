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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Arret;
import fr.ybo.transportsrennes.util.IconeLigne;

import java.util.List;

/**
 * Adapteur pour les arrets pas positionnement GPS..
 */
public class ArretGpsAdapter extends ArrayAdapter<Arret> {

	private List<Arret> arrets;
	private LayoutInflater inflater;

	public ArretGpsAdapter(Context context, List<Arret> objects) {
		super(context, R.layout.arretgps, objects);
		arrets = objects;
		inflater = LayoutInflater.from(getContext());
	}

	private static class ViewHolder {
		ImageView iconeLigne;
		TextView arretDirection;
		TextView nomArret;
		TextView distance;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Arret arret = arrets.get(position);
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.arretgps, null);
			holder = new ViewHolder();
			holder.iconeLigne = (ImageView) convertView.findViewById(R.id.iconeLigne);
			holder.arretDirection = (TextView) convertView.findViewById(R.id.arretgps_direction);
			holder.nomArret = (TextView) convertView.findViewById(R.id.arretgps_nomArret);
			holder.distance = (TextView) convertView.findViewById(R.id.arretgps_distance);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.iconeLigne.setImageResource(IconeLigne.getIconeResource(arret.favori.nomCourt));
		holder.arretDirection.setText(arret.favori.direction);
		holder.nomArret.setText(arret.nom);
		holder.distance.setText(arret.formatDistance());
		return convertView;
	}
}
