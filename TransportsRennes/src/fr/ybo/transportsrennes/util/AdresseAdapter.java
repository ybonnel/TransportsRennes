package fr.ybo.transportsrennes.util;

import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.GeocoderResult;
import com.google.code.geocoder.model.GeocoderStatus;

import fr.ybo.transportsrennes.TransportsRennesApplication;

public class AdresseAdapter extends ArrayAdapter<String> {

	public AdresseAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}

	private final MyFilter filter = new MyFilter();

	@Override
	public Filter getFilter() {
		return filter;
	}

	private class MyFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {

			FilterResults fr = new FilterResults();
			if (constraint != null && constraint.length() > 5) {
				GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().setAddress(constraint.toString())
						.setLanguage("fr").setBounds(TransportsRennesApplication.getBounds()).getGeocoderRequest();
				GeocodeResponse reponseResult = null;
				try {
					reponseResult = Geocoder.geocode(geocoderRequest);
				} catch (Exception ignore) {
				}

				if (reponseResult != null && reponseResult.getStatus().equals(GeocoderStatus.OK)) {

					fr.values = reponseResult.getResults();

					fr.count = reponseResult.getResults().size();
				}
			}
			return fr;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			if (results.count > 0) {
				clear();

				for (GeocoderResult result : ((List<GeocoderResult>) results.values))
					add(result.getFormattedAddress());

				notifyDataSetChanged();
			} else {
				notifyDataSetInvalidated();
			}

		}

	}

}
