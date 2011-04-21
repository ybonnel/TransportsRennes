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

package fr.ybo.transportsrennes.util;

import java.util.Calendar;
import java.util.List;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.widget.RemoteViews;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.TransportsWidget;
import fr.ybo.transportsrennes.keolis.gtfs.modele.ArretFavori;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Horaire;

public class Widget11UpdateUtil {

	private Widget11UpdateUtil() {
	}

	public static void updateAppWidget(Context context, RemoteViews views, ArretFavori favori) {
		views.setTextViewText(R.id.nomArret, favori.nomArret);
		views.setTextViewText(R.id.directionArret, "-> " + favori.direction);
		views.setImageViewResource(R.id.iconeLigne, IconeLigne.getIconeResource(favori.nomCourt));
		Intent intent = new Intent(context, TransportsWidget.class);
		intent.setAction("YboClick_" + favori.arretId + '_' + favori.ligneId);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		views.setOnClickPendingIntent(R.id.widgetlayout, pendingIntent);

		Calendar calendar = Calendar.getInstance();
		int now = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
		try {
			List<Integer> prochainsDeparts = Horaire.getProchainHorairesAsList(favori.ligneId, favori.arretId,
					favori.macroDirection, 2, calendar);
			views.setTextViewText(
					R.id.tempsRestant,
					prochainsDeparts.size() < 1 ? "" : formatterCalendar(context, prochainsDeparts.get(0), now));
			views.setTextViewText(
					R.id.tempsRestantFutur,
					prochainsDeparts.size() < 2 ? "" : formatterCalendar(context, prochainsDeparts.get(1), now));
		} catch (SQLiteException ignore) {

		}
	}

	public static String formatterCalendar(Context context, int prochainDepart, int now) {
		StringBuilder stringBuilder = new StringBuilder();

		int heures = prochainDepart / 60;
		int minutes = prochainDepart - heures * 60;
		if (heures >= 24) {
			heures -= 24;
		}
		if (heures < 10) {
			stringBuilder.append('0');
		}
		stringBuilder.append(heures);
		stringBuilder.append(':');
		if (minutes < 10) {
			stringBuilder.append('0');
		}
		stringBuilder.append(minutes);

		return stringBuilder.toString();
	}
}
