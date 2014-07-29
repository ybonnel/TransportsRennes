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
package fr.ybo.transportsrennes.keolis.modele.bus;

import java.io.Serializable;
import java.util.Calendar;

/**
 * @author ybonnel
 */
@SuppressWarnings("serial")
public class DepartureMetro implements Serializable {

	private Calendar time1;
    private Calendar time2;

    public void setTime1(Calendar time1) {
        this.time1 = time1;
    }

    public void setTime2(Calendar time2) {
        this.time2 = time2;
    }

    public Calendar getTime1() {
        return time1;
    }

    public Calendar getTime2() {
        return time2;
    }
}
