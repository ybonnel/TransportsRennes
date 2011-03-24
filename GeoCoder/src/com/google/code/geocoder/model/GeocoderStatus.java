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

package com.google.code.geocoder.model;

/**
 * Status retour.
 * 
 * @author ybonnel
 * 
 */
public enum GeocoderStatus {

	/**
	 * Erreur.
	 */
	ERROR,
	/**
	 * Indique généralement que l'adresse n'a pas été fournie.
	 */
	INVALID_REQUEST,
	/**
	 * Ok.
	 */
	OK,
	/**
	 * Quota dépassé.
	 */
	OVER_QUERY_LIMIT,
	/**
	 * Refusée, seule google sait pourquoi.
	 */
	REQUEST_DENIED,
	/**
	 * Erreur inconnue.
	 */
	UNKNOWN_ERROR,
	/**
	 * Aucun résultat.
	 */
	ZERO_RESULTS;

}