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
package fr.ybo.transportscommun.donnees.modele;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import fr.ybo.database.annotation.Column;
import fr.ybo.database.annotation.Entity;
import fr.ybo.database.annotation.PrimaryKey;
import fr.ybo.moteurcsv.adapter.AdapterBoolean;
import fr.ybo.moteurcsv.adapter.AdapterInteger;
import fr.ybo.moteurcsv.annotation.BaliseCsv;
import fr.ybo.moteurcsv.annotation.FichierCsv;
import fr.ybo.transportscommun.AbstractTransportsApplication;
import fr.ybo.transportscommun.util.JoursFeries;
import fr.ybo.transportscommun.util.LogYbo;

@FichierCsv("horaires.txt")
@Entity
public class Horaire {
    @BaliseCsv("arret_id")
    @Column
    @PrimaryKey
    public String arretId;
    @BaliseCsv(value = "trajet_id", adapter = AdapterInteger.class)
    @Column(type = Column.TypeColumn.INTEGER)
    @PrimaryKey
    public Integer trajetId;
    @BaliseCsv(value = "heure_depart", adapter = AdapterInteger.class)
    @Column(type = Column.TypeColumn.INTEGER)
    public Integer heureDepart;
    @BaliseCsv(value = "stop_sequence", adapter = AdapterInteger.class)
    @Column(type = Column.TypeColumn.INTEGER)
    public Integer stopSequence;
    @BaliseCsv(value = "terminus", adapter = AdapterBoolean.class)
    @Column(type = Column.TypeColumn.BOOLEAN)
    public Boolean terminus;

    private static final LogYbo LOG_YBO = new LogYbo(Horaire.class);

	private static final SimpleDateFormat FORMAT_DATE_CALENDRIER = new SimpleDateFormat("yyyyMMdd");

	public static List<DetailArretConteneur> getProchainHorairesAsList(String ligneId, String arretId, Integer limit,
			Calendar calendar, Integer macroDirection) throws SQLiteException {
		List<DetailArretConteneur> prochainsDeparts = new ArrayList<DetailArretConteneur>();
		Cursor cursor = getProchainHorairesAsCursor(ligneId, arretId, calendar, macroDirection);
		int heureDepartCol = cursor.getColumnIndex("_id");
		int todayCol = cursor.getColumnIndex("today");
		int calendrierIdCol = cursor.getColumnIndex("calendrierId");
		int trajetIdCol = cursor.getColumnIndex("trajetId");
		int stopSequenceCol = cursor.getColumnIndex("stopSequence");
		int directionCol = cursor.getColumnIndex("direction");
		Set<Integer> horairesDejaAjoutes = new HashSet<Integer>();
		while (cursor.moveToNext()) {
			if (!getExceptionsSuppr(cursor.getString(todayCol)).contains(cursor.getInt(calendrierIdCol))) {
				int heureDepart = cursor.getInt(heureDepartCol);
				if (!horairesDejaAjoutes.contains(heureDepart)) {
					prochainsDeparts.add(new DetailArretConteneur(heureDepart, cursor.getInt(trajetIdCol), cursor
							.getInt(stopSequenceCol), cursor.getString(directionCol)));
					horairesDejaAjoutes.add(heureDepart);
				}
			}
		}
		cursor.close();
		int now = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
		String today = FORMAT_DATE_CALENDRIER.format(calendar.getTime());
		for (Integer calendrierId : getExceptionsAjout(today)) {
			cursor = getAllHorairesAsCursorForCalendrierId(ligneId, arretId, calendrierId, macroDirection);
			heureDepartCol = cursor.getColumnIndex("_id");
			trajetIdCol = cursor.getColumnIndex("trajetId");
			stopSequenceCol = cursor.getColumnIndex("stopSequence");
			directionCol = cursor.getColumnIndex("direction");
			while (cursor.moveToNext()) {
				int heureDepart = cursor.getInt(heureDepartCol);
				if (heureDepart >= now && !horairesDejaAjoutes.contains(heureDepart)) {
					prochainsDeparts.add(new DetailArretConteneur(heureDepart, cursor.getInt(trajetIdCol), cursor
							.getInt(stopSequenceCol), cursor.getString(directionCol)));
					horairesDejaAjoutes.add(heureDepart);
				}
			}
		}
		Collections.sort(prochainsDeparts, new Comparator<DetailArretConteneur>() {
			public int compare(DetailArretConteneur detailArretConteneur1, DetailArretConteneur detailArretConteneur2) {
				int x = detailArretConteneur1.getHoraire();
				int y = detailArretConteneur2.getHoraire();
				return (x < y) ? -1 : ((x == y) ? 0 : 1);
			}
		});

		if (limit == null || prochainsDeparts.size() <= limit) {
			return prochainsDeparts;
		}
		return prochainsDeparts.subList(0, limit);
	}

	public static List<DetailArretConteneur> getAllHorairesAsList(String ligneId, String arretId, Calendar calendar, Integer macroDirection)
			throws SQLiteException {
		List<DetailArretConteneur> prochainsDeparts = new ArrayList<DetailArretConteneur>();
		Cursor cursor = getAllHorairesAsCursor(ligneId, arretId, calendar, macroDirection);
		int heureDepartCol = cursor.getColumnIndex("_id");
		int todayCol = cursor.getColumnIndex("today");
		int calendrierIdCol = cursor.getColumnIndex("calendrierId");
		int trajetIdCol = cursor.getColumnIndex("trajetId");
		int stopSequenceCol = cursor.getColumnIndex("stopSequence");
		int directionCol = cursor.getColumnIndex("direction");
		Set<Integer> horairesDejaAjoutes = new HashSet<Integer>();
		while (cursor.moveToNext()) {
			if (!getExceptionsSuppr(cursor.getString(todayCol)).contains(cursor.getInt(calendrierIdCol))) {
				int heureDepart = cursor.getInt(heureDepartCol);
				if (!horairesDejaAjoutes.contains(heureDepart)) {
					prochainsDeparts.add(new DetailArretConteneur(heureDepart, cursor.getInt(trajetIdCol), cursor
							.getInt(stopSequenceCol), cursor.getString(directionCol)));
					horairesDejaAjoutes.add(heureDepart);
				}
			}
		}
		cursor.close();
		String today = FORMAT_DATE_CALENDRIER.format(calendar.getTime());
		for (Integer calendrierId : getExceptionsAjout(today)) {
			cursor = getAllHorairesAsCursorForCalendrierId(ligneId, arretId, calendrierId, macroDirection);
			heureDepartCol = cursor.getColumnIndex("_id");
			trajetIdCol = cursor.getColumnIndex("trajetId");
			stopSequenceCol = cursor.getColumnIndex("stopSequence");
			directionCol = cursor.getColumnIndex("direction");
			while (cursor.moveToNext()) {
				int heureDepart = cursor.getInt(heureDepartCol);
				if (!horairesDejaAjoutes.contains(heureDepart)) {
					prochainsDeparts.add(new DetailArretConteneur(heureDepart, cursor.getInt(trajetIdCol), cursor
							.getInt(stopSequenceCol), cursor.getString(directionCol)));
					horairesDejaAjoutes.add(heureDepart);
				}
			}
		}
		Collections.sort(prochainsDeparts, new Comparator<DetailArretConteneur>() {
			public int compare(DetailArretConteneur detailArretConteneur1, DetailArretConteneur detailArretConteneur2) {
				int x = detailArretConteneur1.getHoraire();
				int y = detailArretConteneur2.getHoraire();
				return (x < y) ? -1 : ((x == y) ? 0 : 1);
			}
		});
		return prochainsDeparts;
	}

	private static Map<String, Set<Integer>> mapCalendriersExceptionSuppr = new HashMap<String, Set<Integer>>();
	private static Map<String, Set<Integer>> mapCalendriersExceptionAjout = new HashMap<String, Set<Integer>>();

	private static void remplirException(String date) {
		if (!mapCalendriersExceptionSuppr.containsKey(date)) {
			Set<Integer> setExceptionsSuppr = new HashSet<Integer>();
			Set<Integer> setExceptionsAjout = new HashSet<Integer>();
			CalendrierException calendrierException = new CalendrierException();
			calendrierException.date = date;
			for (CalendrierException exception : AbstractTransportsApplication.getDataBaseHelper().select(
					calendrierException)) {
				if (!exception.ajout) {
					setExceptionsSuppr.add(exception.calendrierId);
				} else {
					setExceptionsAjout.add(exception.calendrierId);
				}
			}
			mapCalendriersExceptionSuppr.put(date, setExceptionsSuppr);
			mapCalendriersExceptionAjout.put(date, setExceptionsAjout);
		}
	}

	private static Set<Integer> getExceptionsSuppr(String date) {
		remplirException(date);
		return mapCalendriersExceptionSuppr.get(date);
	}

	private static Set<Integer> getExceptionsAjout(String date) {
		remplirException(date);
		return mapCalendriersExceptionAjout.get(date);
	}

	private static String clauseWhereForCalendrier(Calendar calendar) {
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

	private static Cursor getAllHorairesAsCursorForCalendrierId(String ligneId, String arretId, Integer calendarId, Integer macroDirection) {
		StringBuilder requete = new StringBuilder();
		requete.append("select Horaire.heureDepart as _id, Trajet.id as trajetId, ");
		requete.append("Horaire.stopSequence as stopSequence, Direction.direction as direction ");
		requete.append("from Horaire_");
		requete.append(ligneId);
		requete.append(" as Horaire, Trajet, Direction ");
		requete.append("where ");
		requete.append(" Trajet.calendrierId = :calendrierId");
		requete.append(" and Trajet.id = Horaire.trajetId");
		requete.append(" and Trajet.directionId = Direction.id");
		requete.append(" and Trajet.ligneId = :ligneId");
		if (macroDirection != null) {
			requete.append(" and Trajet.macroDirection = :macroDirection1");
		}
		requete.append(" and Horaire.arretId = :arretId");
		requete.append(" and Horaire.terminus = 0");
		requete.append(" order by Horaire.heureDepart;");
		List<String> selectionArgs = new ArrayList<String>();
		selectionArgs.add(calendarId.toString());
		selectionArgs.add(ligneId);
		if (macroDirection != null) {
			selectionArgs.add(macroDirection.toString());
		}
		selectionArgs.add(arretId);
		LOG_YBO.debug("Requete : " + requete.toString());
		LOG_YBO.debug("SelectionArgs : " + selectionArgs);
		return AbstractTransportsApplication.getDataBaseHelper().executeSelectQuery(requete.toString(), selectionArgs);
	}

	private static Cursor getAllHorairesAsCursor(String ligneId, String arretId, Calendar calendar, Integer macroDirection) {
		StringBuilder requete = new StringBuilder();
		requete.append("select Horaire.heureDepart as _id, :today0 as today, Calendrier.id as calendrierId, ");
		requete.append("Trajet.id as trajetId, Horaire.stopSequence as stopSequence, Direction.direction ");
		requete.append("from Calendrier,  Horaire_");
		requete.append(ligneId);
		requete.append(" as Horaire, Trajet, Direction ");
		requete.append("where ");
		requete.append(" Calendrier.dateDebut <= :today1");
		requete.append(" and Calendrier.dateFin >= :today2");
		requete.append(" and ");
		requete.append(clauseWhereForCalendrier(calendar));
		requete.append(" and Trajet.calendrierId = Calendrier.id");
		requete.append(" and Trajet.id = Horaire.trajetId");
		requete.append(" and Trajet.directionId = Direction.id");
		requete.append(" and Trajet.ligneId = :ligneId");
		if (macroDirection != null) {
			requete.append(" and Trajet.macroDirection = :macroDirection1");
		}
		requete.append(" and Horaire.arretId = :arretId");

		if (!JoursFeries.is1erMai(calendar.getTime())) {
			requete.append(" and Horaire.terminus = 0");
		} else {
			requete.append(" and Horaire.terminus = 2");
		}
		requete.append(" order by Horaire.heureDepart;");
		List<String> selectionArgs = new ArrayList<String>(2);
		String today = FORMAT_DATE_CALENDRIER.format(calendar.getTime());
		selectionArgs.add(today);
		selectionArgs.add(today);
		selectionArgs.add(today);
		selectionArgs.add(ligneId);
		if (macroDirection != null) {
			selectionArgs.add(macroDirection.toString());
		}
		selectionArgs.add(arretId);
		LOG_YBO.debug("Requete : " + requete.toString());
		LOG_YBO.debug("SelectionArgs : " + selectionArgs);
		return AbstractTransportsApplication.getDataBaseHelper().executeSelectQuery(requete.toString(), selectionArgs);
	}

	private static Cursor getProchainHorairesAsCursor(String ligneId, String arretId, Calendar calendar, Integer macroDirection)
			throws SQLiteException {
		int now = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
		Calendar calendarLaVeille = Calendar.getInstance();
		calendarLaVeille.add(Calendar.DATE, -1);

		int uneJournee = 24 * 60;
		// Réquète.
		List<String> selectionArgs = new ArrayList<String>(7);
		StringBuilder requete = new StringBuilder();
		if (!JoursFeries.is1erMai(calendarLaVeille.getTime())) {
			requete.append("select (Horaire.heureDepart - :uneJournee) as _id, :veille0 as today, Calendrier.id as calendrierId, ");
			requete.append("Trajet.id as trajetId, Horaire.stopSequence as stopSequence, Direction.direction as direction ");
			requete.append("from Calendrier, Horaire_");
			requete.append(ligneId);
			requete.append(" as Horaire, Trajet, Direction ");
			requete.append("where ");
			requete.append(" Calendrier.dateDebut <= :veille1");
			requete.append(" and Calendrier.dateFin >= :veille2");
			requete.append(" and ");
			requete.append(clauseWhereForCalendrier(calendarLaVeille));
			requete.append(" and Trajet.id = Horaire.trajetId");
			requete.append(" and Trajet.calendrierId = Calendrier.id");
			requete.append(" and Trajet.directionId = Direction.id");
			requete.append(" and Trajet.ligneId = :routeId1");
			if (macroDirection != null) {
				requete.append(" and Trajet.macroDirection = :macroDirection1");
			}
			requete.append(" and Horaire.arretId = :arretId1");
			requete.append(" and Horaire.terminus = 0");
			requete.append(" and Horaire.heureDepart >= :maintenantHier ");

			String veille = FORMAT_DATE_CALENDRIER.format(calendarLaVeille.getTime());
			selectionArgs.add(Integer.toString(uneJournee));
			selectionArgs.add(veille);
			selectionArgs.add(veille);
			selectionArgs.add(veille);
			selectionArgs.add(ligneId);
			if (macroDirection != null) {
				selectionArgs.add(macroDirection.toString());
			}
			selectionArgs.add(arretId);
			selectionArgs.add(Integer.toString(now + uneJournee));
		}
		if (!JoursFeries.is1erMai(calendar.getTime())) {
			if (requete.length() > 0) {
				requete.append("UNION ");
			}
			requete.append("select Horaire.heureDepart as _id, :today0 as today, Calendrier.id as calendrierId, ");
			requete.append("Trajet.id as trajetId, Horaire.stopSequence as stopSequence, Direction.direction as direction ");
			requete.append("from Calendrier,  Horaire_");
			requete.append(ligneId);
			requete.append(" as Horaire, Trajet, Direction ");
			requete.append("where ");

			requete.append(" Calendrier.dateDebut <= :today1");
			requete.append(" and Calendrier.dateFin >= :today2");
			requete.append(" and ");
			requete.append(clauseWhereForCalendrier(calendar));
			requete.append(" and Trajet.id = Horaire.trajetId");
			requete.append(" and Trajet.calendrierId = Calendrier.id");
			requete.append(" and Trajet.directionId = Direction.id");
			requete.append(" and Trajet.ligneId = :routeId2");
			if (macroDirection != null) {
				requete.append(" and Trajet.macroDirection = :macroDirection2");
			}
			requete.append(" and Horaire.arretId = :arretId2");
			requete.append(" and Horaire.terminus = 0");
			requete.append(" and Horaire.heureDepart >= :maintenant");

			String today = FORMAT_DATE_CALENDRIER.format(calendar.getTime());
			selectionArgs.add(today);
			selectionArgs.add(today);
			selectionArgs.add(today);
			selectionArgs.add(ligneId);
			if (macroDirection != null) {
				selectionArgs.add(macroDirection.toString());
			}
			selectionArgs.add(arretId);
			selectionArgs.add(Integer.toString(now));
		}
		requete.append(" order by _id ");
		LOG_YBO.debug("Requete : " + requete.toString());
		LOG_YBO.debug("SelectionArgs : " + selectionArgs);
		return AbstractTransportsApplication.getDataBaseHelper().executeSelectQuery(requete.toString(), selectionArgs);
	}
}
