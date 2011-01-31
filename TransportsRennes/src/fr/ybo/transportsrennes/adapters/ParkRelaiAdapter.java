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
import fr.ybo.transportsrennes.keolis.modele.bus.ParkRelai;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Adapteur pour les park relais.
 */
public class ParkRelaiAdapter extends ArrayAdapter<ParkRelai> {

	private List<ParkRelai> parkRelais;

	private static final double SEUIL_ROUGE = 0.25;

	private static final double SEUIL_ORANGE = 0.5;

	protected static final Map<Integer, String> MAP_STATES = new HashMap<Integer, String>();

	private LayoutInflater inflater;

	public ParkRelaiAdapter(Context context, List<ParkRelai> objects) {
		super(context, R.layout.dispoparkrelai, objects);
		if (MAP_STATES.isEmpty()) {
			MAP_STATES.put(1, context.getString(R.string.ferme));
			MAP_STATES.put(2, context.getString(R.string.complet));
			MAP_STATES.put(3, context.getString(R.string.indisponible));
		}
		parkRelais = objects;
		inflater = LayoutInflater.from(getContext());
	}

	private static class ViewHolder {
		TextView dispoParkRelaiNom;
		TextView dispoParkRelaiDistance;
		TextView dispoParkRelaiText;
		ImageView icone;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.dispoparkrelai, null);
			holder = new ViewHolder();
			holder.dispoParkRelaiNom = (TextView) convertView.findViewById(R.id.dispoparkrelai_nom);
			holder.dispoParkRelaiDistance = (TextView) convertView.findViewById(R.id.dispoparkrelai_distance);
			holder.dispoParkRelaiText = (TextView) convertView.findViewById(R.id.dispoparkrelai_text);
			holder.icone = (ImageView) convertView.findViewById(R.id.dispoparkrelai_image);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		ParkRelai parkRelai = parkRelais.get(position);
		holder.dispoParkRelaiNom.setText(parkRelai.name);
		holder.dispoParkRelaiDistance.setText(parkRelai.formatDistance());
		// Parc Relai ouvert.
		if (parkRelai.state == 0) {
			double poucentageDispo = ((double) parkRelai.carParkAvailable.intValue()) / ((double) (parkRelai.carParkCapacity.intValue()));


			if (poucentageDispo < SEUIL_ROUGE) {
				holder.icone.setImageResource(R.drawable.dispo_parkrelai_rouge);
			} else if (poucentageDispo < SEUIL_ORANGE) {
				holder.icone.setImageResource(R.drawable.dispo_parkrelai_orange);
			} else {
				holder.icone.setImageResource(R.drawable.dispo_parkrelai_bleue);
			}

			holder.dispoParkRelaiText.setText(parkRelai.carParkAvailable + " / " + parkRelai.carParkCapacity);
		} else {
			// Cas ou le park relai n'est pas disponible (complet, fermé, indisponible).
			holder.icone.setImageResource(R.drawable.dispo_parkrelai_rouge);
			holder.dispoParkRelaiText.setText(MAP_STATES.get(parkRelai.state));
		}
		return convertView;
	}
}
