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
import fr.ybo.itineraires.ItinerairesException;
import fr.ybo.itineraires.schema.PortionTrajet;
import fr.ybo.itineraires.schema.PortionTrajetBus;
import fr.ybo.itineraires.schema.Trajet;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Arret;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Ligne;
import fr.ybo.transportsrennes.util.IconeLigne;

import java.util.List;

public class TrajetAdapter extends ArrayAdapter<Trajet> {

    private List<Trajet> trajets;
    private final LayoutInflater inflater;
    private int heureDepart;
    private Context context;

    public TrajetAdapter(Context context, List<Trajet> trajets, int heureDepart) {
        super(context, R.layout.trajet, trajets);
        this.heureDepart = heureDepart;
        this.trajets = trajets;
        this.context = context;
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
        public TextView departPieton;
        public TextView arriveePieton;
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
            holder.departPieton = (TextView) convertViewLocal.findViewById(R.id.departPieton);
            holder.arriveePieton = (TextView) convertViewLocal.findViewById(R.id.arriveePieton);
            convertViewLocal.setTag(holder);
        } else {
            holder = (TrajetAdapter.ViewHolder) convertViewLocal.getTag();
        }
        Trajet trajet = trajets.get(position);
        PortionTrajetBus portionTrajetBus1 = null;
        PortionTrajetBus portionTrajetBus2 = null;
        for (PortionTrajet portion : trajet.getPortions()) {
            if (portion.getPortionTrajetBus() != null) {
                if (portionTrajetBus1 == null) {
                    portionTrajetBus1 = portion.getPortionTrajetBus();
                } else {
                    portionTrajetBus2 = portion.getPortionTrajetBus();
                }
            }
        }
        if (portionTrajetBus1 == null) {
            throw new ItinerairesException("Pas debus dans le trajet!!!!");
        }
        String heureArriveeBusChaine = (portionTrajetBus2 == null ? portionTrajetBus1 : portionTrajetBus2).getHeureArrivee();
        String[] champs = heureArriveeBusChaine.split(":");
        int heureArriveeBus = Integer.parseInt(champs[0]) * 60 + Integer.parseInt(champs[1]);
        PortionTrajet lastPortion = trajet.getPortions().get(trajet.getPortions().size() - 1);
        if (lastPortion.getJointurePieton() == null) {
            throw new ItinerairesException("La dernière portion n'est pas une portion pietonne!!!");
        }
        int heureArrivee = heureArriveeBus + lastPortion.getJointurePieton().getTempsTrajet();
        holder.departPieton.setText(context.getString(R.string.depart, formatHeure(heureDepart)));
        holder.arriveePieton.setText(context.getString(R.string.arrivee, formatHeure(heureArrivee)));
        holder.iconePortion1.setImageResource(IconeLigne.getIconeResource(Ligne.getLigne(portionTrajetBus1.getLigneId()).nomCourt));
        holder.heureDepart1.setText(portionTrajetBus1.getHeureDepart());
        holder.depart1.setText(Arret.getArret(portionTrajetBus1.getArretDepartId()).nom);
        holder.heureArrivee1.setText(portionTrajetBus1.getHeureArrivee());
        holder.arrivee1.setText(Arret.getArret(portionTrajetBus1.getArretArriveeId()).nom);
        if (portionTrajetBus2 == null) {
            holder.iconePortion2.setVisibility(View.GONE);
            holder.heureDepart2.setVisibility(View.GONE);
            holder.depart2.setVisibility(View.GONE);
            holder.heureArrivee2.setVisibility(View.GONE);
            holder.arrivee2.setVisibility(View.GONE);
        } else {
            holder.iconePortion2.setVisibility(View.VISIBLE);
            holder.heureDepart2.setVisibility(View.VISIBLE);
            holder.depart2.setVisibility(View.VISIBLE);
            holder.heureArrivee2.setVisibility(View.VISIBLE);
            holder.arrivee2.setVisibility(View.VISIBLE);
            holder.iconePortion2.setImageResource(IconeLigne.getIconeResource(Ligne.getLigne(portionTrajetBus2.getLigneId()).nomCourt));
            holder.heureDepart2.setText(portionTrajetBus2.getHeureDepart());
            holder.depart2.setText(Arret.getArret(portionTrajetBus2.getArretDepartId()).nom);
            holder.heureArrivee2.setText(portionTrajetBus2.getHeureArrivee());
            holder.arrivee2.setText(Arret.getArret(portionTrajetBus2.getArretArriveeId()).nom);
        }
        return convertViewLocal;
    }

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
