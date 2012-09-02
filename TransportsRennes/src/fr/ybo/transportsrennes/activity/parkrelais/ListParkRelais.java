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
package fr.ybo.transportsrennes.activity.parkrelais;

import java.util.List;

import fr.ybo.transportscommun.activity.parkings.AbstractListParkings;
import fr.ybo.transportscommun.util.ErreurReseau;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.keolis.Keolis;
import fr.ybo.transportsrennes.keolis.modele.bus.ParkRelai;

/**
 * Activité de type liste permettant de lister les parcs relais par distances de
 * la position actuelle.
 *
 * @author ybonnel
 */
public class ListParkRelais extends AbstractListParkings<ParkRelai> {

	private final Keolis keolis = Keolis.getInstance();

	@Override
	protected int getDialogueRequete() {
		return R.string.dialogRequeteParkRelais;
	}

	@Override
	protected int getLayout() {
		return R.layout.listparkrelais;
	}

	@Override
	protected List<ParkRelai> getParkings() throws ErreurReseau {
		return keolis.getParkRelais();
	}

	@Override
	protected void setupActionBar() {
		getActivityHelper().setupActionBar(R.menu.listparkings_menu_items, R.menu.holo_listparkings_menu_items);
	}
}
