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
 * 
 * Contributors:
 *     ybonnel - initial API and implementation
 */
package fr.ybo.transportsrennes.keolis.xml.sax;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import fr.ybo.transportsrennes.keolis.modele.bus.DepartureMetro;

public class GetDeparturesMetroHandler extends KeolisHandler<DepartureMetro> {

	private static final String STATION = "station";

    private static final String NEXT_TRAIN_1 = "nextTrain1Platform";

    private static final String NEXT_TRAIN_2 = "nextTrain2Platform";

	private static final DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");

    private final int plateform;

    public GetDeparturesMetroHandler(final int plateform) {
        this.plateform = plateform;
    }

	@Override
	protected String getBaliseData() {
		return STATION;
	}

	@Override
	protected DepartureMetro getNewObjetKeolis() {
		return new DepartureMetro();
	}


	@Override
	protected void remplirObjectKeolis(final DepartureMetro currentObjectKeolis, final String baliseName, final String contenuOfBalise) {
		if (baliseName.equals(NEXT_TRAIN_1 + plateform)) {
			try {
				final Calendar calendar = Calendar.getInstance();
				calendar.setTime(dfm.parse(contenuOfBalise));
				currentObjectKeolis.setTime1(calendar);
			} catch (final ParseException e) {
			}
		} else if (baliseName.equals(NEXT_TRAIN_2 + plateform)) {
            try {
				final Calendar calendar = Calendar.getInstance();
				calendar.setTime(dfm.parse(contenuOfBalise));
                currentObjectKeolis.setTime2(calendar);
            } catch (final ParseException e) {
            }
        }
	}
}
