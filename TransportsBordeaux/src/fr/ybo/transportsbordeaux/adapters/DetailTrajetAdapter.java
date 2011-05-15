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

package fr.ybo.transportsbordeaux.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import fr.ybo.transportsbordeaux.R;
import fr.ybo.transportsbordeaux.tbc.PortionTrajet;

/**
 * @author ybonnel
 *
 */
public class DetailTrajetAdapter extends ArrayAdapter<PortionTrajet> {

	private final LayoutInflater inflater;

	public DetailTrajetAdapter(Context context, List<PortionTrajet> trajet) {
		super(context, R.layout.detailtrajetliste, trajet);
		inflater = LayoutInflater.from(context);
	}

	private static class ViewHolder {
		TextView arretNom;
		TextView heurePassage;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View convertView1 = convertView;
		PortionTrajet portion = getItem(position);
		ViewHolder holder;
		if (convertView1 == null) {
			convertView1 = inflater.inflate(R.layout.detailtrajetliste, null);
			holder = new ViewHolder();
			holder.arretNom = (TextView) convertView1.findViewById(R.id.detailTrajet_arretNom);
			holder.heurePassage = (TextView) convertView1.findViewById(R.id.detailTrajet_heurePassage);
			convertView1.setTag(holder);
		} else {
			holder = (ViewHolder) convertView1.getTag();
		}
		holder.arretNom.setText(portion.arret);
		holder.heurePassage.setText(portion.horaire);
		return convertView1;
	}

}
