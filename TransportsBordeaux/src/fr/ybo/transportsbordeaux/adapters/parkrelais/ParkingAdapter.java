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
package fr.ybo.transportsbordeaux.adapters.parkrelais;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import fr.ybo.transportsbordeaux.R;
import fr.ybo.transportsbordeaux.database.modele.Parking;

/**
 * Adapteur pour les park relais.
 */
public class ParkingAdapter extends ArrayAdapter<Parking> {

    private final List<Parking> parkRelais;

    private static final double SEUIL_ROUGE = 0.25;

    private static final double SEUIL_ORANGE = 0.5;

    private final LayoutInflater inflater;

    public ParkingAdapter(Context context, List<Parking> objects) {
        super(context, R.layout.dispoparkrelai, objects);
        parkRelais = objects;
        inflater = LayoutInflater.from(getContext());
    }

    private static class ViewHolder {
        TextView dispoParkRelaiNom;
        TextView dispoParkRelaiDistance;
        TextView dispoParkRelaiText;
        ImageView icone;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View convertView1 = convertView;
        ParkingAdapter.ViewHolder holder;
        if (convertView1 == null) {
            convertView1 = inflater.inflate(R.layout.dispoparkrelai, null);
            holder = new ParkingAdapter.ViewHolder();
            holder.dispoParkRelaiNom = (TextView) convertView1.findViewById(R.id.dispoparkrelai_nom);
            holder.dispoParkRelaiDistance = (TextView) convertView1.findViewById(R.id.dispoparkrelai_distance);
            holder.dispoParkRelaiText = (TextView) convertView1.findViewById(R.id.dispoparkrelai_text);
            holder.icone = (ImageView) convertView1.findViewById(R.id.dispoparkrelai_image);
            convertView1.setTag(holder);
        } else {
            holder = (ParkingAdapter.ViewHolder) convertView1.getTag();
        }
        Parking parkRelai = parkRelais.get(position);
        holder.dispoParkRelaiNom.setText(parkRelai.name);
        holder.dispoParkRelaiDistance.setText(parkRelai.formatDistance());
        // Parc Relai ouvert.
        double poucentageDispo = (double) parkRelai.carParkAvailable.intValue()
                / (double) parkRelai.carParkCapacity.intValue();

        if (poucentageDispo < SEUIL_ROUGE) {
            holder.icone.setImageResource(R.drawable.dispo_parkrelai_rouge);
        } else if (poucentageDispo < SEUIL_ORANGE) {
            holder.icone.setImageResource(R.drawable.dispo_parkrelai_orange);
        } else {
            holder.icone.setImageResource(R.drawable.dispo_parkrelai_bleue);
        }

        holder.dispoParkRelaiText.setText(parkRelai.carParkAvailable + " / " + parkRelai.carParkCapacity);
        return convertView1;
    }
}
