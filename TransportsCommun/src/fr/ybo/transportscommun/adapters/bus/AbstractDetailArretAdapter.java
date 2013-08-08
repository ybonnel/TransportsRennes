package fr.ybo.transportscommun.adapters.bus;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import fr.ybo.transportscommun.AbstractTransportsApplication;
import fr.ybo.transportscommun.R;
import fr.ybo.transportscommun.donnees.modele.DetailArretConteneur;
import fr.ybo.transportscommun.util.Theme;

public abstract class AbstractDetailArretAdapter extends BaseAdapter {

	protected abstract int getLayout();

	private final int now;

	private final int secondesNow;

	private final LayoutInflater inflater;
	private List<DetailArretConteneur> prochainsDeparts;

	private final Context myContext;

	private boolean isToday;

	private int positionToMove;

	private String currentDirection;

	public int getPositionToMove() {
		return positionToMove;
	}

	public AbstractDetailArretAdapter(Context context, List<DetailArretConteneur> prochainsDeparts, int now,
			boolean isToday, String currentDirection, int secondesNow) {
		this.isToday = isToday;
		this.currentDirection = currentDirection;
		myContext = context;
		this.now = now;
		this.secondesNow = secondesNow;
		inflater = LayoutInflater.from(context);
		this.prochainsDeparts = prochainsDeparts;
		if (isToday) {
			positionToMove = 0;
			for (DetailArretConteneur horaire : prochainsDeparts) {
				if (horaire.getHoraire() < now) {
					positionToMove++;
				}
			}
		} else {
			positionToMove = 0;
		}
	}

	private static class ViewHolder {
		TextView heureProchain;
		TextView tempsRestant;
		TextView direction;
	}

	public int getCount() {
		return prochainsDeparts.size();
	}

	public DetailArretConteneur getItem(int position) {
		return prochainsDeparts.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View convertView1 = convertView;
		AbstractDetailArretAdapter.ViewHolder holder;
		if (convertView1 == null) {
			convertView1 = inflater.inflate(getLayout(), parent, false);
			holder = new AbstractDetailArretAdapter.ViewHolder();
			holder.heureProchain = (TextView) convertView1.findViewById(R.id.detailArret_heureProchain);
			holder.tempsRestant = (TextView) convertView1.findViewById(R.id.detailArret_tempsRestant);
			holder.direction = (TextView) convertView1.findViewById(R.id.detailArret_directionTrajet);
			convertView1.setTag(holder);
		} else {
			holder = (AbstractDetailArretAdapter.ViewHolder) convertView1.getTag();
		}
		int prochainDepart = prochainsDeparts.get(position).getHoraire();
		holder.heureProchain.setText(formatterCalendarHeure(prochainDepart, prochainsDeparts.get(position)
				.getSecondes()));
		holder.heureProchain.setTextColor(AbstractTransportsApplication.getTextColor(myContext));
		holder.tempsRestant.setTextColor(AbstractTransportsApplication.getTextColor(myContext));
		holder.direction.setTextColor(AbstractTransportsApplication.getTextColor(myContext));
		if (isToday) {
			holder.tempsRestant.setText(formatterCalendar(prochainDepart, now, secondesNow,
					prochainsDeparts.get(position).getSecondes(), prochainsDeparts.get(position).isAccurate()));
			if (prochainsDeparts.get(position).getSecondes() != null) {
				if (AbstractTransportsApplication.getTheme(myContext) == Theme.NOIR) {
					holder.heureProchain.setTextColor(Color.rgb(150, 150, 255));
					holder.tempsRestant.setTextColor(Color.rgb(150, 150, 255));
				} else {
					holder.tempsRestant.setTextColor(Color.rgb(0, 0, 125));
					holder.heureProchain.setTextColor(Color.rgb(0, 0, 125));
				}
			}
		} else {
			holder.tempsRestant.setText("");
		}
		if (prochainsDeparts.get(position).getDirection().equals(currentDirection)) {
			holder.direction.setVisibility(View.GONE);
		} else {
			holder.direction.setVisibility(View.VISIBLE);
			holder.direction.setText(prochainsDeparts.get(position).getDirection());
		}
		return convertView1;
	}

	private String formatterCalendar(int prochainDepart, int now, int secondesNow, Integer secondes, boolean accurate) {
		StringBuilder stringBuilder = new StringBuilder();
		int secondesNullSafe = secondes == null ? 0 : secondes.intValue();
		int tempsEnSecondes = (prochainDepart * 60 + secondesNullSafe) - (now * 60 + secondesNow);
		int tempsEnMinutes = (tempsEnSecondes / 60);
		if (tempsEnSecondes == tempsEnMinutes * 60) {
			tempsEnMinutes--;
		}
		if (tempsEnMinutes > 0) {
			stringBuilder.append(myContext.getString(R.string.dans));
			stringBuilder.append(' ');
			int heures = tempsEnMinutes / 60;
			int minutes = tempsEnMinutes - heures * 60;
			boolean tempsAjoute = false;
			if (heures > 0) {
				stringBuilder.append(heures);
				stringBuilder.append(' ');
				stringBuilder.append(myContext.getString(R.string.heures));
				stringBuilder.append(' ');
				tempsAjoute = true;
			}
			if (minutes > 0) {
				stringBuilder.append(minutes);
				stringBuilder.append(' ');
				stringBuilder.append(myContext.getString(R.string.minutes));
				tempsAjoute = true;
			}
			if (!tempsAjoute) {
				stringBuilder.append("0 ");
				stringBuilder.append(myContext.getString(R.string.minutes));
			}
		} else if (tempsEnMinutes == 0) {
			stringBuilder.append("< 1 ");
			stringBuilder.append(myContext.getString(R.string.miniMinutes));
		}
		if (secondes != null && !accurate) {
			stringBuilder.append("*");
		}
		return stringBuilder.toString();
	}

	private CharSequence formatterCalendarHeure(int prochainDepart, Integer secondes) {
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

		if (secondes != null) {
			stringBuilder.append(':');
			String secondesChaine = secondes.toString();
			if (secondesChaine.length() < 2) {
				stringBuilder.append('0');
			}
			stringBuilder.append(secondesChaine);
		}
		return stringBuilder.toString();
	}

}
