package fr.ybo.transportsrennes.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.keolis.modele.bus.ParkRelai;
import fr.ybo.transportsrennes.keolis.modele.velos.Station;
import fr.ybo.transportsrennes.util.Formatteur;

import java.util.List;

/**
 * Adapteur pour les park relais.
 */
public class ParkRelaiAdapter extends ArrayAdapter<ParkRelai> {

	public List<ParkRelai> getParkRelais() {
		return parkRelais;
	}

	private List<ParkRelai> parkRelais;

	private static final double SEUIL_ROUGE = 0.25;

	private static final double SEUIL_ORANGE = 0.5;

	public ParkRelaiAdapter(Context context, int textViewResourceId, List<ParkRelai> objects) {
		super(context, textViewResourceId, objects);
		parkRelais = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater vi = LayoutInflater.from(getContext());
		View v = vi.inflate(R.layout.dispoparkrelai, null);
		ParkRelai parkRelai = parkRelais.get(position);
		double poucentageDispo = ((double) parkRelai.getCarParkAvailable().intValue()) / ((double) (parkRelai.getCarParkCapacity().intValue()));

		ImageView icone = (ImageView) v.findViewById(R.id.dispoparkrelai_image);
		if (poucentageDispo < SEUIL_ROUGE) {
			icone.setImageResource(R.drawable.dispo_parkrelai_rouge);
		} else if (poucentageDispo < SEUIL_ORANGE) {
			icone.setImageResource(R.drawable.dispo_parkrelai_orange);
		} else {
			icone.setImageResource(R.drawable.dispo_parkrelai_bleue);
		}

		TextView dispoParkRelaiText = (TextView) v.findViewById(R.id.dispoparkrelai_text);
		dispoParkRelaiText.setText(parkRelai.getCarParkAvailable() + " / " + parkRelai.getCarParkCapacity());
		TextView dispoParkRelaiStation = (TextView) v.findViewById(R.id.dispoparkrelai_nom);
		dispoParkRelaiStation.setText(parkRelai.getName());
		TextView dispoParkRelaiDistance = (TextView) v.findViewById(R.id.dispoparkrelai_distance);
		dispoParkRelaiDistance.setText(parkRelai.formatDistance());
		return v;
	}
}
