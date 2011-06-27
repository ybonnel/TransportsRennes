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
package fr.ybo.transportsbordeaux.adapters;

import java.text.SimpleDateFormat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import fr.ybo.opentripplanner.client.modele.Itinerary;
import fr.ybo.opentripplanner.client.modele.Leg;
import fr.ybo.opentripplanner.client.modele.TraverseMode;
import fr.ybo.opentripplanner.client.modele.TripPlan;
import fr.ybo.transportsbordeaux.R;
import fr.ybo.transportsbordeaux.util.IconeLigne;

public class TrajetAdapter extends ArrayAdapter<Itinerary> {

	private TripPlan tripPlan;
	private final LayoutInflater inflater;
	private int heureDepart;
	private Context context;

	public TrajetAdapter(Context context, TripPlan tripPlan, int heureDepart) {
		super(context, R.layout.trajet, tripPlan.itineraries.itinerary);
		this.heureDepart = heureDepart;
		this.tripPlan = tripPlan;
		this.context = context;
		inflater = LayoutInflater.from(context);
	}

	public static class ViewHolder {
		public TextView departPieton;
		public TextView arriveePieton;
		public LinearLayout layoutTrajets;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View convertViewLocal = convertView;
		TrajetAdapter.ViewHolder holder;
		if (convertViewLocal == null) {
			convertViewLocal = inflater.inflate(R.layout.trajet, null);
			holder = new TrajetAdapter.ViewHolder();
			holder.departPieton = (TextView) convertViewLocal.findViewById(R.id.departPieton);
			holder.arriveePieton = (TextView) convertViewLocal.findViewById(R.id.arriveePieton);
			holder.layoutTrajets = (LinearLayout) convertViewLocal.findViewById(R.id.layoutTrajets);
			convertViewLocal.setTag(holder);
		} else {
			holder = (TrajetAdapter.ViewHolder) convertViewLocal.getTag();
		}
		Itinerary trajet = tripPlan.itineraries.itinerary.get(position);
		holder.departPieton.setText(context.getString(R.string.depart, formatHeure(heureDepart)));
		holder.arriveePieton.setText(context.getString(R.string.arrivee, SDF_HEURE.format(trajet.endTime)));
		holder.layoutTrajets.removeAllViews();
		if (trajet.legs != null) {
			for (Leg leg : trajet.legs.leg) {
				RelativeLayout portionLayout = (RelativeLayout) inflater.inflate(R.layout.portion_trajet, null);
				int icone;
				TextView directionTrajet = (TextView) portionLayout.findViewById(R.id.directionTrajet);
				if (TraverseMode.valueOf(leg.mode).isOnStreetNonTransit()) {
					icone = R.drawable.ipieton;
					directionTrajet.setVisibility(View.GONE);
				} else {
					directionTrajet.setVisibility(View.VISIBLE);
					icone = IconeLigne.getIconeResource(leg.route);
					directionTrajet.setText(context.getString(R.string.directionEntete) + ' ' + leg.getDirection());
				}
				((ImageView) portionLayout.findViewById(R.id.iconePortion)).setImageResource(icone);
				((TextView) portionLayout.findViewById(R.id.departHeure)).setText(SDF_HEURE.format(leg.startTime));
				((TextView) portionLayout.findViewById(R.id.depart)).setText(leg.from.name);
				((TextView) portionLayout.findViewById(R.id.arriveeHeure)).setText(SDF_HEURE.format(leg.endTime));
				((TextView) portionLayout.findViewById(R.id.arrivee)).setText(leg.to.name);
				holder.layoutTrajets.addView(portionLayout);
			}
		}
		return convertViewLocal;
	}
	
	private static final SimpleDateFormat SDF_HEURE = new SimpleDateFormat("HH:mm");

	private String formatHeure(int time) {
		StringBuilder stringBuilder = new StringBuilder();
		int heure = time / 60;
		int minutes = time - heure * 60;
		if (heure < 10) {
			stringBuilder.append('0');
		}
		stringBuilder.append(heure);
		stringBuilder.append(':');
		if (minutes < 10) {
			stringBuilder.append('0');
		}
		stringBuilder.append(minutes);
		return stringBuilder.toString();
	}
}
