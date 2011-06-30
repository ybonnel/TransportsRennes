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
package fr.ybo.transportsbordeaux.database;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import fr.ybo.database.DataBaseHelper;
import fr.ybo.transportsbordeaux.modele.Arret;
import fr.ybo.transportsbordeaux.modele.ArretFavori;
import fr.ybo.transportsbordeaux.modele.ArretRoute;
import fr.ybo.transportsbordeaux.modele.Calendrier;
import fr.ybo.transportsbordeaux.modele.CalendrierException;
import fr.ybo.transportsbordeaux.modele.DernierMiseAJour;
import fr.ybo.transportsbordeaux.modele.Direction;
import fr.ybo.transportsbordeaux.modele.Horaire;
import fr.ybo.transportsbordeaux.modele.Ligne;
import fr.ybo.transportsbordeaux.modele.Trajet;
import fr.ybo.transportsbordeaux.modele.VeloFavori;

public class TransportsBordeauxDatabase extends DataBaseHelper {

	private static final String DATABASE_NAME = "transportsbordeaux.db";
	private static final int DATABASE_VERSION = 4;

	@SuppressWarnings("unchecked")
	private static final List<Class<?>> DATABASE_ENTITITES =
		Arrays.asList(
				Arret.class,
				ArretFavori.class,
				ArretRoute.class,
				Calendrier.class,
				CalendrierException.class,
				DernierMiseAJour.class,
				Direction.class,
				Horaire.class,
				Ligne.class,
				Trajet.class,
				VeloFavori.class);

	public TransportsBordeauxDatabase(Context context, List<Class<?>> classes) {
		super(context, DATABASE_ENTITITES, DATABASE_NAME, DATABASE_VERSION);
	}

	private Map<Integer, UpgradeDatabase> mapUpgrades;

	protected Map<Integer, UpgradeDatabase> getUpgrades() {
		if (mapUpgrades == null) {
			mapUpgrades = new HashMap<Integer, UpgradeDatabase>();
			mapUpgrades.put(3, new UpgradeDatabase() {

				@Override
				public void upgrade(SQLiteDatabase db) {
					getBase().dropDataBase(db);
					getBase().createDataBase(db);
				}
			});
			mapUpgrades.put(4, new UpgradeDatabase() {

				@Override
				public void upgrade(SQLiteDatabase db) {

					Cursor cursor = db.query("sqlite_master", Collections.singleton("name").toArray(new String[1]),
							" type = 'table'", null, null, null, null);
					while (cursor.moveToNext()) {
						String tableName = cursor.getString(0);
						if (!"android_metadata".equals(tableName) && !"VeloFavori".equals(tableName)
								&& !"ArretFavori".equals(tableName)) {
							db.execSQL("DROP TABLE " + tableName);
						}
					}
					cursor.close();
					getBase().getTable(Arret.class).createTable(db);
					getBase().getTable(ArretRoute.class).createTable(db);
					getBase().getTable(Calendrier.class).createTable(db);
					getBase().getTable(CalendrierException.class).createTable(db);
					getBase().getTable(DernierMiseAJour.class).createTable(db);
					getBase().getTable(Direction.class).createTable(db);
					getBase().getTable(Ligne.class).createTable(db);
					getBase().getTable(Trajet.class).createTable(db);
				}
			});
		}
		return mapUpgrades;
	}
}
