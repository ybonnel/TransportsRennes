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
package fr.ybo.transportsbordeaux.adapters.alerts;

import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import fr.ybo.transportsbordeaux.R;
import fr.ybo.transportsbordeaux.twitter.MessageTwitter;

/**
 * Adapteur pour les alerts.
 */
public class TwitterAdapter extends ArrayAdapter<MessageTwitter> {

    private final List<MessageTwitter> messages;
    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy à HH:mm : ");
    private final LayoutInflater inflater;

    public TwitterAdapter(Context context, List<MessageTwitter> objects) {
        super(context, R.layout.onetwitter, objects);
        messages = objects;
        inflater = LayoutInflater.from(context);
    }

    private static class ViewHolder {
        TextView twitter;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View convertView1 = convertView;
        TwitterAdapter.ViewHolder holder;
        if (convertView1 == null) {
            convertView1 = inflater.inflate(R.layout.onetwitter, null);
            holder = new TwitterAdapter.ViewHolder();
            holder.twitter = (TextView) convertView1.findViewById(R.id.twitter);
            convertView1.setTag(holder);
        } else {
            holder = (TwitterAdapter.ViewHolder) convertView1.getTag();
        }
        MessageTwitter message = messages.get(position);
		if (message.dateCreation != null) {
			holder.twitter.setText(SDF.format(message.dateCreation) + message.texte);
		} else {
			holder.twitter.setText(message.texte);
		}
        return convertView1;
    }
}
