package fr.ybo.transportsrennes.keolis.gtfs.modele;

import java.util.Date;

import fr.ybo.transportsrennes.keolis.gtfs.annotation.Colonne;
import fr.ybo.transportsrennes.keolis.gtfs.annotation.Colonne.TypeColonne;
import fr.ybo.transportsrennes.keolis.gtfs.annotation.Table;

@Table
public class DernierMiseAJour {

	@Colonne(type = TypeColonne.DATE)
	private Date derniereMiseAJour;

	public Date getDerniereMiseAJour() {
		return derniereMiseAJour;
	}

	public void setDerniereMiseAJour(final Date derniereMiseAJour) {
		this.derniereMiseAJour = derniereMiseAJour;
	}
}
