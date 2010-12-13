package fr.ybo.transportsrennes.keolis.gtfs.moteur.modele;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import fr.ybo.transportsrennes.keolis.gtfs.moteur.ErreurMoteurCsv;

public class ClassCsv {
	private final String separateur;
	private final Class<?> clazz;
	private Constructor<?> contructeur;

	private final Map<String, ChampCsv> mapOfFields = new HashMap<String, ChampCsv>();

	public ClassCsv(final String separateur, final Class<?> clazz) throws ErreurMoteurCsv {
		this.separateur = separateur;
		this.clazz = clazz;
		try {
			contructeur = clazz.getDeclaredConstructor((Class<?>[]) null);
		} catch (final SecurityException e) {
			throw new ErreurMoteurCsv("Erreur a la r�cup�ration du constructeur de " + clazz.getSimpleName(), e);
		} catch (final NoSuchMethodException e) {
			throw new ErreurMoteurCsv("Erreur a la r�cup�ration du constructeur de " + clazz.getSimpleName(), e);
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
