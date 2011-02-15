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

	private final LayoutInflater mInflater;

	private final List<ArretFavori> favoris;

	private final Context mContext;

	private final List<Integer> favorisSelectionnes = new ArrayList<Integer>(3);

	public List<ArretFavori> getFavorisSelectionnes() {
		final List<ArretFavori> retour = new ArrayList<ArretFavori>(3);
		for (final int position : favorisSelectionnes) {
			retour.add(favoris.get(position));
		}
		return retour;
	}

	public FavoriAdapterForWidget(final Context context, final List<ArretFavori> favoris) {
		super();
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

	public View getView(final int position, final View convertView, final ViewGroup parent) {
		View convertView1 = convertView;
		final FavoriAdapterForWidget.ViewHolder holder;
		if (convertView1 == null) {
			convertView1 = mInflater.inflate(R.layout.favori_for_widget, null);

			holder = new FavoriAdapterForWidget.ViewHolder();
			holder.iconeLigne = (ImageView) convertView1.findViewById(R.id.iconeLigne);
			holder.arret = (TextView) convertView1.findViewById(R.id.nomArret);
			holder.direction = (TextView) convertView1.findViewById(R.id.directionArret);
			holder.checkBox = (CheckBox) convertView1.findViewById(R.id.checkbox);

			convertView1.setTag(holder);
		} else {
			holder = (FavoriAdapterForWidget.ViewHolder) convertView1.getTag();
		}

		final ArretFavori favori = favoris.get(position);

		holder.arret.setText(favori.nomArret);
		holder.direction.setText(favori.direction);
		holder.iconeLigne.setImageResource(IconeLigne.getIconeResource(favori.nomCourt));
		holder.checkBox.setChecked(favorisSelectionnes.contains(Integer.valueOf(position)));
		holder.checkBox.setOnClickListener(new View.OnClickListener() {
			public void onClick(final View v) {
				// Perform action on clicks, depending on whether it's now checked
				final CheckBox checkBox = (CheckBox) v;
				if (checkBox.isChecked()) {
					if (favorisSelectionnes.size() < 3) {
						favorisSelectionnes.add(position);
					} else {
						Toast.makeText(mContext, mContext.getString(R.string.tooMuchFavoris), Toast.LENGTH_SHORT).show();
						checkBox.setChecked(false);
						checkBox.invalidate();
					}
				} else {
					final Iterator<Integer> positionActuels = favorisSelectionnes.iterator();
					while (positionActuels.hasNext()) {
						if (positionActuels.next() == position) {
							positionActuels.remove();
						}
					}
				}
			}
		});
		return convertView1;
	}

}
