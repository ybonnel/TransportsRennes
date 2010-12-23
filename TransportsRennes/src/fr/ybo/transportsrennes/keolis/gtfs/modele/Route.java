package fr.ybo.transportsrennes.keolis.gtfs.modele;

import fr.ybo.transportsrennes.keolis.gtfs.annotation.*;
import fr.ybo.transportsrennes.keolis.gtfs.annotation.Colonne.TypeColonne;
import fr.ybo.transportsrennes.keolis.gtfs.database.DataBaseHelper;
import fr.ybo.transportsrennes.keolis.gtfs.files.GestionZipKeolis;
import fr.ybo.transportsrennes.keolis.gtfs.moteur.MoteurCsv;
import fr.ybo.transportsrennes.keolis.gtfs.moteur.adapter.AdapterTypeRoute;
import fr.ybo.transportsrennes.util.Formatteur;
import fr.ybo.transportsrennes.util.LogYbo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
@Table
@FichierCsv("routes.txt")
public class Route implements Serializable {
	private static final LogYbo LOG_YBO = new LogYbo(Route.class);

	public static String getIdWithoutSpecCar(final String id) {
		return id.replace('-', '_');
	}

	@Colonne
	@PrimaryKey
	@BaliseCsv("route_id")
	private String id;
	@Colonne
	@BaliseCsv("route_short_name")
	private String nomCourt;
	@Colonne
	@BaliseCsv("route_long_name")
	private String nomLong;
	@Colonne
	@BaliseCsv("route_desc")
	private String description;
	@Colonne(type = TypeColonne.ENUM, clazz = TypeRoutes.class, methode = "getIndice")
	@BaliseCsv(value = "route_type", adapter = AdapterTypeRoute.class)
	private TypeRoutes type;
	@Colonne(type = TypeColonne.BOOLEAN)
	private Boolean chargee;

	public void chargerHeuresArrets(final DataBaseHelper dataBaseHelper) {
		LOG_YBO.debug("Chargement des heures d'arrét de la ligne " + nomCourt);
		final List<Class<?>> classes = new ArrayList<Class<?>>();
		classes.add(HeuresArrets.class);
		final MoteurCsv moteur = new MoteurCsv(classes);
		GestionZipKeolis.chargeRoute(moteur, getIdWithoutSpecCar(), dataBaseHelper);
		LOG_YBO.debug("Chargement des heures d'arrét de la ligne " + nomCourt + " terminé.");
	}

	public Boolean getChargee() {
		return chargee;
	}

	public String getId() {
		return id;
	}

	public String getIdWithoutSpecCar() {
		return getIdWithoutSpecCar(id);
	}

	public String getNomCourt() {
		return nomCourt;
	}

	public String getNomLong() {
		return nomLong;
	}

	public String getNomLongFormate() {
		return Formatteur.formatterChaine(nomLong);
	}

	public void setChargee(final Boolean chargee) {
		this.chargee = chargee;
	}

	public void setId(final String id) {
		this.id = id;
	}
}
