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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Adapteur pour les park relais.
 */
public class ParkRelaiAdapter extends ArrayAdapter<ParkRelai> {

	private List<ParkRelai> parkRelais;

	private static final double SEUIL_ROUGE = 0.25;

	private static final double SEUIL_ORANGE = 0.5;

	private static final Map<Integer, String> MAP_STATES = new HashMap<Integer, String>();

	static {
		MAP_STATES.put(1, "Fermé");
		MAP_STATES.put(2, "Complet");
		MAP_STATES.put(3, "Indispo.");
	}

	public ParkRelaiAdapter(Context context, int textViewResourceId, List<ParkRelai> objects) {
		super(context, textViewResourceId, objects);
		parkRelais = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater vi = LayoutInflater.from(getContext());
		View v = vi.inflate(R.layout.dispoparkrelai, null);
		ParkRelai parkRelai = parkRelais.get(position);
		TextView dispoParkRelaiStation = (TextView) v.findViewById(R.id.dispoparkrelai_nom);
		dispoParkRelaiStation.setText(parkRelai.name);
		TextView dispoParkRelaiDistance = (TextView) v.findViewById(R.id.dispoparkrelai_distance);
		dispoParkRelaiDistance.setText(parkRelai.formatDistance());
		TextView dispoParkRelaiText = (TextView) v.findViewById(R.id.dispoparkrelai_text);
		ImageView icone = (ImageView) v.findViewById(R.id.dispoparkrelai_image);
		// Parc Relai ouvert.
		if (parkRelai.state == 0) {
			double poucentageDispo = ((double) parkRelai.carParkAvailable.intValue()) / ((double) (parkRelai.carParkCapacity.intValue()));


			if (poucentageDispo < SEUIL_ROUGE) {
				icone.setImageResource(R.drawable.dispo_parkrelai_rouge);
			} else if (poucentageDispo < SEUIL_ORANGE) {
				icone.setImageResource(R.drawable.dispo_parkrelai_orange);
			} else {
				icone.setImageResource(R.drawable.dispo_parkrelai_bleue);
			}

			dispoParkRelaiText.setText(parkRelai.carParkAvailable + " / " + parkRelai.carParkCapacity);
		} else {
			// Cas ou le park relai n'est pas disponible (complet, fermé, indisponible).
			icone.setImageResource(R.drawable.dispo_parkrelai_rouge);
			dispoParkRelaiText.setText(MAP_STATES.get(parkRelai.state));
		}
		return v;
	}
}
