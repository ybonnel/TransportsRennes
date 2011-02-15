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

package fr.ybo.itineraires.util;

import java.util.logging.Logger;

public class Chrono {
	private static final Logger LOGGER = Logger.getLogger(Chrono.class.getName());
	private final long startTime;
	private final String methode;
	private long elapsedTime;

	public Chrono(String methode) {
		startTime = System.nanoTime();
		this.methode = methode;
	}

	public Chrono stop() {
		elapsedTime = System.nanoTime() - startTime;
		return this;
	}

	public void spool() {
		LOGGER.info(new StringBuilder(methode).append(" : ").append(elapsedTime / 1000000).append(" ms").toString());
	}
}
