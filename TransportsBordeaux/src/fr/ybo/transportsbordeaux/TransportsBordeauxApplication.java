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

package fr.ybo.transportsbordeaux;

import java.util.Arrays;

import android.app.Application;
import fr.ybo.transportsbordeaux.database.DataBaseHelper;
import fr.ybo.transportsbordeaux.modele.Arret;
import fr.ybo.transportsbordeaux.modele.ArretFavori;
import fr.ybo.transportsbordeaux.modele.ArretRoute;
import fr.ybo.transportsbordeaux.modele.DernierMiseAJour;
import fr.ybo.transportsbordeaux.modele.Direction;
import fr.ybo.transportsbordeaux.modele.Ligne;
import fr.ybo.transportsbordeaux.modele.VeloFavori;

/**
 * Classe de l'application permettant de stocker les attributs globaux à l'application.
 */
public class TransportsBordeauxApplication extends Application {

	private static DataBaseHelper databaseHelper;

	public static DataBaseHelper getDataBaseHelper() {
		return databaseHelper;
	}


	@SuppressWarnings("unchecked")
	@Override
	public void onCreate() {
		super.onCreate();
		databaseHelper = new DataBaseHelper(this, Arrays.asList(Arret.class, ArretFavori.class, ArretRoute.class,
				DernierMiseAJour.class, Direction.class, Ligne.class, VeloFavori.class));
	}

}
