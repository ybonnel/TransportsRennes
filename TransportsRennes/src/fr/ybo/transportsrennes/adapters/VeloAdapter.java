package fr.ybo.transportsrennes.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.keolis.modele.velos.Station;
import fr.ybo.transportsrennes.util.Formatteur;

import java.util.List;

/**
 * Adapteur pour les alerts.
 */
public class VeloAdapter extends ArrayAdapter<Station> {

	private List<Station> stations;

	private static final double SEUIL_ROUGE = 0.25;

	private static final double SEUIL_ORANGE = 0.5;

	public VeloAdapter(Context context, int textViewResourceId, List<Station> objects) {
		super(context, textViewResourceId, objects);
		stations = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater vi = LayoutInflater.from(getContext());
		View v = vi.inflate(R.layout.dispovelo, null);
		Station station = stations.get(position);
		int placesTotales = station.getBikesavailable() + station.getSlotsavailable();
		double poucentageDispo = ((double) station.getBikesavailable()) / ((double) (placesTotales));

		ImageView icone = (ImageView) v.findViewById(R.id.dispovelo_image);
		if (poucentageDispo < SEUIL_ROUGE) {
			icone.setImageResource(R.drawable.dispo_velo_rouge);
		} else if (poucentageDispo < SEUIL_ORANGE) {
			icone.setImageResource(R.drawable.dispo_velo_orange);
		} else {
			icone.setImageResource(R.drawable.dispo_velo_bleue);
		}

		TextView dispoVeloText = (TextView) v.findViewById(R.id.dispovelo_text);
		dispoVeloText.setText(station.getBikesavailable() + " / " + placesTotales);
		TextView dispoVeloStation = (TextView) v.findViewById(R.id.dispovelo_station);
		dispoVeloStation.setText(Formatteur.formatterChaine(station.getName()));
		TextView dispoVeloDistance = (TextView) v.findViewById(R.id.dispovelo_distance);
		dispoVeloDistance.setText(station.formatDistance());
		return v;
	}
}
