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

public class MapItemizedOverlay extends ItemizedOverlay {

	//Liste des marqueurs
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private Context mContext;
	private List<ArretFavori> arretFavoris = new ArrayList<ArretFavori>();

	public MapItemizedOverlay(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		mContext = context;
	}

	//Appeler quand on rajoute un nouvel marqueur a la liste des marqueurs
	public void addOverlay(OverlayItem overlay, ArretFavori favori) {
		mOverlays.add(overlay);
		arretFavoris.add(favori);
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
		OverlayItem item = mOverlays.get(index);
		final ArretFavori favori = arretFavoris.get(index);

		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle(item.getTitle());
		builder.setMessage("vers " + item.getSnippet() + "\nVoulez vous ouvrir le détail?");
		builder.setCancelable(true);
		builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int id) {
				dialog.dismiss();
				final Intent intent = new Intent(mContext, DetailArret.class);
				intent.putExtra("favori",favori);
				mContext.startActivity(intent);
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
