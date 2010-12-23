package fr.ybo.transportsrennes.keolis.gtfs.moteur.modele;

import fr.ybo.transportsrennes.keolis.gtfs.moteur.ErreurMoteurCsv;
import fr.ybo.transportsrennes.keolis.gtfs.moteur.adapter.AdapterCsv;

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

	public Field getField() {
		return field;
	}

	public AdapterCsv<?> getNewAdapterCsv() {
		if (!mapAdapters.containsKey(adapter)) {
			try {
				final Constructor<? extends AdapterCsv<?>> construteur = adapter.getConstructor((Class<?>[]) null);
				mapAdapters.put(adapter, construteur.newInstance((Object[]) null));
			} catch (final Exception exception) {
				throw new ErreurMoteurCsv(exception);
			}
		}
		return mapAdapters.get(adapter);
	}
}
