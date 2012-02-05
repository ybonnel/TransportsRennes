package fr.ybo.transportscommun.adapters.parkings;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import fr.ybo.transportscommun.R;

public class ParkingAdapter<T extends IParking> extends ArrayAdapter<T> {

	private final List<T> parkings;

	private static final double SEUIL_ROUGE = 0.25;

	private static final double SEUIL_ORANGE = 0.5;

	private static final Map<Integer, String> MAP_STATES = new HashMap<Integer, String>(3);

	private final LayoutInflater inflater;

	public ParkingAdapter(Context context, List<T> objects) {
		super(context, R.layout.dispoparkrelai, objects);
		if (MAP_STATES.isEmpty()) {
			MAP_STATES.put(1, context.getString(R.string.ferme));
			MAP_STATES.put(2, context.getString(R.string.complet));
			MAP_STATES.put(3, context.getString(R.string.indisponible));
		}
		parkings = objects;
		inflater = LayoutInflater.from(getContext());
	}

	private static class ViewHolder {
		TextView dispoParkRelaiNom;
		TextView dispoParkRelaiDistance;
		TextView dispoParkRelaiText;
		TextView icone;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View convertView1 = convertView;
		ParkingAdapter.ViewHolder holder;
		if (convertView1 == null) {
			convertView1 = inflater.inflate(R.layout.dispoparkrelai, null);
			holder = new ParkingAdapter.ViewHolder();
			holder.dispoParkRelaiNom = (TextView) convertView1.findViewById(R.id.dispoparkrelai_nom);
			holder.dispoParkRelaiDistance = (TextView) convertView1.findViewById(R.id.dispoparkrelai_distance);
			holder.dispoParkRelaiText = (TextView) convertView1.findViewById(R.id.dispoparkrelai_text);
			holder.icone = (TextView) convertView1.findViewById(R.id.itemSymbole);
			convertView1.setTag(holder);
		} else {
			holder = (ParkingAdapter.ViewHolder) convertView1.getTag();
		}
		T parkRelai = parkings.get(position);
		holder.dispoParkRelaiNom.setText(parkRelai.getName());
		holder.dispoParkRelaiDistance.setText(parkRelai.formatDistance());
		// Parc Relai ouvert.
		if (parkRelai.getState() == 0) {
			double poucentageDispo = (double) parkRelai.getCarParkAvailable() / (double) parkRelai.getCarParkCapacity();

			if (poucentageDispo < SEUIL_ROUGE) {
				holder.icone.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.item_symbol_red));
			} else if (poucentageDispo < SEUIL_ORANGE) {
				holder.icone.setBackgroundDrawable(getContext().getResources().getDrawable(
						R.drawable.item_symbol_orange));
			} else {
				holder.icone
						.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.item_symbol_blue));
			}

			holder.dispoParkRelaiText.setText(parkRelai.getCarParkAvailable() + " / " + parkRelai.getCarParkCapacity());
		} else {
			// Cas ou le park relai n'est pas disponible (complet, fermé,
			// indisponible).
			holder.dispoParkRelaiText.setText(MAP_STATES.get(parkRelai.getState()));

			holder.icone.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.item_symbol_red));
		}
		return convertView1;
	}

}
