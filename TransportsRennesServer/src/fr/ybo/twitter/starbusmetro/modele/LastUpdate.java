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
import java.util.HashMap;
import java.util.Map;

public class LastUpdate {

	private static Map<String, LastUpdate> instances = new HashMap<String, LastUpdate>();

	public static synchronized LastUpdate getInstance(String compte) {
		if (!instances.containsKey(compte)) {
			instances.put(compte, new LastUpdate());
		}
		return instances.get(compte);
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
