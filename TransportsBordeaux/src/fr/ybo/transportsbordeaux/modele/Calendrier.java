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
 * 
 * Contributors:
 *     ybonnel - initial API and implementation
 */
package fr.ybo.transportsbordeaux.modele;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.ybo.database.annotation.Column;
import fr.ybo.database.annotation.Column.TypeColumn;
import fr.ybo.database.annotation.Entity;
import fr.ybo.database.annotation.PrimaryKey;
import fr.ybo.moteurcsv.adapter.AdapterBoolean;
import fr.ybo.moteurcsv.adapter.AdapterInteger;
import fr.ybo.moteurcsv.annotation.BaliseCsv;
import fr.ybo.moteurcsv.annotation.FichierCsv;
import fr.ybo.transportsbordeaux.TransportsBordeauxApplication;
import fr.ybo.transportsbordeaux.util.JoursFeries;

/**
 * Un calendrier.
 * @author ybonnel
 *
 */
@FichierCsv("calendriers.txt")
@Entity
public class Calendrier {
	@BaliseCsv(value = "id", adapter = AdapterInteger.class)
	@Column(type = TypeColumn.INTEGER)
	@PrimaryKey
	public Integer id;
	@BaliseCsv(value = "lundi", adapter = AdapterBoolean.class)
	@Column(type = TypeColumn.BOOLEAN)
	public Boolean lundi;
	@BaliseCsv(value = "mardi", adapter = AdapterBoolean.class)
	@Column(type = TypeColumn.BOOLEAN)
	public Boolean mardi;
	@BaliseCsv(value = "mercredi", adapter = AdapterBoolean.class)
	@Column(type = TypeColumn.BOOLEAN)
	public Boolean mercredi;
	@BaliseCsv(value = "jeudi", adapter = AdapterBoolean.class)
	@Column(type = TypeColumn.BOOLEAN)
	public Boolean jeudi;
	@BaliseCsv(value = "vendredi", adapter = AdapterBoolean.class)
	@Column(type = TypeColumn.BOOLEAN)
	public Boolean vendredi;
	@BaliseCsv(value = "samedi", adapter = AdapterBoolean.class)
	@Column(type = TypeColumn.BOOLEAN)
	public Boolean samedi;
	@BaliseCsv(value = "dimanche", adapter = AdapterBoolean.class)
	@Column(type = TypeColumn.BOOLEAN)
	public Boolean dimanche;
	@BaliseCsv(value = "dateDebut")
	@Column
	public String dateDebut;
	@BaliseCsv(value = "dateFin")
	@Column
	public String dateFin;

	private static List<Calendrier> calendriers = null;

	private synchronized static List<Calendrier> getCalendriers() {
		if (calendriers == null) {
			calendriers = TransportsBordeauxApplication.getDataBaseHelper().selectAll(Calendrier.class);
		}
		return calendriers;
	}

	private static Map<String, List<CalendrierException>> exceptionsByDates;

	private synchronized static Map<String, List<CalendrierException>> getExceptionsByDates() {
		if (exceptionsByDates == null) {
			exceptionsByDates = new HashMap<String, List<CalendrierException>>();
			for (CalendrierException exception : TransportsBordeauxApplication.getDataBaseHelper().selectAll(
					CalendrierException.class)) {
				if (!exceptionsByDates.containsKey(exception.date)) {
					exceptionsByDates.put(exception.date, new ArrayList<CalendrierException>());
				}
				exceptionsByDates.get(exception.date).add(exception);
			}
		}
		return exceptionsByDates;
	}

	private static final SimpleDateFormat FORMAT_DATE_CALENDRIER = new SimpleDateFormat("yyyyMMdd");

	private static Map<String, List<Integer>> calendriersIdByDate = new HashMap<String, List<Integer>>();

	public synchronized static List<Integer> getCalendriersIdForDate(Calendar calendar) {
		String today = FORMAT_DATE_CALENDRIER.format(calendar.getTime());
		if (calendriersIdByDate.containsKey(today)) {
			return calendriersIdByDate.get(today);
		}
		Set<Integer> exceptionsAjout = new HashSet<Integer>();
		Set<Integer> exceptionsSuppr = new HashSet<Integer>();
		if (getExceptionsByDates().containsKey(today)) {
			for (CalendrierException exception : getExceptionsByDates().get(today)) {
				if (exception.ajout) {
					exceptionsAjout.add(exception.calendrierId);
				} else {
					exceptionsSuppr.add(exception.calendrierId);
				}
			}
		}
		List<Integer> calendriers = new ArrayList<Integer>();
		for (Calendrier calendrier : getCalendriers()) {
			if (calendrier.isOkForCalendar(calendar, today) && !exceptionsSuppr.contains(calendrier.id)) {
				calendriers.add(calendrier.id);
			} else if (exceptionsAjout.contains(calendrier.id)) {
				calendriers.add(calendrier.id);
			}
		}
		calendriersIdByDate.put(today, calendriers);
		return calendriers;
	}

	private boolean isOkForCalendar(Calendar calendar, String today) {
		if (today.compareTo(dateDebut) < 0 || today.compareTo(dateFin) > 0) {
			return false;
		}
		if (JoursFeries.isJourFerie(calendar.getTime())) {
			return dimanche;
		}
		switch (calendar.get(Calendar.DAY_OF_WEEK)) {
			case Calendar.MONDAY:
				return lundi;
			case Calendar.TUESDAY:
				return mardi;
			case Calendar.WEDNESDAY:
				return mercredi;
			case Calendar.THURSDAY:
				return jeudi;
			case Calendar.FRIDAY:
				return vendredi;
			case Calendar.SATURDAY:
				return samedi;
			case Calendar.SUNDAY:
				return dimanche;
		}
		return false;
	}
}
