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
package fr.ybo.transportscommun.map;


import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

import fr.ybo.transportscommun.donnees.modele.IStation;
import fr.ybo.transportscommun.util.Formatteur;

public class MapItemizedOverlayVelo extends ItemizedOverlay<OverlayItem> {
	private MarkerDrawable marker = null;
	private static Paint mTextPaint = new Paint();
	int IMAGE_HEIGHT;

    //Liste des marqueurs
    private final ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>(20);
    private final Context mContext;
	private final List<IStation> stations = new ArrayList<IStation>();


	public MapItemizedOverlayVelo(BitmapDrawable drawable, Context context) {
		super(drawable);
        mContext = context;
		this.marker = new MarkerDrawable(context.getResources(), drawable.getBitmap());
		boundCenterBottom(marker);
		mTextPaint.setAntiAlias(true);
		mTextPaint.setTextSize(15);
		mTextPaint.setTextAlign(Align.RIGHT);
		mTextPaint.setColor(Color.WHITE);
		mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
		IMAGE_HEIGHT = marker.getIntrinsicHeight();
		populate();
    }

    //Appeler quand on rajoute un nouvel marqueur a la liste des marqueurs
	public void addOverlay(IStation station) {

		int latitude = (int) (station.getLatitude() * 1.0E6);
		int longitude = (int) (station.getLongitude() * 1.0E6);

		StationOverlay overlay =
				new StationOverlay(new GeoPoint(latitude, longitude), String.valueOf(station.getBikesAvailables()),
						String.valueOf(station.getSlotsAvailables()));
        mOverlays.add(overlay);
        stations.add(station);
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
	protected boolean onTap(int index) {
		final IStation station = stations.get(index);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle(station.getName());
		builder.setMessage("Voulez vous ouvrir la station dans GoogleMap?");
        builder.setCancelable(true);
        builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                String lat = Double.toString(station.getLatitude());
                String lon = Double.toString(station.getLongitude());
				Uri uri =
						Uri.parse("geo:" + lat + ',' + lon);
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

	public class StationOverlay extends OverlayItem {
		private String mBikes;
		private String mSlots;

		public StationOverlay(GeoPoint point, String bikes, String slots) {
			super(point, null, null);
			mBikes = String.valueOf(bikes);
			mSlots = String.valueOf(slots);
		}

		@Override
		public Drawable getMarker(int stateBitset) {
			marker.bike = mBikes;
			marker.slots = mSlots;
			return marker;
		}
	}

	private class MarkerDrawable extends BitmapDrawable {

		// For fast access and lot of modifications
		public volatile String bike;
		public volatile String slots;

		public MarkerDrawable(Resources r, Bitmap b) {
			super(r, b);
			this.setAlpha(200);
		}

		@Override
		public void draw(Canvas canvas) {
			super.draw(canvas);
			canvas.drawText(bike, -6, -31, mTextPaint);
			canvas.drawText(slots, -6, -16, mTextPaint);
		}
	}

}
