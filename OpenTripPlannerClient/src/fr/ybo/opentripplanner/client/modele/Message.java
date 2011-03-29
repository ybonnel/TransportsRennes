/* This program is free software: you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public License
 as published by the Free Software Foundation, either version 3 of
 the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>. */
package fr.ybo.opentripplanner.client.modele;

/**
 * The purpose of Messages is to read supply Message.properties to underlying
 * calling code... The ENUM's enumerated values should be named to reflect the
 * property names inside of Message.properties
 */
public enum Message {
	// id field is loosely based on HTTP error codes
	// http://en.wikipedia.org/wiki/List_of_HTTP_status_codes
	PLAN_OK(200), SYSTEM_ERROR(500),

	OUTSIDE_BOUNDS(400), PATH_NOT_FOUND(404), NO_TRANSIT_TIMES(406), REQUEST_TIMEOUT(408), BOGUS_PARAMETER(413), GEOCODE_FROM_NOT_FOUND(
			440), GEOCODE_TO_NOT_FOUND(450), GEOCODE_FROM_TO_NOT_FOUND(460), TOO_CLOSE(409), LOCATION_NOT_ACCESSIBLE(
			470),

	GEOCODE_FROM_AMBIGUOUS(340), GEOCODE_TO_AMBIGUOUS(350), GEOCODE_FROM_TO_AMBIGUOUS(360), ;

	private final int m_id;

	/** enum constructors are private -- see values above */
	private Message(int id) {
		m_id = id;
	}

	public int getId() {
		return m_id;
	}

	public static Message findEnumById(int id) {
		for (Message message : Message.values()) {
			if (message.m_id == id) {
				return message;
			}
		}

		return null;
	}
}
