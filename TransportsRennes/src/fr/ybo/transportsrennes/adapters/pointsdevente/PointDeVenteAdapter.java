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
package fr.ybo.transportsrennes.adapters.pointsdevente;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.keolis.modele.bus.PointDeVente;

/**
 * Adapteur pour les points de vente.
 */
public class PointDeVenteAdapter extends ArrayAdapter<PointDeVente> {

    private final List<PointDeVente> pointsDeVente;

    private final LayoutInflater inflater;

    public PointDeVenteAdapter(Context context, List<PointDeVente> objects) {
        super(context, R.layout.pointdevente, objects);
        pointsDeVente = objects;
        inflater = LayoutInflater.from(context);
    }

    private static class ViewHolder {
        TextView nom;
        TextView telephone;
        TextView distance;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View convertView1 = convertView;
        PointDeVenteAdapter.ViewHolder holder;
        if (convertView1 == null) {
            convertView1 = inflater.inflate(R.layout.pointdevente, null);
            holder = new PointDeVenteAdapter.ViewHolder();
            holder.nom = (TextView) convertView1.findViewById(R.id.pointdevente_nom);
            holder.telephone = (TextView) convertView1.findViewById(R.id.pointdevente_telephone);
            holder.distance = (TextView) convertView1.findViewById(R.id.pointdevente_distance);
            convertView1.setTag(holder);
        } else {
            holder = (PointDeVenteAdapter.ViewHolder) convertView1.getTag();
        }
        PointDeVente pointDeVente = pointsDeVente.get(position);

        holder.nom.setText(pointDeVente.name);
        holder.telephone.setText(pointDeVente.telephone);
        final String tel = pointDeVente.telephone;

        holder.telephone.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Uri uri = Uri.parse("tel:" + tel);
                getContext().startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        });
        holder.distance.setText(pointDeVente.formatDistance());
        return convertView1;
    }
}
