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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fr.ybo.transportsrennes.keolis.KeolisException;
import fr.ybo.transportsrennes.keolis.modele.bus.Departure;

public class GetDeparturesHandler extends KeolisHandler<Departure> {

	private static final String DEPARTURE = "departure";

	private static final String DATA = "data";

	private static final String LOCAL_DATETIME = "localdatetime";

	private static final DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");

	private Calendar dateApi;

	public Calendar getDateApi() {
		return dateApi;
	}

	@Override
	protected String getBaliseData() {
		return DEPARTURE;
	}

	@Override
	protected Departure getNewObjetKeolis() {
		return new Departure();
	}


	@Override
	protected void remplirObjectKeolis(Departure currentObjectKeolis, String baliseName, String contenuOfBalise) {
		if (baliseName.equals(DEPARTURE)) {
			Calendar calendar = Calendar.getInstance();
			try {
				calendar.setTime(dfm.parse(contenuOfBalise));
				currentObjectKeolis.setTime(calendar);
			} catch (ParseException e) {
				throw new KeolisException("Erreur lors du parse de " + contenuOfBalise, e);
			}
		}
	}



	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.ybo.transportsrennes.keolis.xml.sax.KeolisHandler#startElement(java
	 * .lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		if (DEPARTURE.equals(localName)) {
			currentObjetKeolis.setAccurate("1".equals(attributes.getValue("accurate")));
			currentObjetKeolis.setHeadSign(attributes.getValue("headsign"));
		}
		if (DATA.equals(localName)) {
			dateApi = Calendar.getInstance();
			try {
				dateApi.setTime(dfm.parse(attributes.getValue(LOCAL_DATETIME)));
			} catch (ParseException e) {
				throw new KeolisException("Erreur lors du parse de " + attributes.getValue(LOCAL_DATETIME), e);
			}
		}
	}
}
