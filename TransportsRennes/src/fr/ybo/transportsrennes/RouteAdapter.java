package fr.ybo.transportsrennes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
		// Cache the LayoutInflate to avoid asking for a new one each time.
		mInflater = LayoutInflater.from(context);
		this.routes = routes;
	}

	/**
	 * The number of items in the list is determined by the number of speeches
	 * in our array.
	 *
	 * @see android.widget.ListAdapter#getCount()
	 */
	public int getCount() {
		return routes.size();
	}

	/**
	 * Since the data comes from an array, just returning the index is sufficent
	 * to get at the data. If we were using a more complex data structure, we
	 * would return whatever object represents one row in the list.
	 *
	 * @see android.widget.ListAdapter#getItem(int)
	 */
	public Route getItem(final int position) {
		return routes.get(position);
	}

	/**
	 * Use the array index as a unique id.
	 *
	 * @see android.widget.ListAdapter#getItemId(int)
	 */
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
		holder.nomLong.setText(route.getNomLong());
		try {
			System.out.println("Recherche de 'i" + route.getNomCourt().toLowerCase());
			Field fieldIcon = classDrawable.getDeclaredField("i" + route.getNomCourt().toLowerCase());
			int ressourceImg = fieldIcon.getInt(null);
			ImageView imgView = new ImageView(mInflater.getContext());
			imgView.setImageResource(ressourceImg);
			imgView.setPadding(5, 5, 5, 5);
			holder.conteneur.addView(imgView);
		} catch (NoSuchFieldException e) {
			TextView textView = new TextView(mInflater.getContext());
			textView.setPadding(5, 5, 5, 5);
			textView.setTextSize(20);
			textView.setText(route.getNomCourt());
			holder.conteneur.addView(textView);
		} catch (IllegalAccessException e) {
			TextView textView = new TextView(mInflater.getContext());
			textView.setPadding(5, 5, 5, 5);
			textView.setTextSize(20);
			textView.setText(route.getNomCourt());
			holder.conteneur.addView(textView);
		}

		return convertView;
	}

	public void majRoutes(final List<Route> routes) {
		this.routes = routes;
	}

}
