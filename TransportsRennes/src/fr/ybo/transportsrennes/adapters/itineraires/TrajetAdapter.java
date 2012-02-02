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
package fr.ybo.transportsrennes.adapters.itineraires;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import fr.ybo.transportscommun.util.IconeLigne;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.itineraires.PortionTrajet;
import fr.ybo.transportsrennes.itineraires.Trajet;

import java.text.SimpleDateFormat;
import java.util.List;

public class TrajetAdapter extends ArrayAdapter<Trajet> {

    private final LayoutInflater inflater;
    private int heureDepart;
    private Context context;

    public TrajetAdapter(Context context, List<Trajet> trajets, int heureDepart) {
        super(context, R.layout.trajet, trajets);
        this.heureDepart = heureDepart;
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
        Trajet trajet = getItem(position);
        holder.departPieton.setText(context.getString(R.string.depart, formatHeure(heureDepart)));
        holder.arriveePieton.setText(context.getString(R.string.arrivee, SDF_HEURE.format(trajet.getEndTime())));
        holder.layoutTrajets.removeAllViews();
        for (PortionTrajet portionTrajet : trajet.getPortions()) {
            RelativeLayout portionLayout = (RelativeLayout) inflater.inflate(R.layout.portion_trajet, null);
            int icone;
            TextView directionTrajet = (TextView) portionLayout.findViewById(R.id.directionTrajet);
            if (portionTrajet.getMode().isOnStreetNonTransit()) {
                icone = R.drawable.ipieton;
                directionTrajet.setVisibility(View.GONE);
            } else {
                directionTrajet.setVisibility(View.VISIBLE);
                icone = IconeLigne.getIconeResource(portionTrajet.getLigneId());
                directionTrajet.setText(context.getString(R.string.directionEntete) + ' '
                        + portionTrajet.getDirection());
            }
            ((ImageView) portionLayout.findViewById(R.id.iconePortion)).setImageResource(icone);
            ((TextView) portionLayout.findViewById(R.id.departHeure)).setText(SDF_HEURE.format(portionTrajet
                    .getStartTime()));
            ((TextView) portionLayout.findViewById(R.id.depart)).setText(portionTrajet.getFromName());
            ((TextView) portionLayout.findViewById(R.id.arriveeHeure)).setText(SDF_HEURE.format(portionTrajet
                    .getEndTime()));
            ((TextView) portionLayout.findViewById(R.id.arrivee)).setText(portionTrajet.getToName());
            holder.layoutTrajets.addView(portionLayout);
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
