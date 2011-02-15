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

package fr.ybo.transportsrennes.keolis.gtfs.moteur;

import android.database.sqlite.SQLiteDatabase;
import fr.ybo.transportsrennes.keolis.gtfs.annotation.BaliseCsv;
import fr.ybo.transportsrennes.keolis.gtfs.annotation.FichierCsv;
import fr.ybo.transportsrennes.keolis.gtfs.database.DataBaseHelper;
import fr.ybo.transportsrennes.keolis.gtfs.database.modele.Table;
import fr.ybo.transportsrennes.keolis.gtfs.moteur.modele.ChampCsv;
import fr.ybo.transportsrennes.keolis.gtfs.moteur.modele.ClassCsv;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class MoteurCsv {

	private final Map<String, ClassCsv> mapFileClasses = new HashMap<String, ClassCsv>(5);

	private String[] enteteCourante;

	private ClassCsv classCourante;

	public MoteurCsv(Iterable<Class<?>> classes) {
		for (Class<?> clazz : classes) {
			scannerClass(clazz);
		}
	}

	public Object creerObjet(String ligne) {
		if (classCourante == null) {
			throw new MoteurCsvException("La méthode creerObjet a étée appelée sans que la méthode nouveauFichier n'est été appelée.");
		}
		try {
			Object objetCsv = classCourante.getContructeur().newInstance((Object[]) null);
			String[] champs = ligne.split(classCourante.getSeparateur());
			int champsLength = champs.length;
			for (int numChamp = 0; numChamp < champsLength; numChamp++) {
				if (champs[numChamp] != null && !"".equals(champs[numChamp])) {
					String nomChamp = enteteCourante[numChamp];
					ChampCsv champCsv = classCourante.getChampCsv(nomChamp);
					if (champCsv != null) {
						champCsv.getField().setAccessible(true);
						champCsv.getField().set(objetCsv, champCsv.getNewAdapterCsv().parse(champs[numChamp]));
						champCsv.getField().setAccessible(false);
					}
				}
			}
			return objetCsv;
		} catch (Exception e) {
			throw new MoteurCsvException(
					"Erreur à l'instanciation de la class " + classCourante.getClazz().getSimpleName() + " pour la ligne " + ligne, e);
		}
	}

	public void nouveauFichier(String nomFichier, String entete) {
		classCourante = mapFileClasses.get(nomFichier);
		if (classCourante == null) {
			throw new MoteurCsvException("Le fichier " + nomFichier + " n'as pas de classe associée");
		}
		enteteCourante = entete.split(classCourante.getSeparateur());
		if (Character.isIdentifierIgnorable(enteteCourante[0].charAt(0))) {
			enteteCourante[0] = enteteCourante[0].substring(1);
		}
	}

	public <Objet> void parseFileAndInsert(BufferedReader bufReader, Class<Objet> clazz, DataBaseHelper dataBaseHelper, String suffixeTableName)
			throws IOException {
		nouveauFichier(clazz.getAnnotation(FichierCsv.class).value(), bufReader.readLine());
		Table table = dataBaseHelper.getBase().getTable(clazz);
		table.addSuffixeToTableName(suffixeTableName);
		SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
		table.dropTable(db);
		table.createTable(db);
		String ligne = bufReader.readLine();
		while (ligne != null) {
			table.insert(db, creerObjet(ligne));
			ligne = bufReader.readLine();
		}
	}

	private void scannerClass(Class<?> clazz) {
		FichierCsv fichierCsv = clazz.getAnnotation(FichierCsv.class);
		if (fichierCsv == null) {
			throw new MoteurCsvException("Annotation FichierCsv non présente sur la classe " + clazz.getSimpleName());
		}
		if (mapFileClasses.get(fichierCsv.value()) != null) {
			return;
		}
		ClassCsv classCsv = new ClassCsv(fichierCsv.separateur(), clazz);
		for (Field field : clazz.getDeclaredFields()) {
			BaliseCsv baliseCsv = field.getAnnotation(BaliseCsv.class);
			if (baliseCsv != null) {
				classCsv.setChampCsv(baliseCsv.value(), new ChampCsv(baliseCsv.adapter(), field));
			}
		}
		mapFileClasses.put(fichierCsv.value(), classCsv);
	}
}
