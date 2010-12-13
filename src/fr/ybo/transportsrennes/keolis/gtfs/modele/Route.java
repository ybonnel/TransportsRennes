package fr.ybo.transportsrennes.keolis.gtfs.modele;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import fr.ybo.transportsrennes.keolis.gtfs.annotation.BaliseCsv;
import fr.ybo.transportsrennes.keolis.gtfs.annotation.Colonne;
import fr.ybo.transportsrennes.keolis.gtfs.annotation.Colonne.TypeColonne;
import fr.ybo.transportsrennes.keolis.gtfs.annotation.FichierCsv;
import fr.ybo.transportsrennes.keolis.gtfs.annotation.PrimaryKey;
import fr.ybo.transportsrennes.keolis.gtfs.annotation.Table;
import fr.ybo.transportsrennes.keolis.gtfs.database.DataBaseException;
import fr.ybo.transportsrennes.keolis.gtfs.database.DataBaseHelper;
import fr.ybo.transportsrennes.keolis.gtfs.moteur.ErreurMoteurCsv;
import fr.ybo.transportsrennes.keolis.gtfs.moteur.MoteurCsv;
import fr.ybo.transportsrennes.keolis.gtfs.moteur.adapter.AdapterTypeRoute;
import fr.ybo.transportsrennes.util.LogYbo;

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
	@BaliseCsv("agency_id")
	private String agenceId;
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

	public void chargerHeuresArrets(final Context context, final DataBaseHelper dataBaseHelper) throws ErreurMoteurCsv,
			IOException, DataBaseException {
		LOG_YBO.debug("Chargement des heures d'arr�t de la ligne " + nomCourt);
		final List<Class<?>> classes = new ArrayList<Class<?>>();
		classes.add(HeuresArrets.class);
		final MoteurCsv moteur = new MoteurCsv(classes);
		final File file = new File(context.getExternalFilesDir(null), "stopTimes" + id + ".txt");
		if (file.exists()) {
			moteur.parseFileAndInsert(file, HeuresArrets.class, dataBaseHelper, getIdWithoutSpecCar());
		}
		LOG_YBO.debug("Chargement des heures d'arr�t de la ligne " + nomCourt + " termin�.");
	}

	public String getAgenceId() {
		return agenceId;
	}

	public Boolean getChargee() {
		return chargee;
	}

	public String getDescription() {
		return description;
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

	public TypeRoutes getType() {
		return type;
	}

	public void setAgenceId(final String agenceId) {
		this.agenceId = agenceId;
	}

	public void setChargee(final Boolean chargee) {
		this.chargee = chargee;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public void setNomCourt(final String nomCourt) {
		this.nomCourt = nomCourt;
	}

	public void setNomLong(final String nomLong) {
		this.nomLong = nomLong;
	}

	public void setType(final TypeRoutes type) {
		this.type = type;
	}
}
