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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class MoteurCsv {

	private final Map<String, ClassCsv> mapFileClasses = new HashMap<String, ClassCsv>(10);

	private String[] enteteCourante;

	private ClassCsv classCourante;

	public MoteurCsv(Iterable<Class<?>> classes) throws MoteurCsvException {
		for (Class<?> clazz : classes) {
			scannerClass(clazz);
		}
	}

	private Object creerObjet(String ligne) throws MoteurCsvException {
		if (classCourante == null) {
			throw new MoteurCsvException("La m�thode creerObjet a �t�e appel�e sans que la m�thode nouveauFichier n'est �t� appel�e.");
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
					"Erreur � l'instanciation de la class " + classCourante.getClazz().getSimpleName() + " pour la ligne " + ligne, e);
		}
	}

	private void nouveauFichier(String nomFichier, String entete) throws MoteurCsvException {
		classCourante = mapFileClasses.get(nomFichier);
		if (classCourante == null) {
			throw new MoteurCsvException("Le fichier " + nomFichier + " n'as pas de classe associ�e");
		}
		enteteCourante = entete.split(classCourante.getSeparateur());
		if (Character.isIdentifierIgnorable(enteteCourante[0].charAt(0))) {
			enteteCourante[0] = enteteCourante[0].substring(1);
		}
	}

	public <Objet> Iterable<Objet> parseFile(File file, Class<Objet> clazz) throws MoteurCsvException, IOException {
		return parseFile(new FileInputStream(file), clazz);
	}

	@SuppressWarnings({"unchecked"})
	public <Objet> Iterable<Objet> parseFile(InputStream stream, Class<Objet> clazz) throws MoteurCsvException, IOException {
		Collection<Objet> objets = new ArrayList<Objet>(1000);
		BufferedReader bufReader = new BufferedReader(new InputStreamReader(stream), 8 << 10);
		try {
			nouveauFichier(clazz.getAnnotation(FichierCsv.class).value(), bufReader.readLine());
			String ligne = bufReader.readLine();
			while (ligne != null) {
				objets.add((Objet) creerObjet(ligne));
				ligne = bufReader.readLine();
			}
		} finally {
			bufReader.close();
		}
		return objets;
	}

	private void writeEntete(BufferedWriter bufWriter, Iterable<String> nomChamps, ClassCsv classCsv) throws IOException {
		boolean first = true;
		for (String nomChamp : nomChamps) {
			if (!first) {
				bufWriter.write(classCsv.getSeparateur());
			}
			bufWriter.write(nomChamp);
			first = false;
		}
		bufWriter.write('\n');
	}

	private <Objet> void writeLigne(BufferedWriter bufWriter, Iterable<String> nomChamps, ClassCsv classCsv, Objet objet)
			throws IOException, IllegalAccessException {
		boolean first = true;
		for (String nomChamp : nomChamps) {
			if (!first) {
				bufWriter.write(classCsv.getSeparateur());
			}
			ChampCsv champCsv = classCsv.getChampCsv(nomChamp);
			champCsv.getField().setAccessible(true);
			Object valeur = champCsv.getField().get(objet);
			champCsv.getField().setAccessible(false);
			if (valeur != null) {
				bufWriter.write(champCsv.getNewAdapterCsv().toString(valeur));
			}
			first = false;
		}
		bufWriter.write('\n');
	}

	@SuppressWarnings({"TypeMayBeWeakened"})
	public <Objet> void writeFile(File file, Iterable<Objet> objets, Class<Objet> clazz) {
		writeFile(file, objets, clazz, new HashSet<String>(1000));
	}

	private <Objet> void writeFile(File file, Iterable<Objet> objets, AnnotatedElement clazz, Collection<String> champsNoWrites) {
		try {
			BufferedWriter bufWriter = new BufferedWriter(new FileWriter(file));
			try {
				final ClassCsv classCsv = mapFileClasses.get(clazz.getAnnotation(FichierCsv.class).value());
				List<String> nomChamps = new ArrayList<String>(10);
				for (String champ : classCsv.getNomChamps()) {
					if (!champsNoWrites.contains(champ)) {
						nomChamps.add(champ);
					}
				}
				Collections.sort(nomChamps, new Comparator<String>() {
					public int compare(String o1, String o2) {
						int thisVal = classCsv.getOrdre(o1);
						int anotherVal = classCsv.getOrdre(o2);
						return (thisVal<anotherVal ? -1 : (thisVal==anotherVal ? 0 : 1));
					}
				});
				writeEntete(bufWriter, nomChamps, classCsv);
				for (Objet objet : objets) {
					writeLigne(bufWriter, nomChamps, classCsv, objet);
				}
			} finally {
				bufWriter.close();
			}
		} catch (Exception exception) {
			throw new MoteurCsvException(exception);
		}
	}

	private void scannerClass(Class<?> clazz) throws MoteurCsvException {
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
				classCsv.putOrdre(baliseCsv.value(), baliseCsv.ordre());
			}
		}
		mapFileClasses.put(fichierCsv.value(), classCsv);
	}
}
