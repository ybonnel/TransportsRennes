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
import fr.ybo.itineraires.schema.PortionTrajet;
import fr.ybo.itineraires.schema.PortionTrajetBus;
import fr.ybo.itineraires.schema.Trajet;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.util.IconeLigne;

import java.util.List;

public class TrajetAdapter extends ArrayAdapter<Trajet> {

	private List<Trajet> trajets;
	private final LayoutInflater inflater;

	public TrajetAdapter(Context context, List<Trajet> trajets) {
		super(context, R.layout.trajet, trajets);
		this.trajets = trajets;
		inflater = LayoutInflater.from(context);
	}

	public static class ViewHolder {
		public ImageView iconePortion1;
		public TextView heureDepart1;
		public TextView depart1;
		public TextView heureArrivee1;
		public TextView arrivee1;
		public ImageView iconePortion2;
		public TextView heureDepart2;
		public TextView depart2;
		public TextView heureArrivee2;
		public TextView arrivee2;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View convertViewLocal = convertView;
		TrajetAdapter.ViewHolder holder;
		if (convertViewLocal == null) {
			convertViewLocal = inflater.inflate(R.layout.trajet, null);
			holder = new TrajetAdapter.ViewHolder();
			holder.iconePortion1 = (ImageView) convertViewLocal.findViewById(R.id.iconePortion1);
			holder.heureDepart1 = (TextView) convertViewLocal.findViewById(R.id.departHeure1);
			holder.depart1 = (TextView) convertViewLocal.findViewById(R.id.depart1);
			holder.heureArrivee1 = (TextView) convertViewLocal.findViewById(R.id.arriveeHeure1);
			holder.arrivee1 = (TextView) convertViewLocal.findViewById(R.id.arrivee1);
			holder.iconePortion2 = (ImageView) convertViewLocal.findViewById(R.id.iconePortion2);
			holder.heureDepart2 = (TextView) convertViewLocal.findViewById(R.id.departHeure2);
			holder.depart2 = (TextView) convertViewLocal.findViewById(R.id.depart2);
			holder.heureArrivee2 = (TextView) convertViewLocal.findViewById(R.id.arriveeHeure2);
			holder.arrivee2 = (TextView) convertViewLocal.findViewById(R.id.arrivee2);
			convertViewLocal.setTag(holder);
		} else {
			holder = (TrajetAdapter.ViewHolder) convertViewLocal.getTag();
		}
		Trajet trajet = trajets.get(position);
		PortionTrajetBus portionTrajetBus1 = null;
		PortionTrajetBus portionTrajetBus2 = null;
		for (PortionTrajet portion : trajet.getPortions()) {
			if (portion instanceof PortionTrajetBus) {
				if (portionTrajetBus1 == null) {
					portionTrajetBus1 = (PortionTrajetBus) portion;
				} else {
					portionTrajetBus2 = (PortionTrajetBus) portion;
				}
			}
		}
		holder.iconePortion1.setImageResource(IconeLigne.getIconeResource(portionTrajetBus1.getLigne().nomCourt));
		holder.heureDepart1.setText(portionTrajetBus1.heureDepart);
		holder.depart1.setText(portionTrajetBus1.getArretDepart().nom);
		holder.heureArrivee1.setText(portionTrajetBus1.heureArrivee);
		holder.arrivee1.setText(portionTrajetBus1.getArretArrivee().nom);
		if (portionTrajetBus2 == null) {
			holder.iconePortion2.setVisibility(View.INVISIBLE);
			holder.heureDepart2.setVisibility(View.INVISIBLE);
			holder.depart2.setVisibility(View.INVISIBLE);
			holder.heureArrivee2.setVisibility(View.INVISIBLE);
			holder.arrivee2.setVisibility(View.INVISIBLE);
		} else {
			holder.iconePortion2.setImageResource(IconeLigne.getIconeResource(portionTrajetBus2.getLigne().nomCourt));
			holder.heureDepart2.setText(portionTrajetBus2.heureDepart);
			holder.depart2.setText(portionTrajetBus2.getArretDepart().nom);
			holder.heureArrivee2.setText(portionTrajetBus2.heureArrivee);
			holder.arrivee2.setText(portionTrajetBus2.getArretArrivee().nom);
			holder.iconePortion2.setVisibility(View.VISIBLE);
			holder.heureDepart2.setVisibility(View.VISIBLE);
			holder.depart2.setVisibility(View.VISIBLE);
			holder.heureArrivee2.setVisibility(View.VISIBLE);
			holder.arrivee2.setVisibility(View.VISIBLE);
		}
		return convertViewLocal;
	}
}
