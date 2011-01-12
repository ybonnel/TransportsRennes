package fr.ybo.transportsrennes.keolis.modele.velos;

import fr.ybo.transportsrennes.keolis.modele.ObjetWithDistance;

import java.io.Serializable;

/**
 * Classe représentant une station de velo star.
 *
 * @author ybonnel
 */
public class Station extends ObjetWithDistance implements Serializable {
	/**
	 * Numéro de la station.
	 */
	public String number;
	/**
	 * Nom de la station.
	 */
	public String name;
	/**
	 * adresse de la station.
	 */
	public String adresse;

	/**
	 * Etat de la station.
	 */
	public boolean state;

	/**
	 * Latitude.
	 */
	public double latitude;

	/**
	 * Longitude.
	 */
	public double longitude;
	/**
	 * Places libres.
	 */
	public int slotsavailable;
	/**
	 * Vélos libres.
	 */
	public int bikesavailable;
	/**
	 * Position.
	 */
	public boolean pos;
	/**
	 * Nom du district.
	 */
	public String district;
	/**
	 * Date de dernière mise à jour.
	 */
	public String lastupdate;

	/**
	 * Getter.
	 *
	 * @return la latitude.
	 */
	@Override
	public double getLatitude() {
		return latitude;
	}

	/**
	 * Getter.
	 *
	 * @return la longitude.
	 */
	@Override
	public double getLongitude() {
		return longitude;
	}
}
