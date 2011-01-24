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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.TransportsRennesApplication;
import fr.ybo.transportsrennes.keolis.gtfs.modele.ArretFavori;
import fr.ybo.transportsrennes.util.IconeLigne;
import fr.ybo.transportsrennes.util.JoursFeries;
import fr.ybo.transportsrennes.util.LogYbo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FavoriAdapter extends BaseAdapter {
	private final static Class<?> classDrawable = R.drawable.class;

	private final static LogYbo LOG_YBO = new LogYbo(FavoriAdapter.class);

	private final LayoutInflater mInflater;

	private final List<ArretFavori> favoris;

	private int now;
	private Calendar calendar;
	private Calendar calendarLaVeille;

	public FavoriAdapter(final Context context, final List<ArretFavori> favoris) {
		// Cache the LayoutInflate to avoid asking for a new one each time.
		mInflater = LayoutInflater.from(context);
		this.favoris = favoris;
		majCalendar();
	}


	public void majCalendar() {
		calendar = Calendar.getInstance();
		calendarLaVeille = Calendar.getInstance();
		calendarLaVeille.roll(Calendar.DATE, false);
		now = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
	}

	public List<ArretFavori> getFavoris() {
		return favoris;
	}

	public int getCount() {
		return favoris.size();
	}

	public ArretFavori getItem(final int position) {
		return favoris.get(position);
	}

	public long getItemId(final int position) {
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

	public View getView(final int position, View convertView, final ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.favori, null);
			holder = new ViewHolder();
			holder.iconeLigne = (ImageView) convertView.findViewById(R.id.iconeLigne);
			holder.arret = (TextView) convertView.findViewById(R.id.nomArret);
			holder.direction = (TextView) convertView.findViewById(R.id.directionArret);
			holder.tempsRestant = (TextView) convertView.findViewById(R.id.tempsRestant);
			holder.moveUp = (ImageView) convertView.findViewById(R.id.moveUp);
			holder.moveDown = (ImageView) convertView.findViewById(R.id.moveDown);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

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
						String whereClause = "arretId = :arretId and ligneId = :ligneId";
						List<String> whereArgs = new ArrayList<String>(2);
						whereArgs.add(favoris.get(position).arretId);
						whereArgs.add(favoris.get(position).ligneId);
						TransportsRennesApplication.getDataBaseHelper().getWritableDatabase()
								.update("ArretFavori", contentValues, whereClause, whereArgs.toArray(new String[2]));
						favoris.get(autrePosition).ordre = autrePosition;
						contentValues.put("ordre", autrePosition);
						whereArgs.clear();
						whereArgs.add(favoris.get(autrePosition).arretId);
						whereArgs.add(favoris.get(autrePosition).ligneId);
						TransportsRennesApplication.getDataBaseHelper().getWritableDatabase()
								.update("ArretFavori", contentValues, whereClause, whereArgs.toArray(new String[2]));
						FavoriAdapter.this.notifyDataSetChanged();
					}
				}
			});

		}
		if (position == (favoris.size() - 1)) {
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
						String whereClause = "arretId = :arretId and ligneId = :ligneId";
						List<String> whereArgs = new ArrayList<String>(2);
						whereArgs.add(favoris.get(position).arretId);
						whereArgs.add(favoris.get(position).ligneId);
						TransportsRennesApplication.getDataBaseHelper().getWritableDatabase()
								.update("ArretFavori", contentValues, whereClause, whereArgs.toArray(new String[2]));
						favoris.get(autrePosition).ordre = autrePosition;
						contentValues.put("ordre", autrePosition);
						whereArgs.clear();
						whereArgs.add(favoris.get(autrePosition).arretId);
						whereArgs.add(favoris.get(autrePosition).ligneId);
						TransportsRennesApplication.getDataBaseHelper().getWritableDatabase()
								.update("ArretFavori", contentValues, whereClause, whereArgs.toArray(new String[2]));
						FavoriAdapter.this.notifyDataSetChanged();
					}
				}
			});
		}

		holder.arret.setText(favori.nomArret);
		holder.direction.setText(favori.direction);
		holder.iconeLigne.setImageResource(IconeLigne.getIconeResource(favori.nomCourt));

		StringBuilder requete = new StringBuilder();
		requete.append("select (Horaire.heureDepart - :uneJournee) as _id ");
		requete.append("from Calendrier,  Horaire_");
		requete.append(favori.ligneId);
		requete.append(" as Horaire, Trajet ");
		requete.append("where ");
		requete.append(clauseWhereForTodayCalendrier(calendarLaVeille));
		requete.append(" and Trajet.id = Horaire.trajetId");
		requete.append(" and Trajet.calendrierId = Calendrier.id");
		requete.append(" and Trajet.ligneId = :routeId1");
		requete.append(" and Horaire.arretId = :arretId1");
		requete.append(" and Horaire.heureDepart >= :maintenantHier ");
		requete.append(" and Horaire.terminus = 0 ");
		requete.append("UNION ");
		requete.append("select Horaire.heureDepart as _id ");
		requete.append("from Calendrier,  Horaire_");
		requete.append(favori.ligneId);
		requete.append(" as Horaire, Trajet ");
		requete.append("where ");
		requete.append(clauseWhereForTodayCalendrier(calendar));
		requete.append(" and Trajet.id = Horaire.trajetId");
		requete.append(" and Trajet.calendrierId = Calendrier.id");
		requete.append(" and Trajet.ligneId = :routeId2");
		requete.append(" and Horaire.arretId = :arretId2");
		requete.append(" and Horaire.heureDepart >= :maintenant");
		requete.append(" and Horaire.terminus = 0");
		requete.append(" order by _id limit 1;");
		int uneJournee = 24 * 60;
		List<String> selectionArgs = new ArrayList<String>();
		selectionArgs.add(Integer.toString(uneJournee));
		selectionArgs.add(favori.ligneId);
		selectionArgs.add(favori.arretId);
		selectionArgs.add(Integer.toString(now + (uneJournee)));
		selectionArgs.add(favori.ligneId);
		selectionArgs.add(favori.arretId);
		selectionArgs.add(Integer.toString(now));
		try {
			Cursor currentCursor = TransportsRennesApplication.getDataBaseHelper().executeSelectQuery(requete.toString(), selectionArgs);
			if (currentCursor.moveToFirst()) {
				int prochainDepart = currentCursor.getInt(0);
				holder.tempsRestant.setText(formatterCalendar(prochainDepart, now));
			}

			currentCursor.close();
		} catch (SQLiteException sqlException) {
			LOG_YBO.erreur("Erreur SQL reçue lors de la récupération du prochain départ, ça doit pas arriver, mais on ignore l'erreur au cas où",
					sqlException);
		}


		return convertView;
	}


	private String formatterCalendar(int prochainDepart, int now) {
		StringBuilder stringBuilder = new StringBuilder();
		int tempsEnMinutes = prochainDepart - now;
		if (tempsEnMinutes < 0) {
			stringBuilder.append("Trop tard!");
		} else {
			int heures = tempsEnMinutes / 60;
			int minutes = tempsEnMinutes - heures * 60;
			boolean tempsAjoute = false;
			if (heures > 0) {
				stringBuilder.append(heures);
				stringBuilder.append(" h ");
				tempsAjoute = true;
			}
			if (minutes > 0) {
				if (heures <= 0) {
					stringBuilder.append(minutes);
					stringBuilder.append(" min");
				} else {
					if (minutes < 10) {
						stringBuilder.append("0");
					}
					stringBuilder.append(minutes);
				}
				tempsAjoute = true;
			}
			if (!tempsAjoute) {
				stringBuilder.append("0 min");
			}
		}
		return stringBuilder.toString();
	}

	private String clauseWhereForTodayCalendrier(Calendar calendar) {
		if (JoursFeries.isJourFerie(calendar.getTime())) {
			return "Dimanche = 1";
		}
		switch (calendar.get(Calendar.DAY_OF_WEEK)) {
			case Calendar.MONDAY:
				return "Lundi = 1";
			case Calendar.TUESDAY:
				return "Mardi = 1";
			case Calendar.WEDNESDAY:
				return "Mercredi = 1";
			case Calendar.THURSDAY:
				return "Jeudi = 1";
			case Calendar.FRIDAY:
				return "Vendredi = 1";
			case Calendar.SATURDAY:
				return "Samedi = 1";
			case Calendar.SUNDAY:
				return "Dimanche = 1";
			default:
				return null;
		}
	}

}
