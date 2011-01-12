package fr.ybo.transportsrennes.keolis.modele.bus;

import fr.ybo.transportsrennes.keolis.modele.ObjetWithDistance;

import java.io.Serializable;

/**
 * Un point de vente.
 *
 * @author ybonnel
 */
@SuppressWarnings("serial")
public class PointDeVente extends ObjetWithDistance implements Serializable {
	/**
	 * Nom du point de vente.
	 */
	public String name;
	/**
	 * Type du point de vente.
	 */
	public String type;
	/**
	 * Adresse du point de vente.
	 */
	public String adresse;
	/**
	 * Code postal du point de vente.
	 */
	public String codePostal;
	/**
	 * Ville du point de vente.
	 */
	public String ville;
	/**
	 * District du point de vente.
	 */
	public String district;
	/**
	 * Téléphone du point de vente.
	 */
	public String telephone;
	/**
	 * Schedule du point de vente.
	 */
	public String schedule;
	/**
	 * Latitude du point de vente.
	 */
	public double latitude;
	/**
	 * Longitude du point de vente.
	 */
	public double longitude;

	/**
	 * @return the latitude
	 */
	@Override
	public final double getLatitude() {
		return latitude;
	}

	/**
	 * @return the longitude
	 */
	@Override
	public final double getLongitude() {
		return longitude;
	}
}
