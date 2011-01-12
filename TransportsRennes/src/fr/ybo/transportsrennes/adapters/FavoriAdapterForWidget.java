/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.keolis.gtfs.modele.ArretFavori;

import java.lang.reflect.Field;
import java.util.List;

public class FavoriAdapterForWidget extends BaseAdapter {
	private final static Class<?> classDrawable = R.drawable.class;

	static class ViewHolder {
		LinearLayout conteneur;
		TextView arret;
		TextView direction;
	}

	private final LayoutInflater mInflater;

	private final List<ArretFavori> favoris;

	public FavoriAdapterForWidget(final Context context, final List<ArretFavori> favoris) {
		// Cache the LayoutInflate to avoid asking for a new one each time.
		mInflater = LayoutInflater.from(context);
		this.favoris = favoris;
	}

	public int getCount() {
		return favoris.size();
	}

	public ArretFavori getItem(final int position) {
		return favoris.get(position);
	}

	public long getItemId(final int position) {
		return position;
	}

	public View getView(final int position, View convertView, final ViewGroup parent) {
		ViewHolder holder;


		convertView = mInflater.inflate(R.layout.favori_for_widget, null);

		holder = new ViewHolder();
		holder.conteneur = (LinearLayout) convertView.findViewById(R.id.conteneurImage);
		holder.arret = (TextView) convertView.findViewById(R.id.nomArret);
		holder.direction = (TextView) convertView.findViewById(R.id.directionArret);

		convertView.setTag(holder);

		ArretFavori favori = favoris.get(position);

		holder.arret.setText(favori.nomArret);
		holder.direction.setText(favori.direction);
		try {
			Field fieldIcon = classDrawable.getDeclaredField("i" + favori.nomCourt.toLowerCase());
			int ressourceImg = fieldIcon.getInt(null);
			ImageView imgView = new ImageView(mInflater.getContext());
			imgView.setImageResource(ressourceImg);
			holder.conteneur.addView(imgView);
		} catch (NoSuchFieldException e) {
			TextView textView = new TextView(mInflater.getContext());
			textView.setTextSize(16);
			textView.setText(favori.nomCourt);
			holder.conteneur.addView(textView);
		} catch (IllegalAccessException e) {
			TextView textView = new TextView(mInflater.getContext());
			textView.setTextSize(16);
			textView.setText(favori.nomCourt);
			holder.conteneur.addView(textView);
		}
		return convertView;
	}

}
