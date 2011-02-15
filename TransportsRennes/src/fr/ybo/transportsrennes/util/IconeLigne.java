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


import fr.ybo.transportsrennes.R;

public class IconeLigne {

	private IconeLigne() {
	}

	public static int getIconeResource(String nomCourt) {
		try {
			return R.drawable.class.getDeclaredField('i' + nomCourt.toLowerCase()).getInt(null);
		} catch (Exception ignore) {
			return -1;
		}
	}


	public static int getMarkeeResource(String nomCourt) {
		try {
			return R.drawable.class.getDeclaredField('m' + nomCourt.toLowerCase()).getInt(null);
		} catch (Exception ignore) {
			return -1;
		}
	}
}
