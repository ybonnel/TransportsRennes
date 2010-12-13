package fr.ybo.transportsrennes;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import fr.ybo.transportsrennes.keolis.ErreurKeolis;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Route;

public class RouteAdapter extends BaseAdapter {

	static class ViewHolder {
		TextView nomLong;
		TextView nomCourt;
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

	/**
	 * Make a view to hold each row.
	 * 
	 * @see android.widget.ListAdapter#getView(int, android.view.View,
	 *      android.view.ViewGroup)
	 */
	public View getView(final int position, View convertView, final ViewGroup parent) {
		// A ViewHolder keeps references to children views to avoid unneccessary
		// calls
		// to findViewById() on each row.
		ViewHolder holder;

		// When convertView is not null, we can reuse it directly, there is no
		// need
		// to reinflate it. We only inflate a new View when the convertView
		// supplied
		// by ListView is null.
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.route, null);

			// Creates a ViewHolder and store references to the two children
			// views
			// we want to bind data to.
			holder = new ViewHolder();
			holder.nomCourt = (TextView) convertView.findViewById(R.id.nomCourt);
			holder.nomLong = (TextView) convertView.findViewById(R.id.nomLong);

			convertView.setTag(holder);
		} else {
			// Get the ViewHolder back to get fast access to the TextView
			// and the ImageView.
			holder = (ViewHolder) convertView.getTag();
		}

		// Bind the data efficiently with the holder.
		holder.nomLong.setText(routes.get(position).getNomLong());
		holder.nomCourt.setText(routes.get(position).getNomCourt());

		return convertView;
	}

	public void majRoutes(final List<Route> routes) {
		this.routes = routes;
	}

}
