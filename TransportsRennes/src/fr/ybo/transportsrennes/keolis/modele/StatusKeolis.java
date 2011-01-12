/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package fr.ybo.transportsrennes.keolis.modele;

/**
 * Représente le status de retour des apis keolis.
 *
 * @author ybonnel
 */
public class StatusKeolis {

	/**
	 * Le code du status (0 pour ok).
	 */
	private String code;

	/**
	 * Message associé au status.
	 */
	private String message;

	/**
	 * @return the code
	 */
	public final String getCode() {
		return code;
	}

	/**
	 * @return the message
	 */
	public final String getMessage() {
		return message;
	}

	/**
	 * @param pCode the code to set
	 */
	public final void setCode(final String pCode) {
		code = pCode;
	}

	/**
	 * @param pMessage the message to set
	 */
	public final void setMessage(final String pMessage) {
		message = pMessage;
	}
}
