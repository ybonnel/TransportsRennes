package fr.ybo.transportsrennes.keolis.gtfs.modele;

/**
 * 0 - Tram, tramway, train l�ger sur rail. Tout syst�me de m�tro l�ger ou de
 * niveau de la rue dans une r�gion m�tropolitaine.<br/>
 * 1 - M�tro, M�tro. Tout syst�me de m�tro dans une zone m�tropolitaine.<br/>
 * 2 - Rail. Utilis� pour les interurbains ou Voyage de longue distance.<br/>
 * 3 - Bus. Utilis� pour les lignes de bus � court et � long-distance.<br/>
 * 4 - Ferry. Utilis� pour le service de bateau � court et � long-distance.<br/>
 * 5 - T�l�cabine. Utilis� pour les remont�es m�caniques au niveau des rues o�
 * le c�ble passe sous la voiture.<br/>
 * 6 - gondole, t�l�ph�rique suspendu. G�n�ralement utilis� pour les
 * t�l�ph�riques o� la voiture est suspendu au c�ble.<br/>
 * 7 - Funiculaire. Tout syst�me ferroviaire con�u pour les pentes raides.<br/>
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
