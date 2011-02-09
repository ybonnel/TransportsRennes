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

package fr.ybo.gtfs.csv.moteur;

import fr.ybo.gtfs.csv.annotation.BaliseCsv;
import fr.ybo.gtfs.csv.annotation.FichierCsv;
import fr.ybo.gtfs.csv.moteur.modele.ChampCsv;
import fr.ybo.gtfs.csv.moteur.modele.ClassCsv;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoteurCsv {

	private final Map<String, ClassCsv> mapFileClasses = new HashMap<String, ClassCsv>();

	private String[] enteteCourante;

	private ClassCsv classCourante;

	public MoteurCsv(final List<Class<?>> classes) {
		for (final Class<?> clazz : classes) {
			scannerClass(clazz);
		}
	}

	public Object creerObjet(final String ligne) {
		if (classCourante == null) {
			throw new ErreurMoteurCsv("La méthode creerObjet a étée appelée sans que la méthode nouveauFichier n'est été appelée.");
		}
		try {
			final Object objetCsv = classCourante.getContructeur().newInstance((Object[]) null);
			String nomChamp;
			ChampCsv champCsv;
			final String[] champs = ligne.split(classCourante.getSeparateur());
			for (int numChamp = 0; numChamp < champs.length; numChamp++) {
				if (champs[numChamp] != null && !"".equals(champs[numChamp])) {
					nomChamp = enteteCourante[numChamp];
					champCsv = classCourante.getChampCsv(nomChamp);
					if (champCsv != null) {
						//						champCsv.getField().setAccessible(true);
						champCsv.getField().set(objetCsv, champCsv.getNewAdapterCsv().parse(champs[numChamp]));
						//						champCsv.getField().setAccessible(false);
					}
				}
			}
			return objetCsv;
		} catch (final Exception e) {
			throw new ErreurMoteurCsv("Erreur à l'instanciation de la class " + classCourante.getClazz().getSimpleName() + " pour la ligne " + ligne,
					e);
		}
	}

	public Class<?> nouveauFichier(final String nomFichier, final String entete) {
		classCourante = mapFileClasses.get(nomFichier);
		if (classCourante == null) {
			throw new ErreurMoteurCsv("Le fichier " + nomFichier + " n'as pas de classe associée");
		}
		enteteCourante = entete.split(classCourante.getSeparateur());
		if (Character.isIdentifierIgnorable(enteteCourante[0].charAt(0))) {
			enteteCourante[0] = enteteCourante[0].substring(1);
		}
		return classCourante.getClazz();
	}

	@SuppressWarnings("unchecked")
	public <Objet> List<Objet> parseFile(BufferedReader bufReader, Class<Objet> clazz) throws IOException {
		nouveauFichier(clazz.getAnnotation(FichierCsv.class).value(), bufReader.readLine());
		String ligne;
		List<Objet> objets = new ArrayList<Objet>();
		while ((ligne = bufReader.readLine()) != null) {
			objets.add((Objet) creerObjet(ligne));
		}
		return objets;
	}

	private void scannerClass(final Class<?> clazz) {
		final FichierCsv fichierCsv = clazz.getAnnotation(FichierCsv.class);
		if (fichierCsv == null) {
			throw new ErreurMoteurCsv("Annotation FichierCsv non présente sur la classe " + clazz.getSimpleName());
		}
		if (mapFileClasses.get(fichierCsv.value()) != null) {
			return;
		}
		final ClassCsv classCsv = new ClassCsv(fichierCsv.separateur(), clazz);
		BaliseCsv baliseCsv;
		for (final Field field : clazz.getDeclaredFields()) {
			baliseCsv = field.getAnnotation(BaliseCsv.class);
			if (baliseCsv != null) {
				classCsv.setChampCsv(baliseCsv.value(), new ChampCsv(baliseCsv.adapter(), field));
			}
		}
		mapFileClasses.put(fichierCsv.value(), classCsv);
	}
}
