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
import fr.ybo.transportsrennes.keolis.modele.bus.Alert;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Adapteur pour les alerts.
 */
public class AlertAdapter extends ArrayAdapter<Alert> {

	private List<Alert> alerts;

	private static final Class<R.drawable> classDrawable = R.drawable.class;

	public AlertAdapter(Context context, int textViewResourceId, List<Alert> objects) {
		super(context, textViewResourceId, objects);
		alerts = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater vi = LayoutInflater.from(getContext());
		View v = vi.inflate(R.layout.alert, null);
		Alert alert = alerts.get(position);

		TextView titreAlert = (TextView) v.findViewById(R.id.titreAlert);
		LinearLayout conteneur = (LinearLayout) v.findViewById(R.id.conteneurImage);
		titreAlert.setText(alert.getTitleFormate());
		for (String ligne : alert.lines) {
			try {
				Field fieldIcon = classDrawable.getDeclaredField("i" + ligne.toLowerCase());
				int ressourceImg = fieldIcon.getInt(null);
				ImageView imgView = (ImageView) vi.inflate(R.layout.imagebus, null);
				imgView.setImageResource(ressourceImg);
				conteneur.addView(imgView);
			} catch (NoSuchFieldException e) {
				TextView textView = new TextView(getContext());
				textView.setTextSize(16);
				textView.setText(ligne);
				conteneur.addView(textView);
			} catch (IllegalAccessException e) {
				TextView textView = new TextView(getContext());
				textView.setTextSize(16);
				textView.setText(ligne);
				conteneur.addView(textView);
			}
		}
		return v;
	}
}
