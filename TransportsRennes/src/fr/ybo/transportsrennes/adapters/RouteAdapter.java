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
import fr.ybo.transportsrennes.keolis.gtfs.modele.Route;

import java.lang.reflect.Field;
import java.util.List;

public class RouteAdapter extends BaseAdapter {

	private final static Class<?> classDrawable = R.drawable.class;

	static class ViewHolder {
		TextView nomLong;
		LinearLayout conteneur;
	}

	private final LayoutInflater mInflater;

	private List<Route> routes;

	public RouteAdapter(final Context context, final List<Route> routes) throws ErreurKeolis {
		mInflater = LayoutInflater.from(context);
		this.routes = routes;
	}

	public int getCount() {
		return routes.size();
	}

	public Route getItem(final int position) {
		return routes.get(position);
	}

	public long getItemId(final int position) {
		return position;
	}

	public View getView(final int position, View convertView, final ViewGroup parent) {
		ViewHolder holder;

		convertView = mInflater.inflate(R.layout.route, null);


		holder = new ViewHolder();
		holder.conteneur = (LinearLayout) convertView.findViewById(R.id.conteneurImage);
		holder.nomLong = (TextView) convertView.findViewById(R.id.nomLong);

		convertView.setTag(holder);

		Route route = routes.get(position);
		holder.nomLong.setText(route.getNomLongFormate());
		try {
			Field fieldIcon = classDrawable.getDeclaredField("i" + route.getNomCourt().toLowerCase());
			int ressourceImg = fieldIcon.getInt(null);
			ImageView imgView = new ImageView(mInflater.getContext());
			imgView.setImageResource(ressourceImg);
			holder.conteneur.addView(imgView);
		} catch (NoSuchFieldException e) {
			TextView textView = new TextView(mInflater.getContext());
			textView.setTextSize(16);
			textView.setText(route.getNomCourt());
			holder.conteneur.addView(textView);
		} catch (IllegalAccessException e) {
			TextView textView = new TextView(mInflater.getContext());
			textView.setTextSize(16);
			textView.setText(route.getNomCourt());
			holder.conteneur.addView(textView);
		}

		return convertView;
	}
}
