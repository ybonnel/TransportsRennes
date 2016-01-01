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
package fr.ybo.transportsrennes.adapters.alerts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import fr.ybo.transportscommun.util.IconeLigne;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.keolis.modele.bus.Alert;

import java.util.List;

/**
 * Adapteur pour les alerts.
 */
public class AlertAdapter extends ArrayAdapter<Alert> {

    private final List<Alert> alerts;
    private final LayoutInflater inflater;

    public AlertAdapter(final Context context, final List<Alert> objects) {
        super(context, R.layout.alert, objects);
        alerts = objects;
        inflater = LayoutInflater.from(getContext());
    }

    private static class ViewHolder {
        private TextView titreAlerte;
        private ImageView iconeLigne;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View convertView1 = convertView;
        final Alert alert = alerts.get(position);
        final ViewHolder holder;
        if (convertView1 == null) {
            convertView1 = inflater.inflate(R.layout.alert, null);
            holder = new ViewHolder();
            holder.titreAlerte = (TextView) convertView1.findViewById(R.id.titreAlert);
            holder.iconeLigne = (ImageView) convertView1.findViewById(R.id.iconeLigne);
            convertView1.setTag(holder);
        } else {
            holder = (ViewHolder) convertView1.getTag();
        }

        holder.titreAlerte.setText(alert.getTitleFormate());
        if (!alert.lines.isEmpty()) {
            holder.iconeLigne.setImageResource(IconeLigne.getIconeResource(alert.lines.iterator().next()));
        }
        return convertView1;
    }
}
