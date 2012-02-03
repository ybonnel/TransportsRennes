package fr.ybo.transportscommun.donnees.manager;

import com.google.code.geocoder.util.StringUtils;

import fr.ybo.moteurcsv.MoteurCsv.InsertObject;
import fr.ybo.transportscommun.AbstractTransportsApplication;
import fr.ybo.transportscommun.donnees.modele.Arret;
import fr.ybo.transportscommun.donnees.modele.ArretFavori;
import fr.ybo.transportscommun.donnees.modele.ArretRoute;
import fr.ybo.transportscommun.donnees.modele.Direction;
import fr.ybo.transportscommun.donnees.modele.GroupeFavori;
import fr.ybo.transportscommun.donnees.modele.Ligne;
import fr.ybo.transportscommun.util.LogYbo;

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
		if (AbstractTransportsApplication.getDataBaseHelper().selectSingle(favoriSelect) != null) {
			// Le favori existe déjà, on fait rien.
			return;
		}
		ligneSelect.id = favori.ligneId;
		Ligne ligne = AbstractTransportsApplication.getDataBaseHelper().selectSingle(ligneSelect);
		arretSelect.id = favori.arretId;
		Arret arret = AbstractTransportsApplication.getDataBaseHelper().selectSingle(arretSelect);
		arretRouteSelect.ligneId = favori.ligneId;
		arretRouteSelect.arretId = favori.arretId;
		arretRouteSelect.macroDirection = favori.macroDirection;
		ArretRoute arretRoute = AbstractTransportsApplication.getDataBaseHelper().selectSingle(arretRouteSelect);
		if (ligne == null || arret == null || arretRoute == null) {
			LOG_YBO.debug("Le favori avec arretId = " + favori.arretId + ", ligneId = " + favori.ligneId
					+ " n'a plus de correspondances dans la base -> on l'insert pas");
		} else {
			directionSelect.id = arretRoute.directionId;
			favori.direction = AbstractTransportsApplication.getDataBaseHelper().selectSingle(directionSelect).direction;
			favori.nomArret = arret.nom;
			favori.nomCourt = ligne.nomCourt;
			favori.nomLong = ligne.nomLong;
			if (StringUtils.isNotBlank(favori.groupe)) {
				groupeSelect.name = favori.groupe;
				if (AbstractTransportsApplication.getDataBaseHelper().selectSingle(groupeSelect) == null) {
					AbstractTransportsApplication.getDataBaseHelper().insert(groupeSelect);
				}
			}
			AbstractTransportsApplication.getDataBaseHelper().insert(favori);
		}
	}
}