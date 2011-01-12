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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Arret;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Adapteur pour les arrets pas positionnement GPS..
 */
public class ArretGpsAdapter extends ArrayAdapter<Arret> {

	private final static Class<?> classDrawable = R.drawable.class;

	private List<Arret> arrets;

	public ArretGpsAdapter(Context context, int textViewResourceId, List<Arret> objects) {
		super(context, textViewResourceId, objects);
		arrets = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater vi = LayoutInflater.from(getContext());
		View v = vi.inflate(R.layout.arretgps, null);
		Arret arret = arrets.get(position);
		LinearLayout conteneur = (LinearLayout) v.findViewById(R.id.conteneurImage);
		try {
			Field fieldIcon = classDrawable.getDeclaredField("i" + arret.favori.nomCourt.toLowerCase());
			int ressourceImg = fieldIcon.getInt(null);
			ImageView imgView = new ImageView(getContext());
			imgView.setImageResource(ressourceImg);
			conteneur.addView(imgView);
		} catch (NoSuchFieldException e) {
			TextView textView = new TextView(getContext());
			textView.setTextSize(16);
			textView.setText(arret.favori.nomCourt);
			conteneur.addView(textView);
		} catch (IllegalAccessException e) {
			TextView textView = new TextView(getContext());
			textView.setTextSize(16);
			textView.setText(arret.favori.nomCourt);
			conteneur.addView(textView);
		}
		TextView arretDirection = (TextView) v.findViewById(R.id.arretgps_direction);
		arretDirection.setText(arret.favori.direction);
		TextView nomArret = (TextView) v.findViewById(R.id.arretgps_nomArret);
		nomArret.setText(arret.nom);
		TextView distance = (TextView) v.findViewById(R.id.arretgps_distance);
		distance.setText(arret.formatDistance());
		return v;
	}
}
