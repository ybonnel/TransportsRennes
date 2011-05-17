package fr.ybo.transportsbordeaux.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.database.sqlite.SQLiteException;
import fr.ybo.transportsbordeaux.tbc.Horaire;
import fr.ybo.transportsbordeaux.tbc.TbcErreurReseaux;

public class GestionnaireHoraires {

	public static class CleHoraires {
		public String ligneId;
		public String arretId;
		public int macroDirection;
		public String dateChaine;
		public Date date;

		public CleHoraires(String ligneId, String arretId, int macroDirection, Calendar calendar) {
			super();
			this.ligneId = ligneId;
			this.arretId = arretId;
			this.macroDirection = macroDirection;
			this.dateChaine = new StringBuilder(calendar.get(Calendar.YEAR)).append(calendar.get(Calendar.DAY_OF_YEAR))
					.toString();
			this.date = calendar.getTime();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((arretId == null) ? 0 : arretId.hashCode());
			result = prime * result + ((dateChaine == null) ? 0 : dateChaine.hashCode());
			result = prime * result + ((ligneId == null) ? 0 : ligneId.hashCode());
			result = prime * result + macroDirection;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			CleHoraires other = (CleHoraires) obj;
			if (arretId == null) {
				if (other.arretId != null)
					return false;
			} else if (!arretId.equals(other.arretId))
				return false;
			if (dateChaine == null) {
				if (other.dateChaine != null)
					return false;
			} else if (!dateChaine.equals(other.dateChaine))
				return false;
			if (ligneId == null) {
				if (other.ligneId != null)
					return false;
			} else if (!ligneId.equals(other.ligneId))
				return false;
			if (macroDirection != other.macroDirection)
				return false;
			return true;
		}

	}

	private static Map<CleHoraires, List<Integer>> mapHoraires = new HashMap<CleHoraires, List<Integer>>();

	private static List<Integer> getHoraires(CleHoraires cleHoraires) {
		if (!mapHoraires.containsKey(cleHoraires)) {
			try {
				List<Integer> horaires = new ArrayList<Integer>();
				for (Horaire horaire : Horaire.getHoraires(cleHoraires)) {
					horaires.add(horaire.horaire);
				}
				mapHoraires.put(cleHoraires, horaires);
			} catch (TbcErreurReseaux tbcErreurReseaux) {
				return new ArrayList<Integer>();
			}
		}
		return mapHoraires.get(cleHoraires);
	}

	public static List<Integer> getProchainHorairesAsList(String ligneId, String arretId, int macroDirection,
			Integer limit, Calendar calendar) throws SQLiteException {
		List<Integer> prochainsDeparts = new ArrayList<Integer>();
		int now = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
		for (Integer horaire : getHoraires(new CleHoraires(ligneId, arretId, macroDirection, calendar))) {
			if (horaire >= now) {
				prochainsDeparts.add(horaire);
			}
		}
		return prochainsDeparts;
	}

}
