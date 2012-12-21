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
package fr.ybo.transportsrennes.adapters.bus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import fr.ybo.transportscommun.donnees.modele.ArretFavori;
import fr.ybo.transportscommun.donnees.modele.DetailArretConteneur;
import fr.ybo.transportscommun.donnees.modele.Horaire;
import fr.ybo.transportscommun.util.IconeLigne;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.application.TransportsRennesApplication;

public class FavoriAdapter extends BaseAdapter {

    private final LayoutInflater mInflater;

    private final List<ArretFavori> favoris;

    private int now;
    private Calendar calendar;
    private final Context myContext;

    public FavoriAdapter(Context context, List<ArretFavori> favoris) {
        // Cache the LayoutInflate to avoid asking for a new one each time.
        mInflater = LayoutInflater.from(context);
        this.favoris = favoris;
        myContext = context;
        majCalendar();
    }


    public void majCalendar() {
        calendar = Calendar.getInstance();
        now = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
    }

    public Collection<ArretFavori> getFavoris() {
        return favoris;
    }

    public int getCount() {
        return favoris.size();
    }

    public ArretFavori getItem(int position) {
        return favoris.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        ImageView iconeLigne;
        TextView arret;
        TextView direction;
        TextView tempsRestant;
        ImageView moveUp;
        ImageView moveDown;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View convertView1 = convertView;
        FavoriAdapter.ViewHolder holder;
        if (convertView1 == null) {
            convertView1 = mInflater.inflate(R.layout.favori, null);
            holder = new FavoriAdapter.ViewHolder();
            holder.iconeLigne = (ImageView) convertView1.findViewById(R.id.iconeLigne);
            holder.arret = (TextView) convertView1.findViewById(R.id.nomArret);
            holder.direction = (TextView) convertView1.findViewById(R.id.directionArret);
            holder.tempsRestant = (TextView) convertView1.findViewById(R.id.tempsRestant);
            holder.moveUp = (ImageView) convertView1.findViewById(R.id.moveUp);
            holder.moveDown = (ImageView) convertView1.findViewById(R.id.moveDown);

            convertView1.setTag(holder);
        } else {
            holder = (FavoriAdapter.ViewHolder) convertView1.getTag();
        }
		holder.arret.setTextColor(TransportsRennesApplication.getTextColor(myContext));
		holder.direction.setTextColor(TransportsRennesApplication.getTextColor(myContext));
		holder.tempsRestant.setTextColor(TransportsRennesApplication.getTextColor(myContext));

        final ArretFavori favori = favoris.get(position);

        if (position == 0) {
            holder.moveUp.setVisibility(View.INVISIBLE);
        } else {
            holder.moveUp.setVisibility(View.VISIBLE);
            holder.moveUp.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (position > 0) {
                        int autrePosition = position - 1;
                        favoris.set(position, favoris.get(autrePosition));
                        favoris.set(autrePosition, favori);
                        favoris.get(position).ordre = position;
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("ordre", position);
                        List<String> whereArgs = new ArrayList<String>(2);
                        whereArgs.add(favoris.get(position).arretId);
                        whereArgs.add(favoris.get(position).ligneId);
                        whereArgs.add(Integer.toString(favoris.get(position).macroDirection));
                        String whereClause = "arretId = :arretId and ligneId = :ligneId and macroDirection = :macroDirection";
                        TransportsRennesApplication.getDataBaseHelper().getWritableDatabase()
                                .update("ArretFavori", contentValues, whereClause, whereArgs.toArray(new String[3]));
                        favoris.get(autrePosition).ordre = autrePosition;
                        contentValues.put("ordre", autrePosition);
                        whereArgs.clear();
                        whereArgs.add(favoris.get(autrePosition).arretId);
                        whereArgs.add(favoris.get(autrePosition).ligneId);
                        whereArgs.add(Integer.toString(favoris.get(autrePosition).macroDirection));
                        TransportsRennesApplication.getDataBaseHelper().getWritableDatabase()
                                .update("ArretFavori", contentValues, whereClause, whereArgs.toArray(new String[3]));
                        notifyDataSetChanged();
                    }
                }
            });

        }
        if (position == favoris.size() - 1) {
            holder.moveDown.setVisibility(View.INVISIBLE);
        } else {
            holder.moveDown.setVisibility(View.VISIBLE);
            holder.moveDown.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (position < favoris.size() - 1) {
                        int autrePosition = position + 1;
                        favoris.set(position, favoris.get(autrePosition));
                        favoris.set(autrePosition, favori);
                        favoris.get(position).ordre = position;
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("ordre", position);
                        List<String> whereArgs = new ArrayList<String>(2);
                        whereArgs.add(favoris.get(position).arretId);
                        whereArgs.add(favoris.get(position).ligneId);
                        whereArgs.add(Integer.toString(favoris.get(position).macroDirection));
                        String whereClause = "arretId = :arretId and ligneId = :ligneId and macroDirection = :macroDirection";
                        TransportsRennesApplication.getDataBaseHelper().getWritableDatabase()
                                .update("ArretFavori", contentValues, whereClause, whereArgs.toArray(new String[3]));
                        favoris.get(autrePosition).ordre = autrePosition;
                        contentValues.put("ordre", autrePosition);
                        whereArgs.clear();
                        whereArgs.add(favoris.get(autrePosition).arretId);
                        whereArgs.add(favoris.get(autrePosition).ligneId);
                        whereArgs.add(Integer.toString(favoris.get(autrePosition).macroDirection));
                        TransportsRennesApplication.getDataBaseHelper().getWritableDatabase()
                                .update("ArretFavori", contentValues, whereClause, whereArgs.toArray(new String[3]));
                        notifyDataSetChanged();
                    }
                }
            });
        }

        holder.arret.setText(favori.nomArret);
        holder.direction.setText(favori.direction);
        holder.iconeLigne.setImageResource(IconeLigne.getIconeResource(favori.nomCourt));

        try {
            List<DetailArretConteneur> prochainsDepart = Horaire.getProchainHorairesAsList(favori.ligneId,
                    favori.arretId, 1, calendar, favori.macroDirection);

            if (!prochainsDepart.isEmpty()) {
                holder.tempsRestant.setText(formatterCalendar(prochainsDepart.get(0).getHoraire(), now));
            }
        } catch (SQLiteException ignore) {
        }


        return convertView1;
    }


    private CharSequence formatterCalendar(int prochainDepart, int now) {
        StringBuilder stringBuilder = new StringBuilder();
        int tempsEnMinutes = prochainDepart - now;
        if (tempsEnMinutes < 0) {
            stringBuilder.append(myContext.getString(R.string.tropTard));
        } else {
            int heures = tempsEnMinutes / 60;
            int minutes = tempsEnMinutes - heures * 60;
            boolean tempsAjoute = false;
            if (heures > 0) {
                stringBuilder.append(heures);
                stringBuilder.append(' ');
                stringBuilder.append(myContext.getString(R.string.miniHeures));
                stringBuilder.append(' ');
                tempsAjoute = true;
            }
            if (minutes > 0) {
                if (heures <= 0) {
                    stringBuilder.append(minutes);
                    stringBuilder.append(' ');
                    stringBuilder.append(myContext.getString(R.string.miniMinutes));
                } else {
                    if (minutes < 10) {
                        stringBuilder.append('0');
                    }
                    stringBuilder.append(minutes);
                }
                tempsAjoute = true;
            }
            if (!tempsAjoute) {
                stringBuilder.append("0 ");
                stringBuilder.append(myContext.getString(R.string.miniMinutes));
            }
        }
        return stringBuilder.toString();
    }

}
