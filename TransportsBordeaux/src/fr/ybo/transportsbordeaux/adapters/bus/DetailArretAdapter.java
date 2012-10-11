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
package fr.ybo.transportsbordeaux.adapters.bus;

import java.util.List;

import android.content.Context;
import fr.ybo.transportsbordeaux.R;
import fr.ybo.transportscommun.adapters.bus.AbstractDetailArretAdapter;
import fr.ybo.transportscommun.donnees.modele.DetailArretConteneur;

public class DetailArretAdapter extends AbstractDetailArretAdapter {

	public DetailArretAdapter(Context context, List<DetailArretConteneur> prochainsDeparts, int now, boolean isToday,
			String currentDirection) {
		super(context, prochainsDeparts, now, isToday, currentDirection, 0);
	}

	@Override
	protected int getLayout() {
		return R.layout.detailarretliste;
	}

}
