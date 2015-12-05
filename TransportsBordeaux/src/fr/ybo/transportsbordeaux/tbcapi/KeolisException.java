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
package fr.ybo.transportsbordeaux.tbcapi;

/**
 * Exception sur les traitements associés aux API Keolis.
 *
 * @author ybonnel
 */
class KeolisException extends RuntimeException {

    /**
     * Serial.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructeur avec message.
     *
     * @param message message.
     */
    KeolisException(final String message) {
        super(message);
    }

    /**
     * Constructeur avec message et exception.
     *
     * @param cause   exception.
     */
    KeolisException(final Throwable cause) {
        super("Erreur lors de l'appel à l'API keolis", cause);
    }

}
