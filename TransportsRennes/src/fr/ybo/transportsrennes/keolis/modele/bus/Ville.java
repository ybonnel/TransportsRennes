package fr.ybo.transportsrennes.keolis.modele.bus;

import java.io.Serializable;

/**
 * Ville.
 *
 * @author ybonnel
 */
@SuppressWarnings("serial")
public class Ville implements Serializable {
	/**
	 * Nom.
	 */
	private String name;
	/**
	 * Nombre de district.
	 */
	private int nombreDistricts;
	/**
	 * Id.
	 */
	private int id;

	/**
	 * @return the id
	 */
	public final int getId() {
		return id;
	}

	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * @return the nombreDistricts
	 */
	public final int getNombreDistricts() {
		return nombreDistricts;
	}

	/**
	 * @param pId the id to set
	 */
	public final void setId(final int pId) {
		id = pId;
	}

	/**
	 * @param pName the name to set
	 */
	public final void setName(final String pName) {
		name = pName;
	}

	/**
	 * @param pNombreDistricts the nombreDistricts to set
	 */
	public final void setNombreDistricts(final int pNombreDistricts) {
		nombreDistricts = pNombreDistricts;
	}
}
