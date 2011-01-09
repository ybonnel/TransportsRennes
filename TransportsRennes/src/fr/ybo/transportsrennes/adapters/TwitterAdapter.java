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
import fr.ybo.transportsrennes.keolis.modele.bus.Alert;
import twitter4j.ResponseList;
import twitter4j.Status;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Adapteur pour les alerts.
 */
public class TwitterAdapter extends ArrayAdapter<Status> {

	private List<Status> allStatus;
	private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy à hh:mm : ");

	public TwitterAdapter(Context context, int textViewResourceId, List<Status> objects) {
		super(context, textViewResourceId, objects);
		allStatus = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater vi = LayoutInflater.from(getContext());
		View v = vi.inflate(R.layout.onetwitter, null);
		Status status = allStatus.get(position);

		TextView twitter = (TextView) v.findViewById(R.id.twitter);
		twitter.setText(SDF.format(status.getCreatedAt()) + status.getText());
		return v;
	}
}
