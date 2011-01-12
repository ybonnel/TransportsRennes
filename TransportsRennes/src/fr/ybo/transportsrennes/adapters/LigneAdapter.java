package fr.ybo.transportsrennes.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.keolis.ErreurKeolis;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Ligne;

import java.lang.reflect.Field;
import java.util.List;

public class LigneAdapter extends BaseAdapter {

	private final static Class<?> classDrawable = R.drawable.class;

	static class ViewHolder {
		TextView nomLong;
		LinearLayout conteneur;
	}

	private final LayoutInflater mInflater;

	private List<Ligne> lignes;

	public LigneAdapter(Context context, List<Ligne> lignes) throws ErreurKeolis {
		mInflater = LayoutInflater.from(context);
		this.lignes = lignes;
	}

	public int getCount() {
		return lignes.size();
	}

	public Ligne getItem(final int position) {
		return lignes.get(position);
	}

	public long getItemId(final int position) {
		return position;
	}

	public View getView(final int position, View convertView, final ViewGroup parent) {
		ViewHolder holder;

		convertView = mInflater.inflate(R.layout.ligne, null);


		holder = new ViewHolder();
		holder.conteneur = (LinearLayout) convertView.findViewById(R.id.conteneurImage);
		holder.nomLong = (TextView) convertView.findViewById(R.id.nomLong);

		convertView.setTag(holder);

		Ligne ligne = lignes.get(position);
		holder.nomLong.setText(ligne.nomLong);
		try {
			Field fieldIcon = classDrawable.getDeclaredField("i" + ligne.nomCourt.toLowerCase());
			int ressourceImg = fieldIcon.getInt(null);
			ImageView imgView = new ImageView(mInflater.getContext());
			imgView.setImageResource(ressourceImg);
			holder.conteneur.addView(imgView);
		} catch (NoSuchFieldException e) {
			TextView textView = new TextView(mInflater.getContext());
			textView.setTextSize(16);
			textView.setText(ligne.nomCourt);
			holder.conteneur.addView(textView);
		} catch (IllegalAccessException e) {
			TextView textView = new TextView(mInflater.getContext());
			textView.setTextSize(16);
			textView.setText(ligne.nomCourt);
			holder.conteneur.addView(textView);
		}

		return convertView;
	}
}
