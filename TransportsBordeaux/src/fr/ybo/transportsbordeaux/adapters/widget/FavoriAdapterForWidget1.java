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
package fr.ybo.transportsbordeaux.adapters.widget;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import fr.ybo.transportsbordeaux.R;
import fr.ybo.transportscommun.donnees.modele.ArretFavori;
import fr.ybo.transportscommun.util.IconeLigne;

public class FavoriAdapterForWidget1 extends BaseAdapter {

    private final LayoutInflater mInflater;

    private final List<ArretFavori> favoris;

    private Integer favoriSelectionne = null;

    public ArretFavori getFavoriSelectionne() {
        if (favoriSelectionne == null) {
            return null;
        }
        return favoris.get(favoriSelectionne);
    }
    
    public void setFavoriSelectionne(Integer favoriSelectionne) {
		this.favoriSelectionne = favoriSelectionne;
	}

    public FavoriAdapterForWidget1(Context context, List<ArretFavori> favoris) {
        // Cache the LayoutInflate to avoid asking for a new one each time.
        mInflater = LayoutInflater.from(context);
        this.favoris = favoris;
    }

    public int getCount() {
        return favoris.size();
    }

    public ArretFavori getItem(int position) {
        return favoris.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        ImageView iconeLigne;
        TextView arret;
        TextView direction;
        CheckBox checkBox;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View convertView1 = convertView;
        FavoriAdapterForWidget1.ViewHolder holder;
        if (convertView1 == null) {
            convertView1 = mInflater.inflate(R.layout.favori_for_widget, null);

            holder = new FavoriAdapterForWidget1.ViewHolder();
            holder.iconeLigne = (ImageView) convertView1.findViewById(R.id.iconeLigne);
            holder.arret = (TextView) convertView1.findViewById(R.id.nomArret);
            holder.direction = (TextView) convertView1.findViewById(R.id.directionArret);
            holder.checkBox = (CheckBox) convertView1.findViewById(R.id.checkbox);

            convertView1.setTag(holder);
        } else {
            holder = (FavoriAdapterForWidget1.ViewHolder) convertView1.getTag();
        }

        ArretFavori favori = favoris.get(position);

        holder.arret.setText(favori.nomArret);
        holder.direction.setText(favori.direction);
        holder.iconeLigne.setImageResource(IconeLigne.getIconeResource(favori.nomCourt));
        holder.checkBox.setChecked(Integer.valueOf(position).equals(favoriSelectionne));
        return convertView1;
    }

}
