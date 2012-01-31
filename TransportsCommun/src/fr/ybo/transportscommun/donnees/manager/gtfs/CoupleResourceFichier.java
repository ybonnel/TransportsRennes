package fr.ybo.transportscommun.donnees.manager.gtfs;

public class CoupleResourceFichier {
    final int resourceId;
    final String fichier;

	public CoupleResourceFichier(int resourceId, String fichier) {
        this.resourceId = resourceId;
        this.fichier = fichier;
    }
}