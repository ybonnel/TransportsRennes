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
 * 
 * Contributors:
 *     ybonnel - initial API and implementation
 */
package fr.ybo.transportsrennes.map;


import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.MotionEvent;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

import fr.ybo.transportsrennes.keolis.modele.bus.ParkRelai;
import fr.ybo.transportsrennes.util.Formatteur;

public class MapItemizedOverlayParking extends ItemizedOverlay<OverlayItem> {

	//Liste des marqueurs
	private final ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>(4);
	private final Context mContext;
	private final List<ParkRelai> parkRelais = new ArrayList<ParkRelai>(4);

	private static Drawable leftBottom(Drawable drawable) {
		drawable.setBounds(0, 0 - drawable.getIntrinsicHeight(), drawable.getIntrinsicWidth(), 0);
		return drawable;
	}

	public MapItemizedOverlayParking(Drawable defaultMarker, Context context) {
		super(leftBottom(defaultMarker));
		mContext = context;
	}

	//Appeler quand on rajoute un nouvel marqueur a la liste des marqueurs
	public void addOverlay(OverlayItem overlay, ParkRelai parkRelai) {
		mOverlays.add(overlay);
		parkRelais.add(parkRelai);
		populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}

	//Appeer quand on clique sur un marqueur
	@Override
	protected boolean onTap(final int index) {
		OverlayItem item = mOverlays.get(index);
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle(item.getTitle());
		builder.setMessage(item.getSnippet() + "\nVoulez vous ouvrir le Parc Relai dans GoogleMap?");
		builder.setCancelable(true);
		builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
				ParkRelai parkRelai = parkRelais.get(index);
				String lat = Double.toString(parkRelai.getLatitude());
				String lon = Double.toString(parkRelai.getLongitude());
				Uri uri = Uri.parse("geo:0,0?q=" + Formatteur.formatterChaine(parkRelai.name) + "+@" + lat + ',' + lon);
				mContext.startActivity(new Intent(Intent.ACTION_VIEW, uri));
			}
		});
		builder.setNegativeButton("Non", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
		return true;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent pEvent, MapView pMapView) {
		return super.onTouchEvent(pEvent, pMapView);
	}
}
