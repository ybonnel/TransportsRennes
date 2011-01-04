package fr.ybo.transportsrennes.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Arret;
import fr.ybo.transportsrennes.util.Formatteur;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Adapteur pour les arrets pas positionnement GPS..
 */
public class ArretGpsAdapter extends ArrayAdapter<Arret> {

	private final static Class<?> classDrawable = R.drawable.class;

	private List<Arret> arrets;

	public ArretGpsAdapter(Context context, int textViewResourceId, List<Arret> objects) {
		super(context, textViewResourceId, objects);
		arrets = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater vi = LayoutInflater.from(getContext());
		View v = vi.inflate(R.layout.arretgps, null);
		Arret arret = arrets.get(position);
		LinearLayout conteneur = (LinearLayout) v.findViewById(R.id.conteneurImage);
		try {
			Field fieldIcon = classDrawable.getDeclaredField("i" + arret.getFavori().getRouteNomCourt().toLowerCase());
			int ressourceImg = fieldIcon.getInt(null);
			ImageView imgView = new ImageView(getContext());
			imgView.setImageResource(ressourceImg);
			conteneur.addView(imgView);
		} catch (NoSuchFieldException e) {
			TextView textView = new TextView(getContext());
			textView.setTextSize(16);
			textView.setText(arret.getFavori().getRouteNomCourt());
			conteneur.addView(textView);
		} catch (IllegalAccessException e) {
			TextView textView = new TextView(getContext());
			textView.setTextSize(16);
			textView.setText(arret.getFavori().getRouteNomCourt());
			conteneur.addView(textView);
		}
		TextView arretDirection = (TextView) v.findViewById(R.id.arretgps_direction);
		arretDirection.setText(arret.getFavori().getDirection());
		TextView nomArret = (TextView) v.findViewById(R.id.arretgps_nomArret);
		nomArret.setText(Formatteur.formatterChaine(arret.getNom()));
		TextView distance = (TextView) v.findViewById(R.id.arretgps_distance);
		distance.setText(arret.formatDistance());
		return v;
	}
}
