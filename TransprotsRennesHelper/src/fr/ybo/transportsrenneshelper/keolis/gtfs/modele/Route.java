package fr.ybo.transportsrenneshelper.keolis.gtfs.modele;

import fr.ybo.transportsrenneshelper.keolis.gtfs.annotation.BaliseCsv;
import fr.ybo.transportsrenneshelper.keolis.gtfs.annotation.FichierCsv;
import fr.ybo.transportsrenneshelper.keolis.gtfs.moteur.adapter.AdapterInteger;
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
	@BaliseCsv("route_short_name")
	private String nomCourt;
	@BaliseCsv("route_long_name")
	private String nomLong;

	@BaliseCsv(value = "route_type", adapter = AdapterTypeRoute.class)
	private TypeRoutes type;

	@BaliseCsv(value = "route_ordre", adapter = AdapterInteger.class)
	private Integer ordre;

	private String nomCourtFormatte;

	private Boolean chargee;

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

	public TypeRoutes getType() {
		return type;
	}

	public void setChargee(final Boolean chargee) {
		this.chargee = chargee;
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

	public Integer getOrdre() {
		return ordre;
	}

	public void setOrdre(Integer ordre) {
		this.ordre = ordre;
	}

	public String getNomCourtFormatte() {
		return nomCourtFormatte;
	}

	public void setNomCourtFormatte(String nomCourtFormatte) {
		this.nomCourtFormatte = nomCourtFormatte;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Route route = (Route) o;

		if (id != null ? !id.equals(route.id) : route.id != null) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}
}
