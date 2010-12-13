package fr.ybo.transportsrennes.keolis.gtfs.moteur.modele;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import fr.ybo.transportsrennes.keolis.gtfs.moteur.adapter.AdapterCsv;

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

	public AdapterCsv<?> getNewAdapterCsv() {
		if (!mapAdapters.containsKey(adapter)) {
			try {
				final Constructor<? extends AdapterCsv<?>> construteur = adapter.getConstructor((Class<?>[]) null);
				mapAdapters.put(adapter, construteur.newInstance((Object[]) null));
			} catch (final SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (final NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (final IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (final InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (final IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (final InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return mapAdapters.get(adapter);
	}
}
