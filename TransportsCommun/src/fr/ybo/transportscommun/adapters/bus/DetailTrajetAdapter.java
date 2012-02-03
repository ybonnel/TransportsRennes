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

    public DetailTrajetAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        arretNomCol = cursor.getColumnIndex("nom");
        prochainDepartCol = cursor.getColumnIndex("heureDepart");
    }

    private static class ViewHolder {
        TextView arretNom;
        TextView heurePassage;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.detailtrajetliste, parent, false);
        DetailTrajetAdapter.ViewHolder holder = new DetailTrajetAdapter.ViewHolder();
        holder.arretNom = (TextView) view.findViewById(R.id.detailTrajet_arretNom);
        holder.heurePassage = (TextView) view.findViewById(R.id.detailTrajet_heurePassage);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int prochainDepart = cursor.getInt(prochainDepartCol);
        String arretNom = cursor.getString(arretNomCol);
        ((DetailTrajetAdapter.ViewHolder) view.getTag()).arretNom.setText(arretNom);
        ((DetailTrajetAdapter.ViewHolder) view.getTag()).heurePassage.setText(formatterCalendarHeure(prochainDepart));
    }

    private CharSequence formatterCalendarHeure(int prochainDepart) {
        StringBuilder stringBuilder = new StringBuilder();
        int heures = prochainDepart / 60;
        int minutes = prochainDepart - heures * 60;
        if (heures >= 24) {
            heures -= 24;
        }
        String heuresChaine = Integer.toString(heures);
        String minutesChaine = Integer.toString(minutes);
        if (heuresChaine.length() < 2) {
            stringBuilder.append('0');
        }
        stringBuilder.append(heuresChaine);
        stringBuilder.append(':');
        if (minutesChaine.length() < 2) {
            stringBuilder.append('0');
        }
        stringBuilder.append(minutesChaine);
        return stringBuilder.toString();
    }

}
