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

package fr.ybo.transportsrenneshelper.moteurcsv;

import fr.ybo.transportsrenneshelper.annotation.BaliseCsv;
import fr.ybo.transportsrenneshelper.annotation.FichierCsv;
import fr.ybo.transportsrenneshelper.moteurcsv.modele.ChampCsv;
import fr.ybo.transportsrenneshelper.moteurcsv.modele.ClassCsv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class MoteurCsv {

	private final Map<String, ClassCsv> mapFileClasses = new HashMap<String, ClassCsv>(10);

	private String[] enteteCourante;

	private ClassCsv classCourante;

	public MoteurCsv(final Iterable<Class<?>> classes) throws MoteurCsvException {
		super();
		for (final Class<?> clazz : classes) {
			scannerClass(clazz);
		}
	}

	Object creerObjet(final String ligne) throws MoteurCsvException {
		if (classCourante == null) {
			throw new MoteurCsvException(
					"La m�thode creerObjet a �t�e appel�e sans que la m�thode nouveauFichier n'est �t� appel�e.");
		}
		try {
			final Object objetCsv = classCourante.getContructeur().newInstance((Object[]) null);
			final String[] champs = ligne.split(classCourante.getSeparateur());
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
		} catch (final Exception e) {
			throw new MoteurCsvException("Erreur � l'instanciation de la class " + classCourante.getClazz().getSimpleName()
					+ " pour la ligne " + ligne, e);
		}
	}

	void nouveauFichier(final String nomFichier, final String entete) throws MoteurCsvException {
		classCourante = mapFileClasses.get(nomFichier);
		if (classCourante == null) {
			throw new MoteurCsvException("Le fichier " + nomFichier + " n'as pas de classe associ�e");
		}
		enteteCourante = entete.split(classCourante.getSeparateur());
		if (Character.isIdentifierIgnorable(enteteCourante[0].charAt(0))) {
			enteteCourante[0] = enteteCourante[0].substring(1);
		}
	}

	@SuppressWarnings({"unchecked", "TypeMayBeWeakened"})
	public <Objet> Iterable<Objet> parseFile(final File file, final Class<Objet> clazz) throws MoteurCsvException, IOException {
		final Collection<Objet> objets = new ArrayList<Objet>(1000);
		final BufferedReader bufReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)), 8 << 10);
		nouveauFichier(clazz.getAnnotation(FichierCsv.class).value(), bufReader.readLine());
		String ligne = bufReader.readLine();
		while (ligne != null) {
			objets.add((Objet) creerObjet(ligne));
			ligne = bufReader.readLine();
		}
		return objets;
	}

	void writeEntete(final BufferedWriter bufWriter, final Iterable<String> nomChamps, final ClassCsv classCsv) throws IOException {
		boolean first = true;
		for (final String nomChamp : nomChamps) {
			if (!first) {
				bufWriter.write(classCsv.getSeparateur());
			}
			bufWriter.write(nomChamp);
			first = false;
		}
		bufWriter.write('\n');
	}

	<Objet> void writeLigne(final BufferedWriter bufWriter, final Iterable<String> nomChamps, final ClassCsv classCsv, final Objet objet) throws IOException, IllegalAccessException {
		boolean first = true;
		for (final String nomChamp : nomChamps) {
			if (!first) {
				bufWriter.write(classCsv.getSeparateur());
			}
			final ChampCsv champCsv = classCsv.getChampCsv(nomChamp);
			champCsv.getField().setAccessible(true);
			final Object valeur = champCsv.getField().get(objet);
			champCsv.getField().setAccessible(false);
			if (valeur != null) {
				bufWriter.write(champCsv.getNewAdapterCsv().toString(valeur));
			}
			first = false;
		}
		bufWriter.write('\n');
	}

	@SuppressWarnings({"TypeMayBeWeakened"})
	public <Objet> void writeFile(final File file, final Iterable<Objet> objets, final Class<Objet> clazz)  {
		writeFile(file, objets, clazz, new HashSet<String>(1000));
	}

	<Objet> void writeFile(final File file, final Iterable<Objet> objets, final AnnotatedElement clazz, final Collection<String> champsNoWrites) {
		try {
			final BufferedWriter bufWriter = new BufferedWriter(new FileWriter(file));
			final ClassCsv classCsv = mapFileClasses.get(clazz.getAnnotation(FichierCsv.class).value());
			final Collection<String> nomChamps = new ArrayList<String>(10);
			for (final String champ : classCsv.getNomChamps()) {
				if (!champsNoWrites.contains(champ)) {
					nomChamps.add(champ);
				}
			}
			writeEntete(bufWriter, nomChamps, classCsv);
			for (final Objet objet : objets) {
				writeLigne(bufWriter, nomChamps, classCsv, objet);
			}
			bufWriter.close();
		} catch (Exception exception) {
			throw new MoteurCsvException(exception);
		}
	}

	private void scannerClass(final Class<?> clazz) throws MoteurCsvException {
		final FichierCsv fichierCsv = clazz.getAnnotation(FichierCsv.class);
		if (fichierCsv == null) {
			throw new MoteurCsvException("Annotation FichierCsv non présente sur la classe " + clazz.getSimpleName());
		}
		if (mapFileClasses.get(fichierCsv.value()) != null) {
			return;
		}
		final ClassCsv classCsv = new ClassCsv(fichierCsv.separateur(), clazz);
		for (final Field field : clazz.getDeclaredFields()) {
			BaliseCsv baliseCsv = field.getAnnotation(BaliseCsv.class);
			if (baliseCsv != null) {
				classCsv.setChampCsv(baliseCsv.value(), new ChampCsv(baliseCsv.adapter(), field));
			}
		}
		mapFileClasses.put(fichierCsv.value(), classCsv);
	}
}
