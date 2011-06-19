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
package fr.ybo.transportsrennes.keolis.modele;

/**
 * Paramètre d'une URL.
 *
 * @author ybonnel
 */
public class ParametreUrl {

	/**
	 * name.
	 */
	private final String name;
	/**
	 * value.
	 */
	private final String value;

	/**
	 * @param pName  name.
	 * @param pValue value.
	 */
	public ParametreUrl(String pName, String pValue) {
		name = pName;
		value = pValue;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
}
