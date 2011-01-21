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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.keolis.gtfs.modele.ArretFavori;
import fr.ybo.transportsrennes.util.IconeLigne;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FavoriAdapterForWidget extends BaseAdapter {
	private final static Class<?> classDrawable = R.drawable.class;

	private final LayoutInflater mInflater;

	private final List<ArretFavori> favoris;

	private final Context mContext;

	private final List<Integer> favorisSelectionnes = new ArrayList<Integer>();

	public List<ArretFavori> getFavorisSelectionnes() {
		List<ArretFavori> retour = new ArrayList<ArretFavori>(2);
		for (int position : favorisSelectionnes) {
			retour.add(favoris.get(position));
		}
		return retour;
	}

	public FavoriAdapterForWidget(final Context context, final List<ArretFavori> favoris) {
		// Cache the LayoutInflate to avoid asking for a new one each time.
		mInflater = LayoutInflater.from(context);
		mContext = context;
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

	static class ViewHolder {
		ImageView iconeLigne;
		TextView arret;
		TextView direction;
		CheckBox checkBox;
	}

	public View getView(final int position, View convertView, final ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.favori_for_widget, null);

			holder = new ViewHolder();
			holder.iconeLigne = (ImageView) convertView.findViewById(R.id.iconeLigne);
			holder.arret = (TextView) convertView.findViewById(R.id.nomArret);
			holder.direction = (TextView) convertView.findViewById(R.id.directionArret);
			holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		ArretFavori favori = favoris.get(position);

		holder.arret.setText(favori.nomArret);
		holder.direction.setText(favori.direction);
		holder.iconeLigne.setImageResource(IconeLigne.getIconeResource(favori.nomCourt));
		holder.checkBox.setChecked(favorisSelectionnes.contains(new Integer(position)));
		holder.checkBox.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on clicks, depending on whether it's now checked
				CheckBox checkBox = (CheckBox) v;
				if (checkBox.isChecked()) {
					if (favorisSelectionnes.size() < 3) {
						favorisSelectionnes.add(position);
					} else {
						Toast.makeText(mContext, "Déjà trois arrêts favoris sélectionnés", Toast.LENGTH_SHORT).show();
						checkBox.setChecked(false);
						checkBox.invalidate();
					}
				} else {
					Iterator<Integer> positionActuels = favorisSelectionnes.iterator();
					while (positionActuels.hasNext()) {
						if (positionActuels.next() == position) {
							positionActuels.remove();
						}
					}
				}
			}
		});
		return convertView;
	}

}
