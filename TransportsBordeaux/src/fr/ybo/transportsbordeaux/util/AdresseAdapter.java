/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.ybo.transportsbordeaux.util;

import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.GeocoderResult;
import com.google.code.geocoder.model.GeocoderStatus;

import fr.ybo.transportsbordeaux.application.TransportsBordeauxApplication;

public class AdresseAdapter extends ArrayAdapter<String> {

	public AdresseAdapter(Context context) {
		super(context, android.R.layout.simple_spinner_item);
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
                        .setLanguage("fr").setBounds(TransportsBordeauxApplication.getBounds()).getGeocoderRequest();
                GeocodeResponse reponseResult = null;
                try {
                    reponseResult = TransportsBordeauxApplication.getGeocodeUtil().geocode(geocoderRequest);
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
