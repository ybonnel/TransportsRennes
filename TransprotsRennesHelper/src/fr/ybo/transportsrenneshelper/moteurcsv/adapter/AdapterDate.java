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

package fr.ybo.transportsrenneshelper.moteurcsv.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AdapterDate implements AdapterCsv<Date> {

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

	public Date parse(final String chaine) {
		try {
			return sdf.parse(chaine);
		} catch (final ParseException e) {
			return null;
		}
	}

	public String toString(Date date) {
		return sdf.format(date);
	}

}
