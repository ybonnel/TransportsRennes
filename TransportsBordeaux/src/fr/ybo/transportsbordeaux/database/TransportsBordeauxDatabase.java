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

package fr.ybo.transportsbordeaux.database;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import fr.ybo.database.DataBaseHelper;
import fr.ybo.transportsbordeaux.modele.Arret;
import fr.ybo.transportsbordeaux.modele.ArretFavori;
import fr.ybo.transportsbordeaux.modele.ArretRoute;
import fr.ybo.transportsbordeaux.modele.DernierMiseAJour;
import fr.ybo.transportsbordeaux.modele.Direction;
import fr.ybo.transportsbordeaux.modele.Ligne;
import fr.ybo.transportsbordeaux.modele.VeloFavori;

public class TransportsBordeauxDatabase extends DataBaseHelper {

	private static final String DATABASE_NAME = "transportsbordeaux.db";
	private static final int DATABASE_VERSION = 2;

	@SuppressWarnings("unchecked")
	private static final List<Class<?>> DATABASE_ENTITITES = Arrays.asList(Arret.class, ArretFavori.class,
			ArretRoute.class, DernierMiseAJour.class, Direction.class, Ligne.class, VeloFavori.class);

	public TransportsBordeauxDatabase(Context context, List<Class<?>> classes) {
		super(context, DATABASE_ENTITITES, DATABASE_NAME, DATABASE_VERSION);
	}

	private Map<Integer, UpgradeDatabase> mapUpgrades;

	protected Map<Integer, UpgradeDatabase> getUpgrades() {
		if (mapUpgrades == null) {
			mapUpgrades = new HashMap<Integer, UpgradeDatabase>();
			mapUpgrades.put(2, new UpgradeDatabase() {

				@Override
				public void upgrade(SQLiteDatabase db) {
					getBase().getTable(Arret.class).dropTable(db);
					getBase().getTable(ArretRoute.class).dropTable(db);
					getBase().getTable(DernierMiseAJour.class).dropTable(db);
					getBase().getTable(Direction.class).dropTable(db);
					getBase().getTable(Ligne.class).dropTable(db);
					getBase().getTable(Arret.class).createTable(db);
					getBase().getTable(ArretRoute.class).createTable(db);
					getBase().getTable(DernierMiseAJour.class).createTable(db);
					getBase().getTable(Direction.class).createTable(db);
					getBase().getTable(Ligne.class).createTable(db);
				}
			});
		}
		return mapUpgrades;
	}
}
