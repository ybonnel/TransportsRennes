package fr.ybo.transportsrenneshelper.keolis.gtfs.modele;

import fr.ybo.transportsrenneshelper.keolis.gtfs.annotation.BaliseCsv;
import fr.ybo.transportsrenneshelper.keolis.gtfs.annotation.FichierCsv;
import fr.ybo.transportsrenneshelper.keolis.gtfs.moteur.adapter.AdapterTypeRoute;

import java.io.Serializable;

@SuppressWarnings("serial")
@FichierCsv("routes.txt")
public class Route implements Serializable {

	public static String getIdWithoutSpecCar(final String id) {
		return id.replace('-', '_');
	}

	@BaliseCsv("route_id")
	private String id;
	@BaliseCsv("agency_id")
	private String agenceId;
	@BaliseCsv("route_short_name")
	private String nomCourt;
	@BaliseCsv("route_long_name")
	private String nomLong;

	@BaliseCsv("route_desc")
	private String description;

	@BaliseCsv(value = "route_type", adapter = AdapterTypeRoute.class)
	private TypeRoutes type;

	private Boolean chargee;

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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Route route = (Route) o;

		if (id != null ? !id.equals(route.id) : route.id != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}
}
