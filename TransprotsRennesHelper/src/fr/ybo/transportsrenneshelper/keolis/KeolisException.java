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
package fr.ybo.transportsrenneshelper.keolis;

/**
 * Exception sur les traitements associés aux API Keolis.
 *
 * @author ybonnel
 */
public class KeolisException extends RuntimeException {

	/**
	 * Serial.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructeur avec message.
	 *
	 * @param message message.
	 */
	public KeolisException(String message) {
		super(message);
	}

	/**
	 * Constructeur avec message et exception.
	 *
	 * @param message message.
	 * @param cause   exception.
	 */
	public KeolisException(String message, Throwable cause) {
		super(message, cause);
	}

}
