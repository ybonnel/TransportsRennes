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
package fr.ybo.transportsbordeaux.activity.parkrelais;

import java.util.List;

import android.os.Bundle;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

import fr.ybo.transportsbordeaux.R;
import fr.ybo.transportsbordeaux.database.modele.Parking;
import fr.ybo.transportsbordeaux.tbcapi.Keolis;
import fr.ybo.transportscommun.activity.parkings.AbstractListParkings;
import fr.ybo.transportscommun.util.ErreurReseau;

/**
 * Activité de type liste permettant de lister les parcs relais par distances de
 * la position actuelle.
 *
 * @author ybonnel
 */
public class ListParkings extends AbstractListParkings<Parking> {

	@Override
	protected int getDialogueRequete() {
		return R.string.dialogRequeteParkRelais;
	}

	@Override
	protected int getLayout() {
		return R.layout.listparkrelais;
	}

	@Override
	protected List<Parking> getParkings() throws ErreurReseau {
		return Keolis.getInstance().getParkings();
	}

	@Override
	protected void setupActionBar() {
		getActivityHelper().setupActionBar(R.menu.listparkings_menu_items, R.menu.holo_listparkings_menu_items);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Look up the AdView as a resource and load a request.
		((AdView) this.findViewById(R.id.adView)).loadAd(new AdRequest());
	}
}
