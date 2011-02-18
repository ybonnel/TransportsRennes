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

package fr.ybo.transportsrennes;

import android.os.Bundle;
import fr.ybo.itineraires.schema.ItineraireReponse;
import fr.ybo.itineraires.schema.Trajet;
import fr.ybo.transportsrennes.activity.MenuAccueil;

public class Itineraires extends MenuAccueil.ListActivity {

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.itineraires);
		ItineraireReponse itineraireReponse = (ItineraireReponse) getIntent().getSerializableExtra("itineraireReponse");
		Trajet trajet = itineraireReponse.getTrajets().iterator().next();

	}
}
