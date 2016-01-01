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

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import fr.ybo.transportsbordeaux.R;
import fr.ybo.transportscommun.donnees.modele.Ligne;
import fr.ybo.transportscommun.util.IconeLigne;

public class LigneAdapter extends BaseAdapter {

    static class ViewHolder {
        TextView nomLong;
        ImageView iconeLigne;
    }

    private final LayoutInflater mInflater;

    private final List<Ligne> lignes;

    public LigneAdapter(final Context context, final List<Ligne> lignes) {
        mInflater = LayoutInflater.from(context);
        this.lignes = lignes;
    }

    @Override
    public int getCount() {
        return lignes.size();
    }

    @Override
    public Ligne getItem(final int position) {
        return lignes.get(position);
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View convertView1 = convertView;
        final ViewHolder holder;
        if (convertView1 == null) {
            convertView1 = mInflater.inflate(R.layout.ligne, null);
            holder = new ViewHolder();
            holder.iconeLigne = (ImageView) convertView1.findViewById(R.id.iconeLigne);
            holder.nomLong = (TextView) convertView1.findViewById(R.id.nomLong);
            convertView1.setTag(holder);
        } else {
            holder = (ViewHolder) convertView1.getTag();
        }
        final Ligne ligne = lignes.get(position);
        holder.nomLong.setText(ligne.nomLong);
        try {
            holder.iconeLigne.setImageResource(IconeLigne.getIconeResource(ligne.nomCourt));
        } catch (final Exception ignore) {
        }
        return convertView1;
    }
}
