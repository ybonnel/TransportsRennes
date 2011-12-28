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
package fr.ybo.transportsrennes.adapters.bus;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.application.TransportsRennesApplication;

public class DetailArretAdapter extends BaseAdapter {

    private final int now;

    private final LayoutInflater inflater;
	private List<DetailArretConteneur> prochainsDeparts;

    private final Context myContext;

    private boolean isToday;

    public DetailArretAdapter(Context context, List<DetailArretConteneur> prochainsDeparts, int now, boolean isToday) {
        this.isToday = isToday;
        myContext = context;
        this.now = now;
        inflater = LayoutInflater.from(context);
		this.prochainsDeparts = prochainsDeparts;
    }

    private static class ViewHolder {
        TextView heureProchain;
        TextView tempsRestant;
    }

	public int getCount() {
		return prochainsDeparts.size();
	}

	public DetailArretConteneur getItem(int position) {
		return prochainsDeparts.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
		View convertView1 = convertView;
        DetailArretAdapter.ViewHolder holder;
        if (convertView1 == null) {
            convertView1 = inflater.inflate(R.layout.detailarretliste, parent, false);
            holder = new DetailArretAdapter.ViewHolder();
            holder.heureProchain = (TextView) convertView1.findViewById(R.id.detailArret_heureProchain);
            holder.tempsRestant = (TextView) convertView1.findViewById(R.id.detailArret_tempsRestant);
            convertView1.setTag(holder);
        } else {
            holder = (DetailArretAdapter.ViewHolder) convertView1.getTag();
        }
		int prochainDepart = prochainsDeparts.get(position).getHoraire();
        holder.heureProchain.setText(formatterCalendarHeure(prochainDepart));
		holder.heureProchain.setTextColor(TransportsRennesApplication.getTextColor(myContext));
		holder.tempsRestant.setTextColor(TransportsRennesApplication.getTextColor(myContext));
        if (isToday) {
            holder.tempsRestant.setText(formatterCalendar(prochainDepart, now));
        } else {
            holder.tempsRestant.setText("");
        }
        return convertView1;
    }

    private CharSequence formatterCalendar(int prochainDepart, int now) {
        StringBuilder stringBuilder = new StringBuilder();
        int tempsEnMinutes = prochainDepart - now;
        if (tempsEnMinutes < 0) {
            stringBuilder.append(myContext.getString(R.string.tropTard));
        } else {
            stringBuilder.append(myContext.getString(R.string.dans));
            stringBuilder.append(' ');
            int heures = tempsEnMinutes / 60;
            int minutes = tempsEnMinutes - heures * 60;
            boolean tempsAjoute = false;
            if (heures > 0) {
                stringBuilder.append(heures);
                stringBuilder.append(' ');
                stringBuilder.append(myContext.getString(R.string.heures));
                stringBuilder.append(' ');
                tempsAjoute = true;
            }
            if (minutes > 0) {
                stringBuilder.append(minutes);
                stringBuilder.append(' ');
                stringBuilder.append(myContext.getString(R.string.minutes));
                tempsAjoute = true;
            }
            if (!tempsAjoute) {
                stringBuilder.append("0 ");
                stringBuilder.append(myContext.getString(R.string.minutes));
            }
        }
        return stringBuilder.toString();
    }

    private CharSequence formatterCalendarHeure(int prochainDepart) {
        StringBuilder stringBuilder = new StringBuilder();
        int heures = prochainDepart / 60;
        int minutes = prochainDepart - heures * 60;
        if (heures >= 24) {
            heures -= 24;
        }
        String heuresChaine = Integer.toString(heures);
        String minutesChaine = Integer.toString(minutes);
        if (heuresChaine.length() < 2) {
            stringBuilder.append('0');
        }
        stringBuilder.append(heuresChaine);
        stringBuilder.append(':');
        if (minutesChaine.length() < 2) {
            stringBuilder.append('0');
        }
        stringBuilder.append(minutesChaine);
        return stringBuilder.toString();
    }

}
