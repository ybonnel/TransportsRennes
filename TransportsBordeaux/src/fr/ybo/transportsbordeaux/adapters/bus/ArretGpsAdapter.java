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
package fr.ybo.transportsbordeaux.adapters.bus;

import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import fr.ybo.transportsbordeaux.R;
import fr.ybo.transportsbordeaux.database.modele.Arret;
import fr.ybo.transportsbordeaux.database.modele.DetailArretConteneur;
import fr.ybo.transportsbordeaux.database.modele.Horaire;
import fr.ybo.transportsbordeaux.util.IconeLigne;

import java.util.Calendar;
import java.util.List;

/**
 * Adapteur pour les arrets pas positionnement GPS..
 */
public class ArretGpsAdapter extends ArrayAdapter<Arret> {

    private final List<Arret> arrets;
    private final LayoutInflater inflater;
    private Calendar calendar;
    private int now;
    private final Context myContext;

    public ArretGpsAdapter(Context context, List<Arret> objects, Calendar calendar) {
        super(context, R.layout.arretgps, objects);
        this.calendar = calendar;
        now = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
        arrets = objects;
        inflater = LayoutInflater.from(getContext());
        myContext = context;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
        now = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
    }

    private static class ViewHolder {
        ImageView iconeLigne;
        TextView arretDirection;
        TextView nomArret;
        TextView distance;
        TextView tempsRestant;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View convertView1 = convertView;
        if (position >= arrets.size()) {
            return convertView;
        }
        Arret arret = arrets.get(position);
        ArretGpsAdapter.ViewHolder holder;
        if (convertView1 == null) {
            convertView1 = inflater.inflate(R.layout.arretgps, null);
            holder = new ArretGpsAdapter.ViewHolder();
            holder.iconeLigne = (ImageView) convertView1.findViewById(R.id.iconeLigne);
            holder.arretDirection = (TextView) convertView1.findViewById(R.id.arretgps_direction);
            holder.nomArret = (TextView) convertView1.findViewById(R.id.arretgps_nomArret);
            holder.distance = (TextView) convertView1.findViewById(R.id.arretgps_distance);
            holder.tempsRestant = (TextView) convertView1.findViewById(R.id.arretgps_tempsRestant);
            convertView1.setTag(holder);
        } else {
            holder = (ArretGpsAdapter.ViewHolder) convertView1.getTag();
        }
        holder.iconeLigne.setImageResource(IconeLigne.getIconeResource(arret.favori.nomCourt));
        holder.arretDirection.setText(arret.favori.direction);
        holder.nomArret.setText(arret.nom);
        holder.distance.setText(arret.formatDistance());
        holder.tempsRestant.setText(getTempsRestant(arret));
        return convertView1;
    }

    private CharSequence getTempsRestant(Arret arret) {
        try {
            List<DetailArretConteneur> prochainsDeparts = Horaire.getProchainHorairesAsList(arret.favori.ligneId,
                    arret.favori.arretId, 1, calendar);
            return prochainsDeparts.isEmpty() ? "" : formatterCalendar(prochainsDeparts.get(0).getHoraire(), now);
        } catch (SQLiteException ignore) {
            return "";
        }
    }


    private CharSequence formatterCalendar(int prochainDepart, int now) {
        StringBuilder stringBuilder = new StringBuilder();
        int tempsEnMinutes = prochainDepart - now;
        if (tempsEnMinutes < 0) {
            stringBuilder.append(myContext.getString(R.string.tropTard));
        } else {
            int heures = tempsEnMinutes / 60;
            int minutes = tempsEnMinutes - heures * 60;
            boolean tempsAjoute = false;
            if (heures > 0) {
                stringBuilder.append(heures);
                stringBuilder.append(' ');
                stringBuilder.append(myContext.getString(R.string.miniHeures));
                stringBuilder.append(' ');
                tempsAjoute = true;
            }
            if (minutes > 0) {
                if (heures <= 0) {
                    stringBuilder.append(minutes);
                    stringBuilder.append(' ');
                    stringBuilder.append(myContext
                            .getString(R.string.miniMinutes));
                } else {
                    if (minutes < 10) {
                        stringBuilder.append('0');
                    }
                    stringBuilder.append(minutes);
                }
                tempsAjoute = true;
            }
            if (!tempsAjoute) {
                stringBuilder.append("0 ");
                stringBuilder.append(myContext.getString(R.string.miniMinutes));
            }
        }
        return stringBuilder.toString();
    }
}
