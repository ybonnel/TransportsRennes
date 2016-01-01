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
package fr.ybo.transportsbordeaux.adapters.itineraires;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import fr.ybo.transportsbordeaux.R;
import fr.ybo.transportsbordeaux.itineraires.PortionTrajet;
import fr.ybo.transportsbordeaux.itineraires.Trajet;
import fr.ybo.transportscommun.util.IconeLigne;

public class TrajetAdapter extends ArrayAdapter<Trajet> {

    private final LayoutInflater inflater;
    private final int heureDepart;
    private final Context context;

    public TrajetAdapter(final Context context, final List<Trajet> trajets, final int heureDepart) {
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
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View convertViewLocal = convertView;
        final ViewHolder holder;
        if (convertViewLocal == null) {
            convertViewLocal = inflater.inflate(R.layout.trajet, null);
            holder = new ViewHolder();
            holder.departPieton = (TextView) convertViewLocal.findViewById(R.id.departPieton);
            holder.arriveePieton = (TextView) convertViewLocal.findViewById(R.id.arriveePieton);
            holder.layoutTrajets = (LinearLayout) convertViewLocal.findViewById(R.id.layoutTrajets);
            convertViewLocal.setTag(holder);
        } else {
            holder = (ViewHolder) convertViewLocal.getTag();
        }
        final Trajet trajet = getItem(position);
        holder.departPieton.setText(context.getString(R.string.depart, formatHeure(heureDepart)));
        holder.arriveePieton.setText(context.getString(R.string.arrivee, SDF_HEURE.format(trajet.getEndTime())));
        holder.layoutTrajets.removeAllViews();
        for (final PortionTrajet portion : trajet.getPortions()) {
            final View portionLayout = inflater.inflate(R.layout.portion_trajet, null);
            final int icone;
            final TextView directionTrajet = (TextView) portionLayout.findViewById(R.id.directionTrajet);
            if (portion.getMode().isOnStreetNonTransit()) {
                icone = R.drawable.ipieton;
                directionTrajet.setVisibility(View.GONE);
            } else {
                directionTrajet.setVisibility(View.VISIBLE);
                final String ligneId = portion.getLigneId();
                icone = ligneId.length() == 1 && ligneId.charAt(0) >= '1' && ligneId.charAt(0) <= '9' ? IconeLigne.getIconeResource('0' + ligneId) : IconeLigne.getIconeResource(ligneId);
                directionTrajet.setText(context.getString(R.string.directionEntete) + ' ' + portion.getDirection());
            }
            ((ImageView) portionLayout.findViewById(R.id.iconePortion)).setImageResource(icone);
            ((TextView) portionLayout.findViewById(R.id.departHeure)).setText(SDF_HEURE.format(portion.getStartTime()));
            ((TextView) portionLayout.findViewById(R.id.depart)).setText(portion.getFromName());
            ((TextView) portionLayout.findViewById(R.id.arriveeHeure)).setText(SDF_HEURE.format(portion.getEndTime()));
            ((TextView) portionLayout.findViewById(R.id.arrivee)).setText(portion.getToName());
            holder.layoutTrajets.addView(portionLayout);
        }
        return convertViewLocal;
    }

    private static final DateFormat SDF_HEURE = new SimpleDateFormat("HH:mm");

    private static CharSequence formatHeure(final int time) {
        final StringBuilder stringBuilder = new StringBuilder();
        final int heure = time / 60;
        final int minutes = time - heure * 60;
        if (heure < 10) {
            stringBuilder.append('0');
        }
        stringBuilder.append(heure).append(':');
        if (minutes < 10) {
            stringBuilder.append('0');
        }
        return stringBuilder.append(minutes);
    }
}
