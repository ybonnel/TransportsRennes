package fr.ybo.transportsrennes.keolis.gtfs.modele;

/**
 * 0 - Tram, tramway, train léger sur rail. Tout système de métro léger ou de
 * niveau de la rue dans une région métropolitaine.<br/>
 * 1 - Métro, Métro. Tout système de métro dans une zone métropolitaine.<br/>
 * 2 - Rail. Utilisé pour les interurbains ou Voyage de longue distance.<br/>
 * 3 - Bus. Utilisé pour les lignes de bus à court et à longue distance.<br/>
 * 4 - Ferry. Utilisé pour le service de bateau à court et à longue distance.<br/>
 * 5 - Télécabine. Utilisé pour les remontées mécaniques au niveau des rues où
 * le câble passe sous la voiture.<br/>
 * 6 - gondole, téléphérique suspendu. Généralement utilisé pour les
 * téléphériques où la voiture est suspendu au câble.<br/>
 * 7 - Funiculaire. Tout système ferroviaire conçu pour les pentes raides.<br/>
 *
 * @author ybonnel
 */
public enum TypeRoutes {
	TRAMWAY(0), METRO(1), TRAIN(2), BUS(3), FERRY(4), TELECABINE(5), TELEPHERIQUE(6), FUNICULAIRE(7);

	public static TypeRoutes valueOf(final int indice) {
		for (final TypeRoutes value : values()) {
			if (value.getIndice() == indice) {
				return value;
			}
		}
		return null;
	}

	private int indice;

	private TypeRoutes(final int indiceIn) {
		indice = indiceIn;
	}

	public int getIndice() {
		return indice;
	}
}
