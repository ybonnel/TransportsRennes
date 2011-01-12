package fr.ybo.transportsrennes.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.keolis.modele.bus.PointDeVente;

import java.util.List;

/**
 * Adapteur pour les points de vente.
 */
public class PointDeVenteAdapter extends ArrayAdapter<PointDeVente> {

	private List<PointDeVente> pointsDeVente;

	public PointDeVenteAdapter(Context context, int textViewResourceId, List<PointDeVente> objects) {
		super(context, textViewResourceId, objects);
		pointsDeVente = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater vi = LayoutInflater.from(getContext());
		View v = vi.inflate(R.layout.pointdevente, null);
		PointDeVente pointDeVente = pointsDeVente.get(position);

		TextView nom = (TextView) v.findViewById(R.id.pointdevente_nom);
		nom.setText(pointDeVente.name);
		TextView telephone = (TextView) v.findViewById(R.id.pointdevente_telephone);
		telephone.setText(pointDeVente.telephone);
		final String tel = pointDeVente.telephone;

		telephone.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				Uri uri = Uri.parse("tel:" + tel);
				getContext().startActivity(new Intent(Intent.ACTION_VIEW, uri));
			}
		});
		TextView distance = (TextView) v.findViewById(R.id.pointdevente_distance);
		distance.setText(pointDeVente.formatDistance());
		return v;
	}
}
