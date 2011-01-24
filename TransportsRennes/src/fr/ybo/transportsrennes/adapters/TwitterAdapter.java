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

package fr.ybo.transportsrennes.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import fr.ybo.transportsrennes.R;
import twitter4j.Status;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Adapteur pour les alerts.
 */
public class TwitterAdapter extends ArrayAdapter<Status> {

	private List<Status> allStatus;
	private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy à HH:mm : ");
	private LayoutInflater inflater;

	public TwitterAdapter(Context context, List<Status> objects) {
		super(context, R.layout.onetwitter, objects);
		allStatus = objects;
		inflater = LayoutInflater.from(context);
	}

	private static class ViewHolder {
		TextView twitter;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.onetwitter, null);
			holder = new ViewHolder();
			holder.twitter = (TextView) convertView.findViewById(R.id.twitter);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Status status = allStatus.get(position);
		holder.twitter.setText(SDF.format(status.getCreatedAt()) + status.getText());
		return convertView;
	}
}
