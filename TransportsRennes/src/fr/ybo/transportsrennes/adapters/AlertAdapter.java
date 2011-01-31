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
import fr.ybo.transportsrennes.keolis.modele.bus.Alert;
import fr.ybo.transportsrennes.util.IconeLigne;

import java.util.List;

/**
 * Adapteur pour les alerts.
 */
public class AlertAdapter extends ArrayAdapter<Alert> {

	private List<Alert> alerts;
	private LayoutInflater inflater;

	public AlertAdapter(Context context, List<Alert> objects) {
		super(context, R.layout.alert, objects);
		alerts = objects;
		inflater = LayoutInflater.from(getContext());
	}

	private static class ViewHolder {
		TextView titreAlerte;
		ImageView iconeLigne;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Alert alert = alerts.get(position);
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.alert, null);
			holder = new ViewHolder();
			holder.titreAlerte = (TextView) convertView.findViewById(R.id.titreAlert);
			holder.iconeLigne = (ImageView) convertView.findViewById(R.id.iconeLigne);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.titreAlerte.setText(alert.getTitleFormate());
		if (!alert.lines.isEmpty()) {
			holder.iconeLigne.setImageResource(IconeLigne.getIconeResource(alert.lines.iterator().next()));
		}
		return convertView;
	}
}
