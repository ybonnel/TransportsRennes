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
package fr.ybo.transportsrennes.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.R;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.GeocoderResult;
import com.google.code.geocoder.model.GeocoderStatus;

import fr.ybo.transportscommun.donnees.modele.Arret;
import fr.ybo.transportscommun.util.GeocodeUtil;
import fr.ybo.transportscommun.util.StringOperation;
import fr.ybo.transportsrennes.application.TransportsRennesApplication;

public class AdresseAdapter extends ArrayAdapter<String> {

	private final List<Arret> arrets;

	public AdresseAdapter(final Context context, final List<Arret> arrets) {
		super(context, R.layout.simple_spinner_item);
		this.arrets = arrets;
    }

    private final Filter filter = new MyFilter();

    @Override
    public Filter getFilter() {
        return filter;
    }

	private class MyFilter extends Filter {

        @Override
        protected FilterResults performFiltering(final CharSequence constraint) {

            final FilterResults fr = new FilterResults();
            if (constraint != null && constraint.length() > 5) {
                final GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().setAddress(constraint.toString())
                        .setLanguage("fr").setBounds(TransportsRennesApplication.getBounds()).getGeocoderRequest();
                GeocodeResponse reponseResult = null;
                try {
                    reponseResult = GeocodeUtil.geocode(geocoderRequest);
                } catch (final Exception ignore) {
                }

				final Collection<String> results = new ArrayList<String>();

				final String upper = StringOperation.sansAccents(constraint.toString().toUpperCase());

				for (final Arret arret : arrets) {
					if (arret.nom.contains(upper)) {
						results.add(arret.nom);
					}
				}

                if (reponseResult != null && reponseResult.getStatus() == GeocoderStatus.OK) {
					for (final GeocoderResult oneResult : reponseResult.getResults()) {
						results.add(oneResult.getFormattedAddress());
					}
				}

				fr.values = results;
				fr.count = results.size();
            }
            return fr;
        }

        @Override
        protected void publishResults(final CharSequence constraint, final FilterResults results) {
            if (results.count > 0) {
                clear();

				for (final String result : (Iterable<String>) results.values) {
                    add(result);
                }

                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }

        }

    }

}
