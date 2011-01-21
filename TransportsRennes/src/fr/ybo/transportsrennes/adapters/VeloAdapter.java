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
import android.widget.TextView;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.keolis.modele.velos.Station;
import fr.ybo.transportsrennes.util.Formatteur;

import java.util.List;

/**
 * Adapteur pour les alerts.
 */
public class VeloAdapter extends ArrayAdapter<Station> {

	public List<Station> getStations() {
		return stations;
	}

	private List<Station> stations;

	private static final double SEUIL_ROUGE = 0.25;

	private static final double SEUIL_ORANGE = 0.5;

	private LayoutInflater inflater;

	public VeloAdapter(Context context, List<Station> objects) {
		super(context, R.layout.dispovelo, objects);
		stations = objects;
		inflater = LayoutInflater.from(getContext());
	}

	private static class ViewHolder {
		ImageView icone;
		TextView dispoVeloText;
		TextView dispoVeloStation;
		TextView dispoVeloDistance;
		ImageView iconeCb;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.dispovelo, null);
			holder = new ViewHolder();
			holder.icone =(ImageView) convertView.findViewById(R.id.dispovelo_image);
			holder.dispoVeloText =(TextView) convertView.findViewById(R.id.dispovelo_text);
			holder.dispoVeloStation =(TextView) convertView.findViewById(R.id.dispovelo_station);
			holder.dispoVeloDistance =(TextView) convertView.findViewById(R.id.dispovelo_distance);
			holder.iconeCb =(ImageView) convertView.findViewById(R.id.dispovelo_cb);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Station station = stations.get(position);
		int placesTotales = station.bikesavailable + station.slotsavailable;
		double poucentageDispo = ((double) station.bikesavailable) / ((double) (placesTotales));

		if (poucentageDispo < SEUIL_ROUGE) {
			holder.icone.setImageResource(R.drawable.dispo_velo_rouge);
		} else if (poucentageDispo < SEUIL_ORANGE) {
			holder.icone.setImageResource(R.drawable.dispo_velo_orange);
		} else {
			holder.icone.setImageResource(R.drawable.dispo_velo_bleue);
		}

		holder.dispoVeloText.setText(station.bikesavailable + " / " + placesTotales);
		holder.dispoVeloStation.setText(Formatteur.formatterChaine(station.name));
		holder.dispoVeloDistance.setText(station.formatDistance());
		if (station.pos) {
			holder.iconeCb.setVisibility(View.VISIBLE);
		} else {
			holder.iconeCb.setVisibility(View.INVISIBLE);
		}
		return convertView;
	}
}
