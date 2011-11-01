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
package fr.ybo.transportsrennes.database.modele;

import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import fr.ybo.database.annotation.Column;
import fr.ybo.database.annotation.Entity;
import fr.ybo.database.annotation.PrimaryKey;
import fr.ybo.moteurcsv.adapter.AdapterBoolean;
import fr.ybo.moteurcsv.adapter.AdapterInteger;
import fr.ybo.moteurcsv.annotation.BaliseCsv;
import fr.ybo.moteurcsv.annotation.FichierCsv;
import fr.ybo.transportsrennes.application.TransportsRennesApplication;
import fr.ybo.transportsrennes.util.JoursFeries;
import fr.ybo.transportsrennes.util.LogYbo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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

    public static List<Integer> getProchainHorairesAsList(String ligneId, String arretId, int macroDirection,
                                                          Integer limit, Calendar calendar) throws SQLiteException {
        List<Integer> prochainsDeparts = new ArrayList<Integer>();
        Cursor cursor = getProchainHorairesAsCursor(ligneId, arretId, macroDirection, limit, calendar);
        while (cursor.moveToNext()) {
            prochainsDeparts.add(cursor.getInt(0));
        }
        cursor.close();
        return prochainsDeparts;
    }

    public static Cursor getAllHorairesAsCursor(String ligneId, String arretId, int macroDirection, Calendar calendar) {
        StringBuilder requete = new StringBuilder();
        requete.append("select Horaire.heureDepart as _id,");
        requete.append(" Trajet.id as trajetId, stopSequence as sequence ");
        requete.append("from Calendrier,  Horaire_");
        requete.append(ligneId);
        requete.append(" as Horaire, Trajet ");
        requete.append("where ");
        requete.append(clauseWhereForTodayCalendrier(calendar));
        requete.append(" and Trajet.calendrierId = Calendrier.id");
        requete.append(" and Trajet.id = Horaire.trajetId");
        requete.append(" and Trajet.ligneId = :ligneId");
        requete.append(" and Horaire.arretId = :arretId");
        requete.append(" and Trajet.macroDirection = :macroDirection");

        if (JoursFeries.is1erMai(calendar.getTime())) {
            requete.append(" and Horaire.terminus = 2");
        } else {
            requete.append(" and Horaire.terminus = 0");
        }
        requete.append(" order by Horaire.heureDepart;");
        List<String> selectionArgs = new ArrayList<String>(2);
        selectionArgs.add(ligneId);
        selectionArgs.add(arretId);
        selectionArgs.add(Integer.toString(macroDirection));
        LOG_YBO.debug("Requete : " + requete.toString());
        LOG_YBO.debug("SelectionArgs : " + selectionArgs);
        return TransportsRennesApplication.getDataBaseHelper().executeSelectQuery(requete.toString(), selectionArgs);
    }

    public static Cursor getProchainHorairesAsCursor(String ligneId, String arretId, int macroDirection, Integer limit,
                                                     Calendar calendar) throws SQLiteException {
        int now = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
        Calendar calendarLaVeille = Calendar.getInstance();
        calendarLaVeille.add(Calendar.DATE, -1);

        int uneJournee = 24 * 60;
        // Réquète.
        List<String> selectionArgs = new ArrayList<String>(7);
        StringBuilder requete = new StringBuilder();
        if (!JoursFeries.is1erMai(calendarLaVeille.getTime())) {
            requete.append("select (Horaire.heureDepart - :uneJournee) as _id,");
            requete.append(" Trajet.id as trajetId, stopSequence as sequence ");
            requete.append("from Calendrier,  Horaire_");
            requete.append(ligneId);
            requete.append(" as Horaire, Trajet ");
            requete.append("where ");
            requete.append(clauseWhereForTodayCalendrier(calendarLaVeille));
            requete.append(" and Trajet.id = Horaire.trajetId");
            requete.append(" and Trajet.calendrierId = Calendrier.id");
            requete.append(" and Trajet.ligneId = :routeId1");
            requete.append(" and Horaire.arretId = :arretId1");
            requete.append(" and Trajet.macroDirection = :macroDirection1");
            requete.append(" and Horaire.terminus = 0");
            requete.append(" and Horaire.heureDepart >= :maintenantHier ");

            selectionArgs.add(Integer.toString(uneJournee));
            selectionArgs.add(ligneId);
            selectionArgs.add(arretId);
            selectionArgs.add(Integer.toString(macroDirection));
            selectionArgs.add(Integer.toString(now + uneJournee));
        }
        if (!JoursFeries.is1erMai(calendar.getTime())) {
            if (requete.length() > 0) {
                requete.append("UNION ");
            }
            requete.append("select Horaire.heureDepart as _id,");
            requete.append(" Trajet.id as trajetId, stopSequence as sequence ");
            requete.append("from Calendrier,  Horaire_");
            requete.append(ligneId);
            requete.append(" as Horaire, Trajet ");
            requete.append("where ");
            requete.append(clauseWhereForTodayCalendrier(calendar));
            requete.append(" and Trajet.id = Horaire.trajetId");
            requete.append(" and Trajet.calendrierId = Calendrier.id");
            requete.append(" and Trajet.ligneId = :routeId2");
            requete.append(" and Horaire.arretId = :arretId2");
            requete.append(" and Trajet.macroDirection = :macroDirection2");
            requete.append(" and Horaire.terminus = 0");
            requete.append(" and Horaire.heureDepart >= :maintenant");

            selectionArgs.add(ligneId);
            selectionArgs.add(arretId);
            selectionArgs.add(Integer.toString(macroDirection));
            selectionArgs.add(Integer.toString(now));
        }
        requete.append(" order by _id ");
        if (limit != null) {
            requete.append("limit ");
            requete.append(limit);
        }
        LOG_YBO.debug("Requete : " + requete.toString());
        LOG_YBO.debug("SelectionArgs : " + selectionArgs);
        return TransportsRennesApplication.getDataBaseHelper().executeSelectQuery(requete.toString(), selectionArgs);
    }

    private static String clauseWhereForTodayCalendrier(Calendar calendar) {
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
