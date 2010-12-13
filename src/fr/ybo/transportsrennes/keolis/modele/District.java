package fr.ybo.transportsrennes.keolis.modele;

import java.io.Serializable;

/**
 * Class repr�sentant un district Keolis.
 * 
 * @author ybonnel
 * 
 */
@SuppressWarnings("serial")
public class District implements Serializable {

	/**
	 * Id du district.
	 */
	private String id;

	/**
	 * Le nom du district.
	 */
	private String name;

	/**
	 * @return the id
	 */
	public final String getId() {
		return id;
	}

	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * @param pId
	 *            the id to set
	 */
	public final void setId(final String pId) {
		id = pId;
	}

	/**
	 * @param pName
	 *            the name to set
	 */
	public final void setName(final String pName) {
		name = pName;
	}

	/**
	 * @return le nom du district.
	 */
	@Override
	public final String toString() {
		return name;
	}

}
