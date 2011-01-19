/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

	public TwitterAdapter(Context context, List<Status> objects) {
		super(context, R.layout.onetwitter, objects);
		allStatus = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater vi = LayoutInflater.from(getContext());
		View v = vi.inflate(R.layout.onetwitter, null);
		Status status = allStatus.get(position);

		TextView twitter = (TextView) v.findViewById(R.id.twitter);
		twitter.setText(SDF.format(status.getCreatedAt()) + status.getText());
		return v;
	}
}
