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

package fr.ybo.transportsrenneshelper.moteurcsv;

import fr.ybo.transportsrenneshelper.annotation.BaliseCsv;
import fr.ybo.transportsrenneshelper.annotation.FichierCsv;
import fr.ybo.transportsrenneshelper.moteurcsv.modele.ChampCsv;
import fr.ybo.transportsrenneshelper.moteurcsv.modele.ClassCsv;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

public class MoteurCsv {

	private final Map<String, ClassCsv> mapFileClasses = new HashMap<String, ClassCsv>();

	private String[] enteteCourante;

	private ClassCsv classCourante;

	public MoteurCsv(final List<Class<?>> classes) throws ErreurMoteurCsv {
		for (final Class<?> clazz : classes) {
			scannerClass(clazz);
		}
	}

	public Object creerObjet(final String ligne) throws ErreurMoteurCsv {
		if (classCourante == null) {
			throw new ErreurMoteurCsv(
					"La m�thode creerObjet a �t�e appel�e sans que la m�thode nouveauFichier n'est �t� appel�e.");
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
						champCsv.getField().setAccessible(true);
						champCsv.getField().set(objetCsv, champCsv.getNewAdapterCsv().parse(champs[numChamp]));
						champCsv.getField().setAccessible(false);
					}
				}
			}
			return objetCsv;
		} catch (final Exception e) {
			throw new ErreurMoteurCsv("Erreur � l'instanciation de la class " + classCourante.getClazz().getSimpleName()
					+ " pour la ligne " + ligne, e);
		}
	}

	public void nouveauFichier(final String nomFichier, final String entete) throws ErreurMoteurCsv {
		classCourante = mapFileClasses.get(nomFichier);
		if (classCourante == null) {
			throw new ErreurMoteurCsv("Le fichier " + nomFichier + " n'as pas de classe associ�e");
		}
		enteteCourante = entete.split(classCourante.getSeparateur());
		if (Character.isIdentifierIgnorable(enteteCourante[0].charAt(0))) {
			enteteCourante[0] = enteteCourante[0].substring(1);
		}
	}

	@SuppressWarnings("unchecked")
	public <Objet> List<Objet> parseFile(final File file, final Class<Objet> clazz) throws ErreurMoteurCsv, IOException {
		final List<Objet> objets = new ArrayList<Objet>();
		final BufferedReader bufReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)), 8 * 1024);
		nouveauFichier(clazz.getAnnotation(FichierCsv.class).value(), bufReader.readLine());
		String ligne;
		while ((ligne = bufReader.readLine()) != null) {
			objets.add((Objet) creerObjet(ligne));
		}
		return objets;
	}

	protected void writeEntete(BufferedWriter bufWriter, List<String> nomChamps, ClassCsv classCsv) throws IOException {
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

	protected <Objet> void writeLigne(BufferedWriter bufWriter, List<String> nomChamps, ClassCsv classCsv, Objet objet) throws IOException, IllegalAccessException {
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

	public <Objet> void writeFile(File file, Collection<Objet> objets, Class<Objet> clazz)  {
		writeFile(file, objets, clazz, new HashSet<String>());
	}

	public <Objet> void writeFile(File file, Collection<Objet> objets, Class<Objet> clazz, Set<String> champsNoWrites) {
		try {
			BufferedWriter bufWriter = new BufferedWriter(new FileWriter(file));
			ClassCsv classCsv = mapFileClasses.get(clazz.getAnnotation(FichierCsv.class).value());
			List<String> nomChamps = new ArrayList<String>();
			for (String champ : classCsv.getNomChamps()) {
				if (!champsNoWrites.contains(champ)) {
					nomChamps.add(champ);
				}
			}
			writeEntete(bufWriter, nomChamps, classCsv);
			for (Objet objet : objets) {
				writeLigne(bufWriter, nomChamps, classCsv, objet);
			}
			bufWriter.close();
		} catch (Exception exception) {
			throw new ErreurMoteurCsv(exception);
		}
	}

	private void scannerClass(final Class<?> clazz) throws ErreurMoteurCsv {
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
