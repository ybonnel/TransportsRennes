package fr.ybo.transportsrennes.database;

import fr.ybo.moteurcsv.MoteurCsv.InsertObject;
import fr.ybo.transportscommun.donnees.modele.Arret;
import fr.ybo.transportscommun.donnees.modele.ArretFavori;
import fr.ybo.transportscommun.donnees.modele.ArretRoute;
import fr.ybo.transportscommun.donnees.modele.Direction;
import fr.ybo.transportscommun.donnees.modele.GroupeFavori;
import fr.ybo.transportscommun.donnees.modele.Ligne;
import fr.ybo.transportscommun.util.LogYbo;
import fr.ybo.transportsrennes.application.TransportsRennesApplication;
import fr.ybo.transportsrennes.util.StringUtils;

public class InsertArretFavori implements InsertObject<ArretFavori> {
	private static final LogYbo LOG_YBO = new LogYbo(FavorisManager.class);

	private Ligne ligneSelect = new Ligne();
	private Arret arretSelect = new Arret();
	private ArretRoute arretRouteSelect = new ArretRoute();
	private Direction directionSelect = new Direction();
	private ArretFavori favoriSelect = new ArretFavori();
	private GroupeFavori groupeSelect = new GroupeFavori();

	@Override
	public void insertObject(ArretFavori favori) {
		favoriSelect.ligneId = favori.ligneId;
		favoriSelect.arretId = favori.arretId;
		favoriSelect.macroDirection = favori.macroDirection;
		if (TransportsRennesApplication.getDataBaseHelper().selectSingle(favoriSelect) != null) {
			// Le favori existe déjà, on fait rien.
			return;
		}
		ligneSelect.id = favori.ligneId;
		Ligne ligne = TransportsRennesApplication.getDataBaseHelper().selectSingle(ligneSelect);
		arretSelect.id = favori.arretId;
		Arret arret = TransportsRennesApplication.getDataBaseHelper().selectSingle(arretSelect);
		arretRouteSelect.ligneId = favori.ligneId;
		arretRouteSelect.arretId = favori.arretId;
		arretRouteSelect.macroDirection = favori.macroDirection;
		ArretRoute arretRoute = TransportsRennesApplication.getDataBaseHelper().selectSingle(arretRouteSelect);
		if (ligne == null || arret == null || arretRoute == null) {
			LOG_YBO.debug("Le favori avec arretId = " + favori.arretId + ", ligneId = " + favori.ligneId
					+ " n'a plus de correspondances dans la base -> on l'insert pas");
		} else {
			directionSelect.id = arretRoute.directionId;
			favori.direction = TransportsRennesApplication.getDataBaseHelper().selectSingle(directionSelect).direction;
			favori.nomArret = arret.nom;
			favori.nomCourt = ligne.nomCourt;
			favori.nomLong = ligne.nomLong;
			if (StringUtils.isNotBlank(favori.groupe)) {
				groupeSelect.name = favori.groupe;
				if (TransportsRennesApplication.getDataBaseHelper().selectSingle(groupeSelect) == null) {
					TransportsRennesApplication.getDataBaseHelper().insert(groupeSelect);
				}
			}
			TransportsRennesApplication.getDataBaseHelper().insert(favori);
		}
	}
}