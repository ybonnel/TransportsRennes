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

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

import java.util.ArrayList;
import java.util.Iterator;

public class LineItemizedOverlay extends Overlay {

    private ArrayList<GeoPoint> mOverlays = new ArrayList<GeoPoint>();
    private static final int colour = Color.BLUE;
    private static final int ALPHA = 120;
    private static final float STROKE = 4.5f;
    private final Path path;
    private final Point p;
    private final Paint paint;


    public LineItemizedOverlay(ArrayList<GeoPoint> mOverlays) {
        this.mOverlays = mOverlays;
        path = new Path();
        p = new Point();
        paint = new Paint();
    }

    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        super.draw(canvas, mapView, shadow);

        paint.setColor(colour);
        paint.setAlpha(ALPHA);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(STROKE);
        paint.setStyle(Paint.Style.STROKE);

        redrawPath(mapView);
        canvas.drawPath(path, paint);
    }

    private void redrawPath(final MapView mv) {
        final Projection prj = mv.getProjection();
        path.rewind();
        final Iterator<GeoPoint> it = mOverlays.iterator();
        prj.toPixels(it.next(), p);
        path.moveTo(p.x, p.y);
        while (it.hasNext()) {
            prj.toPixels(it.next(), p);
            path.lineTo(p.x, p.y);
        }
        path.setLastPoint(p.x, p.y);
    }

}
