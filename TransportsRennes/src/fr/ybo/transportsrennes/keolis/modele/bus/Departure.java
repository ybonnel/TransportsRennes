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
public class Departure implements Serializable {


    private boolean accurate;
    private Calendar time;

    public boolean isAccurate() {
        return accurate;
    }

    public void setAccurate(final boolean accurate) {
        this.accurate = accurate;
    }

    public Calendar getTime() {
        return time;
    }

    public void setTime(final Calendar time) {
        this.time = time;
    }

    private int horaire = -1;

    public int getHoraire() {
        if (horaire == -1) {
            horaire = time.get(Calendar.HOUR_OF_DAY) * 60 + time.get(Calendar.MINUTE);
        }
        return horaire;
    }


    @Override
    public String toString() {
        return "Departure{" +
                "accurate=" + accurate +
                ", time=" + time +
                ", horaire=" + horaire +
                '}';
    }
}
