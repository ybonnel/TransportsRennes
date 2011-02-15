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

package fr.ybo.twitter.starbusmetro.modele;

import java.util.Date;

public class LastUpdate {

	@SuppressWarnings({"StaticNonFinalField"})
	private static LastUpdate instance;

	public static synchronized LastUpdate getInstance() {
		if (instance == null) {
			instance = new LastUpdate();
		}
		return instance;
	}

	private LastUpdate() {
	}

	// 5 minutes.
	private static final long ECART_UPDATE = 300000;

	private Date lastUpdate;

	public synchronized boolean isUpdate() {
		Date dateCourante = new Date();
		if (lastUpdate == null) {
			lastUpdate = dateCourante;
			return false;
		} else {
			return !(dateCourante.getTime() - lastUpdate.getTime() > ECART_UPDATE);
		}
	}

}
