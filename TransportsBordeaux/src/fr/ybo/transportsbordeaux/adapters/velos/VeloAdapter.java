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
package fr.ybo.transportsbordeaux.adapters.velos;

import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import fr.ybo.transportsbordeaux.R;
import fr.ybo.transportsbordeaux.tbcapi.modele.Station;
import fr.ybo.transportscommun.AbstractTransportsApplication;
import fr.ybo.transportscommun.util.Formatteur;

/**
 * Adapteur pour les alerts.
 */
public class VeloAdapter extends ArrayAdapter<Station> {

    public Collection<Station> getStations() {
        return stations;
    }

    private final List<Station> stations;

    private static final double SEUIL_ROUGE = 0.25;

    private static final double SEUIL_ORANGE = 0.5;

    private final LayoutInflater inflater;

    public VeloAdapter(Context context, List<Station> objects) {
        super(context, R.layout.dispovelo, objects);
        stations = objects;
        inflater = LayoutInflater.from(getContext());
    }

    private static class ViewHolder {
		TextView icone;
        TextView dispoVeloText;
        TextView dispoVeloStation;
        TextView dispoVeloDistance;
        ImageView iconeCb;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View convertView1 = convertView;
        VeloAdapter.ViewHolder holder;
        if (convertView1 == null) {
            convertView1 = inflater.inflate(R.layout.dispovelo, null);
            holder = new VeloAdapter.ViewHolder();
			holder.icone = (TextView) convertView1.findViewById(R.id.itemSymbole);
            holder.dispoVeloText = (TextView) convertView1.findViewById(R.id.dispovelo_text);
            holder.dispoVeloStation = (TextView) convertView1.findViewById(R.id.dispovelo_station);
            holder.dispoVeloDistance = (TextView) convertView1.findViewById(R.id.dispovelo_distance);
            holder.iconeCb = (ImageView) convertView1.findViewById(R.id.dispovelo_cb);
            convertView1.setTag(holder);
        } else {
            holder = (VeloAdapter.ViewHolder) convertView1.getTag();
        }
		holder.dispoVeloStation.setTextColor(AbstractTransportsApplication.getTextColor(getContext()));
		holder.dispoVeloDistance.setTextColor(AbstractTransportsApplication.getTextColor(getContext()));
        Station station = stations.get(position);
        if (station.isOpen) {
            int placesTotales = station.availableBikes + station.freeSlots;
            double poucentageDispo = (double) station.availableBikes / (double) placesTotales;
            if (poucentageDispo < SEUIL_ROUGE) {
				holder.icone.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.item_symbol_red));
            } else if (poucentageDispo < SEUIL_ORANGE) {
				holder.icone.setBackgroundDrawable(getContext().getResources().getDrawable(
						R.drawable.item_symbol_orange));
            } else {
				holder.icone
						.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.item_symbol_blue));
            }
            holder.dispoVeloText.setText(station.availableBikes + " / " + placesTotales);
        } else {
			holder.icone.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.item_symbol_red));
            holder.dispoVeloText.setText("Fermée");
        }
        holder.dispoVeloStation.setText(Formatteur.formatterChaine(station.name));
        holder.dispoVeloDistance.setText(station.formatDistance());
		// if (station.isPayment) {
		// holder.iconeCb.setVisibility(View.VISIBLE);
		// } else {
            holder.iconeCb.setVisibility(View.INVISIBLE);
		// }
        return convertView1;
    }
}
