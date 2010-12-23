package fr.ybo.transportsrennes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import fr.ybo.transportsrennes.keolis.gtfs.modele.ArretFavori;
import fr.ybo.transportsrennes.util.Formatteur;

import java.lang.reflect.Field;
import java.util.List;

public class FavoriAdapter extends BaseAdapter {
	private final static Class<?> classDrawable = R.drawable.class;

	static class ViewHolder {
		LinearLayout conteneur;
		TextView arret;
		TextView direction;
	}

	private final LayoutInflater mInflater;

	private final List<ArretFavori> favoris;

	public FavoriAdapter(final Context context, final List<ArretFavori> favoris) {
		// Cache the LayoutInflate to avoid asking for a new one each time.
		mInflater = LayoutInflater.from(context);
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

	public View getView(final int position, View convertView, final ViewGroup parent) {
		ViewHolder holder;


		convertView = mInflater.inflate(R.layout.favori, null);

		holder = new ViewHolder();
		holder.conteneur = (LinearLayout) convertView.findViewById(R.id.conteneurImage);
		holder.arret = (TextView) convertView.findViewById(R.id.nomArret);
		holder.direction = (TextView) convertView.findViewById(R.id.directionArret);

		convertView.setTag(holder);

		ArretFavori favori = favoris.get(position);

		holder.arret.setText(Formatteur.formatterChaine(favori.getNomArret()));
		holder.direction.setText(Formatteur.formatterChaine(favori.getDirection().replaceAll(favori.getRouteNomCourt(), "")));
		try {
			Field fieldIcon = classDrawable.getDeclaredField("i" + favori.getRouteNomCourt().toLowerCase());
			int ressourceImg = fieldIcon.getInt(null);
			ImageView imgView = new ImageView(mInflater.getContext());
			imgView.setImageResource(ressourceImg);
			holder.conteneur.addView(imgView);
		} catch (NoSuchFieldException e) {
			TextView textView = new TextView(mInflater.getContext());
			textView.setTextSize(16);
			textView.setText(favori.getRouteNomCourt());
			holder.conteneur.addView(textView);
		} catch (IllegalAccessException e) {
			TextView textView = new TextView(mInflater.getContext());
			textView.setTextSize(16);
			textView.setText(favori.getRouteNomCourt());
			holder.conteneur.addView(textView);
		}

		return convertView;
	}

}
