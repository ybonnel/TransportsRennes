package fr.ybo.transportsrennes.keolis.gtfs.moteur.modele;

import fr.ybo.transportsrennes.keolis.gtfs.moteur.ErreurMoteurCsv;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

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
