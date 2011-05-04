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

package fr.ybo.transportsbordeaux.adapters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import fr.ybo.transportsbordeaux.R;
import fr.ybo.transportsbordeaux.TransportsBordeauxApplication;
import fr.ybo.transportsbordeaux.modele.ArretFavori;
import fr.ybo.transportsbordeaux.util.IconeLigne;

public class FavoriAdapter extends BaseAdapter {

	private final LayoutInflater mInflater;

	private final List<ArretFavori> favoris;

	public FavoriAdapter(Context context, List<ArretFavori> favoris) {
		// Cache the LayoutInflate to avoid asking for a new one each time.
		mInflater = LayoutInflater.from(context);
		this.favoris = favoris;
	}

	public Collection<ArretFavori> getFavoris() {
		return favoris;
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
		ImageView moveUp;
		ImageView moveDown;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		View convertView1 = convertView;
		FavoriAdapter.ViewHolder holder;
		if (convertView1 == null) {
			convertView1 = mInflater.inflate(R.layout.favori, null);
			holder = new FavoriAdapter.ViewHolder();
			holder.iconeLigne = (ImageView) convertView1.findViewById(R.id.iconeLigne);
			holder.arret = (TextView) convertView1.findViewById(R.id.nomArret);
			holder.direction = (TextView) convertView1.findViewById(R.id.directionArret);
			holder.moveUp = (ImageView) convertView1.findViewById(R.id.moveUp);
			holder.moveDown = (ImageView) convertView1.findViewById(R.id.moveDown);

			convertView1.setTag(holder);
		} else {
			holder = (FavoriAdapter.ViewHolder) convertView1.getTag();
		}

		final ArretFavori favori = favoris.get(position);

		if (position == 0) {
			holder.moveUp.setVisibility(View.INVISIBLE);
		} else {
			holder.moveUp.setVisibility(View.VISIBLE);
			holder.moveUp.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					if (position > 0) {
						int autrePosition = position - 1;
						favoris.set(position, favoris.get(autrePosition));
						favoris.set(autrePosition, favori);
						favoris.get(position).ordre = position;
						ContentValues contentValues = new ContentValues();
						contentValues.put("ordre", position);
						List<String> whereArgs = new ArrayList<String>(2);
						whereArgs.add(favoris.get(position).arretId);
						whereArgs.add(favoris.get(position).ligneId);
						whereArgs.add(Integer.toString(favoris.get(position).macroDirection));
						String whereClause = "arretId = :arretId and ligneId = :ligneId and macroDirection = :macroDirection";
						TransportsBordeauxApplication.getDataBaseHelper().getWritableDatabase()
								.update("ArretFavori", contentValues, whereClause, whereArgs.toArray(new String[3]));
						favoris.get(autrePosition).ordre = autrePosition;
						contentValues.put("ordre", autrePosition);
						whereArgs.clear();
						whereArgs.add(favoris.get(autrePosition).arretId);
						whereArgs.add(favoris.get(autrePosition).ligneId);
						whereArgs.add(Integer.toString(favoris.get(autrePosition).macroDirection));
						TransportsBordeauxApplication.getDataBaseHelper().getWritableDatabase()
								.update("ArretFavori", contentValues, whereClause, whereArgs.toArray(new String[3]));
						notifyDataSetChanged();
					}
				}
			});

		}
		if (position == favoris.size() - 1) {
			holder.moveDown.setVisibility(View.INVISIBLE);
		} else {
			holder.moveDown.setVisibility(View.VISIBLE);
			holder.moveDown.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					if (position < favoris.size() - 1) {
						int autrePosition = position + 1;
						favoris.set(position, favoris.get(autrePosition));
						favoris.set(autrePosition, favori);
						favoris.get(position).ordre = position;
						ContentValues contentValues = new ContentValues();
						contentValues.put("ordre", position);
						List<String> whereArgs = new ArrayList<String>(2);
						whereArgs.add(favoris.get(position).arretId);
						whereArgs.add(favoris.get(position).ligneId);
						whereArgs.add(Integer.toString(favoris.get(position).macroDirection));
						String whereClause = "arretId = :arretId and ligneId = :ligneId and macroDirection = :macroDirection";
						TransportsBordeauxApplication.getDataBaseHelper().getWritableDatabase()
								.update("ArretFavori", contentValues, whereClause, whereArgs.toArray(new String[3]));
						favoris.get(autrePosition).ordre = autrePosition;
						contentValues.put("ordre", autrePosition);
						whereArgs.clear();
						whereArgs.add(favoris.get(autrePosition).arretId);
						whereArgs.add(favoris.get(autrePosition).ligneId);
						whereArgs.add(Integer.toString(favoris.get(autrePosition).macroDirection));
						TransportsBordeauxApplication.getDataBaseHelper().getWritableDatabase()
								.update("ArretFavori", contentValues, whereClause, whereArgs.toArray(new String[3]));
						notifyDataSetChanged();
					}
				}
			});
		}

		holder.arret.setText(favori.nomArret);
		holder.direction.setText(favori.direction);
		holder.iconeLigne.setImageResource(IconeLigne.getIconeResource(favori.nomCourt));

		return convertView1;
	}

}
