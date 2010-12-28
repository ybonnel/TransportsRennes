package fr.ybo.transportsrenneshelper.keolis.gtfs.moteur.modele;

import fr.ybo.transportsrenneshelper.keolis.gtfs.moteur.ErreurMoteurCsv;
import fr.ybo.transportsrenneshelper.keolis.gtfs.moteur.adapter.AdapterCsv;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ChampCsv {

	private final Class<? extends AdapterCsv<?>> adapter;
	private static Map<Class<? extends AdapterCsv<?>>, AdapterCsv<?>> mapAdapters = new HashMap<Class<? extends AdapterCsv<?>>, AdapterCsv<?>>();
	private final Field field;

	public ChampCsv(final Class<? extends AdapterCsv<?>> adapter, final Field field) {
		super();
		this.adapter = adapter;
		this.field = field;
	}

	public Class<? extends AdapterCsv<?>> getAdapter() {
		return adapter;
	}

	public Field getField() {
		return field;
	}

	@SuppressWarnings("unchecked")
	public AdapterCsv<Object> getNewAdapterCsv() {
		if (!mapAdapters.containsKey(adapter)) {
			try {
				final Constructor<? extends AdapterCsv<?>> constructeur = adapter.getConstructor((Class<?>[]) null);
				mapAdapters.put(adapter, constructeur.newInstance((Object[]) null));
			} catch (Exception e) {
				throw new ErreurMoteurCsv(e);
			}
		}
		return (AdapterCsv<Object>) mapAdapters.get(adapter);
	}
}
