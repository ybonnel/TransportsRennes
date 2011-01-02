package fr.ybo.transportsrennes.adapters;

import android.content.Context;
import android.database.Cursor;
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
import fr.ybo.transportsrennes.keolis.gtfs.modele.Route;
import fr.ybo.transportsrennes.util.Formatteur;
import fr.ybo.transportsrennes.util.JoursFeries;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FavoriAdapter extends BaseAdapter {
	private final static Class<?> classDrawable = R.drawable.class;

	static class ViewHolder {
		LinearLayout conteneur;
		TextView arret;
		TextView direction;
		TextView tempsRestant;
	}

	private final LayoutInflater mInflater;

	private final List<ArretFavori> favoris;

	private int now;
	private Calendar calendar;

	public FavoriAdapter(final Context context, final List<ArretFavori> favoris) {
		// Cache the LayoutInflate to avoid asking for a new one each time.
		mInflater = LayoutInflater.from(context);
		this.favoris = favoris;
		calendar = Calendar.getInstance();
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

	public View getView(final int position, View convertView, final ViewGroup parent) {
		ViewHolder holder;


		convertView = mInflater.inflate(R.layout.favori, null);

		holder = new ViewHolder();
		holder.conteneur = (LinearLayout) convertView.findViewById(R.id.conteneurImage);
		holder.arret = (TextView) convertView.findViewById(R.id.nomArret);
		holder.direction = (TextView) convertView.findViewById(R.id.directionArret);
		holder.tempsRestant = (TextView) convertView.findViewById(R.id.tempsRestant);

		convertView.setTag(holder);

		ArretFavori favori = favoris.get(position);

		holder.arret.setText(Formatteur.formatterChaine(favori.getNomArret()));
		holder.direction.setText(Formatteur.formatterChaine(favori.getDirection().replaceAll(favori.getRouteNomCourt(), "")));
		try {
			Field fieldIcon = classDrawable.getDeclaredField("i" + favori.getRouteNomCourt().toLowerCase());
			int ressourceImg = fieldIcon.getInt(null);
			ImageView imgView = new ImageView(mInflater.getContext());
			imgView.setImageResource(ressourceImg);
			holder.conteneur.addView(imgView);
		} catch (NoSuchFieldException e) {
			TextView textView = new TextView(mInflater.getContext());
			textView.setTextSize(16);
			textView.setText(favori.getRouteNomCourt());
			holder.conteneur.addView(textView);
		} catch (IllegalAccessException e) {
			TextView textView = new TextView(mInflater.getContext());
			textView.setTextSize(16);
			textView.setText(favori.getRouteNomCourt());
			holder.conteneur.addView(textView);
		}

		StringBuilder requete = new StringBuilder();
		requete.append("select HeuresArrets.heureDepart as _id ");
		requete.append("from Calendrier,  HeuresArrets");
		requete.append(Route.getIdWithoutSpecCar(favori.getRouteId()));
		requete.append(" as HeuresArrets ");
		requete.append("where ");
		requete.append(clauseWhereForTodayCalendrier(calendar));
		requete.append(" and HeuresArrets.serviceId = Calendrier.id");
		requete.append(" and HeuresArrets.routeId = :routeId");
		requete.append(" and HeuresArrets.stopId = :arretId");
		requete.append(" and HeuresArrets.heureDepart >= :maintenant");
		requete.append(" order by HeuresArrets.heureDepart limit 1;");
		List<String> selectionArgs = new ArrayList<String>();
		selectionArgs.add(favori.getRouteId());
		selectionArgs.add(favori.getStopId());
		selectionArgs.add(Long.toString(now));
		Cursor currentCursor = TransportsRennesApplication.getDataBaseHelper().executeSelectQuery(requete.toString(), selectionArgs);
		if (currentCursor.moveToFirst()) {
			int prochainDepart = currentCursor.getInt(0);
			holder.tempsRestant.setText(formatterCalendar(prochainDepart, now));
		}

		currentCursor.close();

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
				stringBuilder.append(minutes);
				stringBuilder.append(" min ");
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
