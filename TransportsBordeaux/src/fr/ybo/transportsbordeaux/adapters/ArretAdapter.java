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

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import fr.ybo.transportsbordeaux.R;
import fr.ybo.transportsbordeaux.TransportsBordeauxApplication;
import fr.ybo.transportsbordeaux.activity.OnClickFavoriGestionnaire;
import fr.ybo.transportsbordeaux.modele.ArretFavori;
import fr.ybo.transportsbordeaux.modele.Ligne;

/**
 * Adapteur pour les arrêts.
 */
public class ArretAdapter extends CursorAdapter {

	private final Ligne ligne;
	private final ArretFavori favori;

	public ArretAdapter(Context context, Cursor cursor, Ligne ligne) {
		super(context, cursor);
		this.ligne = ligne;
		favori = new ArretFavori();
		favori.ligneId = this.ligne.id;
		nameCol = cursor.getColumnIndex("arretName");
		directionCol = cursor.getColumnIndex("direction");
		arretIdCol = cursor.getColumnIndex("_id");
		macroDirectionCol = cursor.getColumnIndex("macroDirection");
	}

	private final int nameCol;
	private final int directionCol;
	private final int arretIdCol;
	private final int macroDirectionCol;

	private static class ViewHolder {
		private TextView nomArret;
		private TextView directionArret;
		private ImageView isFavori;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.arret, parent, false);
		ArretAdapter.ViewHolder holder = new ArretAdapter.ViewHolder();
		holder.nomArret = (TextView) view.findViewById(R.id.nomArret);
		holder.directionArret = (TextView) view.findViewById(R.id.directionArret);
		holder.isFavori = (ImageView) view.findViewById(R.id.isfavori);
		view.setTag(holder);
		return view;

	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		String name = cursor.getString(nameCol);
		String direction = cursor.getString(directionCol);
		favori.arretId = cursor.getString(arretIdCol);
		favori.macroDirection = cursor.getInt(macroDirectionCol);
		final ArretAdapter.ViewHolder holder = (ArretAdapter.ViewHolder) view.getTag();
		holder.nomArret.setText(name);
		holder.directionArret.setText(context.getString(R.string.vers) + ' ' + direction);
		holder.isFavori
				.setImageResource(TransportsBordeauxApplication.getDataBaseHelper().selectSingle(favori) == null ? android.R.drawable.btn_star_big_off
						: android.R.drawable.btn_star_big_on);
		holder.isFavori.setOnClickListener(new OnClickFavoriGestionnaire(ligne, favori.arretId, name, direction,
				favori.macroDirection));
	}

}
