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
package fr.ybo.transportscommun.adapters.bus;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import fr.ybo.transportscommun.R;

public class DetailTrajetAdapter extends CursorAdapter {

    private final int arretNomCol;
    private final int prochainDepartCol;

    public DetailTrajetAdapter(final Context context, final Cursor cursor) {
        super(context, cursor);
        arretNomCol = cursor.getColumnIndex("nom");
        prochainDepartCol = cursor.getColumnIndex("heureDepart");
    }

    private static class ViewHolder {
        TextView arretNom;
        TextView heurePassage;
    }

    @Override
    public View newView(final Context context, final Cursor cursor, final ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.detailtrajetliste, parent, false);
        final ViewHolder holder = new ViewHolder();
        holder.arretNom = (TextView) view.findViewById(R.id.detailTrajet_arretNom);
        holder.heurePassage = (TextView) view.findViewById(R.id.detailTrajet_heurePassage);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {
        final int prochainDepart = cursor.getInt(prochainDepartCol);
        final String arretNom = cursor.getString(arretNomCol);
        ((ViewHolder) view.getTag()).arretNom.setText(arretNom);
        ((ViewHolder) view.getTag()).heurePassage.setText(formatterCalendarHeure(prochainDepart));
    }

    private static CharSequence formatterCalendarHeure(final int prochainDepart) {
        final StringBuilder stringBuilder = new StringBuilder();
        int heures = prochainDepart / 60;
        final int minutes = prochainDepart - heures * 60;
        if (heures >= 24) {
            heures -= 24;
        }
        final String heuresChaine = Integer.toString(heures);
        final String minutesChaine = Integer.toString(minutes);
        if (heuresChaine.length() < 2) {
            stringBuilder.append('0');
        }
        stringBuilder.append(heuresChaine).append(':');
        if (minutesChaine.length() < 2) {
            stringBuilder.append('0');
        }
        return stringBuilder.append(minutesChaine);
    }

}
