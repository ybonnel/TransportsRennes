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

package com.google.code.geocoder.util;

/**
 * Classe utilitaire pour les chaines de caractères.
 * @author ybonnel
 *
 */
public class StringUtils {

	/**
	 * Constructeur privé pour empécher l'instanciation de la classe.
	 */
	private StringUtils() {
	}

	/**
	 * Permet de vérifier qu'une chaine n'est pas vide.
	 * @param string chaine.
	 * @return false si la chaine est null ou vide (longueur 0), true sinon.
	 */
	public static boolean isNotBlank(CharSequence string) {
		return string != null && string.length() > 0;
	}

}
