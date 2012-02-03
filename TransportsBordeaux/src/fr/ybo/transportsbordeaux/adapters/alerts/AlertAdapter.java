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
package fr.ybo.transportsbordeaux.adapters.alerts;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import fr.ybo.transportsbordeaux.R;
import fr.ybo.transportsbordeaux.application.TransportsBordeauxApplication;
import fr.ybo.transportsbordeaux.database.modele.Alert;
import fr.ybo.transportscommun.donnees.modele.Ligne;
import fr.ybo.transportscommun.util.IconeLigne;

/**
 * Adapteur pour les alerts.
 */
public class AlertAdapter extends ArrayAdapter<Alert> {

    private final List<Alert> alerts;
    private final LayoutInflater inflater;

    public AlertAdapter(Context context, List<Alert> objects) {
        super(context, R.layout.alert, objects);
        alerts = objects;
        inflater = LayoutInflater.from(getContext());
    }

    private static class ViewHolder {
        private TextView titreAlerte;
        private ImageView iconeLigne;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View convertView1 = convertView;
        Alert alert = alerts.get(position);
        AlertAdapter.ViewHolder holder;
        if (convertView1 == null) {
            convertView1 = inflater.inflate(R.layout.alert, null);
            holder = new AlertAdapter.ViewHolder();
            holder.titreAlerte = (TextView) convertView1.findViewById(R.id.titreAlert);
            holder.iconeLigne = (ImageView) convertView1.findViewById(R.id.iconeLigne);
            convertView1.setTag(holder);
        } else {
            holder = (AlertAdapter.ViewHolder) convertView1.getTag();
        }

        holder.titreAlerte.setText(alert.title);
		Ligne ligneTmp = null;
		if (alert.ligne != null) {
			ligneTmp = new Ligne();
			ligneTmp.nomLong = alert.ligne;
			ligneTmp = TransportsBordeauxApplication.getDataBaseHelper().selectSingle(ligneTmp);
		}
        if (ligneTmp == null) {
			holder.iconeLigne.setVisibility(View.GONE);
        } else {
            holder.iconeLigne.setVisibility(View.VISIBLE);
            holder.iconeLigne.setImageResource(IconeLigne.getIconeResource(ligneTmp.nomCourt));
        }
        return convertView1;
    }
}
