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

package fr.ybo.transportsrennes.map;


import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.Toast;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;
import fr.ybo.transportsrennes.keolis.modele.bus.ParkRelai;
import fr.ybo.transportsrennes.keolis.modele.velos.Station;
import fr.ybo.transportsrennes.util.Formatteur;
import fr.ybo.transportsrennes.util.LogYbo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapItemizedOverlayParking extends ItemizedOverlay {

	private final static LogYbo LOG_YBO = new LogYbo(MapItemizedOverlayParking.class);

	//Liste des marqueurs
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private Context mContext;
	private List<ParkRelai> parkRelais = new ArrayList<ParkRelai>();

	public MapItemizedOverlayParking(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
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
			public void onClick(final DialogInterface dialog, final int id) {
				dialog.dismiss();
				ParkRelai parkRelai = parkRelais.get(index);
				String _lat = Double.toString(parkRelai.getLatitude());
				String _lon = Double.toString(parkRelai.getLongitude());
				Uri uri = Uri.parse("geo:0,0?q=" + Formatteur.formatterChaine(parkRelai.name) + "+@" + _lat + "," + _lon);
				try {
					MapItemizedOverlayParking.this.mContext.startActivity(new Intent(Intent.ACTION_VIEW, uri));
				} catch (ActivityNotFoundException noGoogleMapsException) {
					LOG_YBO.erreur("Google maps de doit pas être présent", noGoogleMapsException);
					Toast.makeText(MapItemizedOverlayParking.this.mContext, "Vous n'avez pas GoogleMaps d'installé...", Toast.LENGTH_LONG).show();
				}
			}
		});
		builder.setNegativeButton("Non", new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int id) {
				dialog.cancel();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
		return true;
	}
}
