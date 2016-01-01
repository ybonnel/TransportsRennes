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

import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import fr.ybo.transportsbordeaux.R;
import fr.ybo.transportscommun.AbstractTransportsApplication;
import fr.ybo.transportscommun.donnees.modele.Arret;
import fr.ybo.transportscommun.donnees.modele.Ligne;
import fr.ybo.transportscommun.donnees.modele.Notification;
import fr.ybo.transportscommun.util.IconeLigne;

public class NotifAdapter extends BaseAdapter {

    private final LayoutInflater mInflater;

    private final List<Notification> notifications;

    private int now;
    private final Context myContext;

    public NotifAdapter(final Context context, final List<Notification> notifications) {
        // Cache the LayoutInflate to avoid asking for a new one each time.
        mInflater = LayoutInflater.from(context);
        this.notifications = notifications;
        myContext = context;
        majCalendar();
    }


    public void majCalendar() {
        final Calendar calendar = Calendar.getInstance();
        now = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
    }

    @Override
    public int getCount() {
        return notifications.size();
    }

    @Override
    public Notification getItem(final int position) {
        return notifications.get(position);
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    public Collection<Notification> getNotifications() {
        return notifications;
    }

    static class ViewHolder {
        ImageView iconeLigne;
        TextView arret;
        TextView tempsRestant;
        TextView directionArret;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View convertView1 = convertView;
        final ViewHolder holder;
        if (convertView1 == null) {
            convertView1 = mInflater.inflate(R.layout.notif, null);
            holder = new ViewHolder();
            holder.iconeLigne = (ImageView) convertView1.findViewById(R.id.iconeLigne);
            holder.arret = (TextView) convertView1.findViewById(R.id.nomArret);
            holder.tempsRestant = (TextView) convertView1.findViewById(R.id.tempsRestant);
            holder.directionArret = (TextView) convertView1.findViewById(R.id.directionArret);

            convertView1.setTag(holder);
        } else {
            holder = (ViewHolder) convertView1.getTag();
        }
		holder.arret.setTextColor(AbstractTransportsApplication.getTextColor(myContext));
		holder.tempsRestant.setTextColor(AbstractTransportsApplication.getTextColor(myContext));
		holder.directionArret.setTextColor(AbstractTransportsApplication.getTextColor(myContext));

        final Notification notification = notifications.get(position);

        holder.arret.setText(Arret.getArret(notification.getArretId()).nom);
        holder.iconeLigne.setImageResource(IconeLigne.getIconeResource(Ligne.getLigne(notification.getLigneId()).nomCourt));
        holder.tempsRestant.setText(formatterCalendar(notification.getHeure(), now));
        holder.directionArret.setText(notification.getDirection());

        return convertView1;
    }


    private CharSequence formatterCalendar(final int prochainDepart, final int now) {
        final StringBuilder stringBuilder = new StringBuilder();
        int tempsEnMinutes = prochainDepart - now;
        if (tempsEnMinutes < 0) {
            tempsEnMinutes += 24 * 60;
        }
        final int heures = tempsEnMinutes / 60;
        final int minutes = tempsEnMinutes - heures * 60;
        boolean tempsAjoute = false;
        if (heures > 0) {
            stringBuilder.append(heures).append(' ').append(myContext.getString(R.string.miniHeures)).append(' ');
            tempsAjoute = true;
        }
        if (minutes > 0) {
            if (heures <= 0) {
                stringBuilder.append(minutes).append(' ').append(myContext.getString(R.string.miniMinutes));
            } else {
                if (minutes < 10) {
                    stringBuilder.append('0');
                }
                stringBuilder.append(minutes);
            }
            tempsAjoute = true;
        }
        if (!tempsAjoute) {
            stringBuilder.append("0 ").append(myContext.getString(R.string.miniMinutes));
        }
        return stringBuilder;
    }

}
