package fr.ybo.transportsrennes.keolis.modele;

/**
 * Paramètre d'une URL.
 *
 * @author ybonnel
 */
public class ParametreUrl {

	/**
	 * name.
	 */
	private final String name;
	/**
	 * value.
	 */
	private final String value;

	/**
	 * @param pName  name.
	 * @param pValue value.
	 */
	public ParametreUrl(final String pName, final String pValue) {
		super();
		name = pName;
		value = pValue;
	}

	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * @return the value
	 */
	public final String getValue() {
		return value;
	}
}
