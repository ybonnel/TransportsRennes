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

package fr.ybo.gtfs.csv.moteur.modele;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import fr.ybo.gtfs.csv.moteur.ErreurMoteurCsv;

public class ClassCsv {
	private final String separateur;
	private final Class<?> clazz;
	private Constructor<?> contructeur;

	private final Map<String, ChampCsv> mapOfFields = new HashMap<String, ChampCsv>();

	public ClassCsv(final String separateur, final Class<?> clazz) {
		this.separateur = separateur;
		this.clazz = clazz;
		try {
			contructeur = clazz.getDeclaredConstructor((Class<?>[]) null);
		} catch (Exception e) {
			throw new ErreurMoteurCsv("Erreur a la récupération du constructeur de " + clazz.getSimpleName(), e);
		}
	}

	public ChampCsv getChampCsv(final String nomCsv) {
		return mapOfFields.get(nomCsv);
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public Constructor<?> getContructeur() {
		return contructeur;
	}

	public String getSeparateur() {
		return separateur;
	}

	public void setChampCsv(final String nomCsv, final ChampCsv champCsv) {
		mapOfFields.put(nomCsv, champCsv);
	}

}
