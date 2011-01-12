package fr.ybo.transportsrennes.keolis.gtfs.modele;

import fr.ybo.transportsrennes.keolis.gtfs.annotation.Colonne;
import fr.ybo.transportsrennes.keolis.gtfs.annotation.PrimaryKey;
import fr.ybo.transportsrennes.keolis.gtfs.annotation.Table;

import java.io.Serializable;

@SuppressWarnings("serial")
@Table
public class ArretFavori implements Serializable {
	@Colonne
	@PrimaryKey
	public String arretId;
	@Colonne
	@PrimaryKey
	public String ligneId;
	@Colonne
	public String nomArret;
	@Colonne
	public String direction;
	@Colonne
	public String nomCourt;
	@Colonne
	public String nomLong;
	@Colonne(type = Colonne.TypeColonne.INTEGER)
	public Integer ordre;
}
