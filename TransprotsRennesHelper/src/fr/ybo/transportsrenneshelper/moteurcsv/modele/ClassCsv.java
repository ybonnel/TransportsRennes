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

package fr.ybo.transportsrenneshelper.moteurcsv.modele;

import fr.ybo.transportsrenneshelper.moteurcsv.MoteurCsvException;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class ClassCsv {
	private final String separateur;
	private final Class<?> clazz;
	private final Constructor<?> contructeur;

	private final Map<String, ChampCsv> mapOfFields = new HashMap<String, ChampCsv>(10);

	private final Map<String, Integer> ordres = new HashMap<String, Integer>();

	public ClassCsv(String separateur, Class<?> clazz) throws MoteurCsvException {
		this.separateur = separateur;
		this.clazz = clazz;
		try {
			contructeur = clazz.getDeclaredConstructor((Class<?>[]) null);
		} catch (SecurityException e) {
			throw new MoteurCsvException("Erreur a la r�cup�ration du constructeur de " + clazz.getSimpleName(), e);
		} catch (NoSuchMethodException e) {
			throw new MoteurCsvException("Erreur a la r�cup�ration du constructeur de " + clazz.getSimpleName(), e);
		}
	}

	public ChampCsv getChampCsv(String nomCsv) {
		return mapOfFields.get(nomCsv);
	}

	public Iterable<String> getNomChamps() {
		return mapOfFields.keySet();
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

	public void setChampCsv(String nomCsv, ChampCsv champCsv) {
		mapOfFields.put(nomCsv, champCsv);
	}

	public void putOrdre(String nomCsv, int ordre) {
		ordres.put(nomCsv, ordre);
	}

	public int getOrdre(String nomCsv) {
		return ordres.get(nomCsv);
	}

}
