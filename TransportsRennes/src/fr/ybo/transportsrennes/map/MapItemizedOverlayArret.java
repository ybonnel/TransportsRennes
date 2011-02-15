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

package fr.ybo.transportsrennes.map;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;
import fr.ybo.transportsrennes.DetailArret;
import fr.ybo.transportsrennes.keolis.gtfs.modele.ArretFavori;

import java.util.ArrayList;
import java.util.List;

public class MapItemizedOverlayArret extends ItemizedOverlay<OverlayItem> {

	//Liste des marqueurs
	private final ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>(20);
	private final Context mContext;
	private final List<ArretFavori> arretFavoris = new ArrayList<ArretFavori>(20);

	private static Drawable leftBottom(final Drawable drawable) {
		drawable.setBounds(0, 0 - drawable.getIntrinsicHeight(), drawable.getIntrinsicWidth(), 0);
		return drawable;
	}

	public MapItemizedOverlayArret(final Drawable defaultMarker, final Context context) {
		super(leftBottom(defaultMarker));
		mContext = context;
	}

	//Appeler quand on rajoute un nouvel marqueur a la liste des marqueurs
	public void addOverlay(final OverlayItem overlay, final ArretFavori favori) {
		mOverlays.add(overlay);
		arretFavoris.add(favori);
		populate();
	}

	@Override
	protected OverlayItem createItem(final int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}

	//Appeer quand on clique sur un marqueur
	@Override
	protected boolean onTap(final int index) {
		final OverlayItem item = mOverlays.get(index);
		final ArretFavori favori = arretFavoris.get(index);

		final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle(item.getTitle());
		builder.setMessage("vers " + item.getSnippet() + "\nVoulez vous ouvrir le détail?");
		builder.setCancelable(true);
		builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int id) {
				dialog.dismiss();
				final Intent intent = new Intent(mContext, DetailArret.class);
				intent.putExtra("favori", favori);
				mContext.startActivity(intent);
			}
		});
		builder.setNegativeButton("Non", new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int id) {
				dialog.cancel();
			}
		});
		final AlertDialog alert = builder.create();
		alert.show();


		return true;
	}
}
