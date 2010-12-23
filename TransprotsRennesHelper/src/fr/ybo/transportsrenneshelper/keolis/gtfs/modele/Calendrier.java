package fr.ybo.transportsrenneshelper.keolis.gtfs.modele;

import fr.ybo.transportsrenneshelper.keolis.gtfs.annotation.BaliseCsv;
import fr.ybo.transportsrenneshelper.keolis.gtfs.annotation.FichierCsv;
import fr.ybo.transportsrenneshelper.keolis.gtfs.moteur.adapter.AdapterBoolean;

@FichierCsv("calendar.txt")
public class Calendrier {

	@BaliseCsv("service_id")
	private String id;
	@BaliseCsv(value = "monday", adapter = AdapterBoolean.class)
	private Boolean lundi;
	@BaliseCsv(value = "tuesday", adapter = AdapterBoolean.class)
	private Boolean mardi;
	@BaliseCsv(value = "wednesday", adapter = AdapterBoolean.class)
	private Boolean mercredi;
	@BaliseCsv(value = "thursday", adapter = AdapterBoolean.class)
	private Boolean jeudi;
	@BaliseCsv(value = "friday", adapter = AdapterBoolean.class)
	private Boolean vendredi;
	@BaliseCsv(value = "saturday", adapter = AdapterBoolean.class)
	private Boolean samedi;
	@BaliseCsv(value = "sunday", adapter = AdapterBoolean.class)
	private Boolean dimanche;

	public Boolean getDimanche() {
		return dimanche;
	}

	public String getId() {
		return id;
	}

	public Boolean getJeudi() {
		return jeudi;
	}

	public Boolean getLundi() {
		return lundi;
	}

	public Boolean getMardi() {
		return mardi;
	}

	public Boolean getMercredi() {
		return mercredi;
	}

	public Boolean getSamedi() {
		return samedi;
	}

	public Boolean getVendredi() {
		return vendredi;
	}

	public void setDimanche(final Boolean dimanche) {
		this.dimanche = dimanche;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public void setJeudi(final Boolean jeudi) {
		this.jeudi = jeudi;
	}

	public void setLundi(final Boolean lundi) {
		this.lundi = lundi;
	}

	public void setMardi(final Boolean mardi) {
		this.mardi = mardi;
	}

	public void setMercredi(final Boolean mercredi) {
		this.mercredi = mercredi;
	}

	public void setSamedi(final Boolean samedi) {
		this.samedi = samedi;
	}

	public void setVendredi(final Boolean vendredi) {
		this.vendredi = vendredi;
	}

	public Calendrier() {
	}

	public Calendrier(Calendrier calendrier) {
		this.lundi = calendrier.lundi;
		this.mardi = calendrier.mardi;
		this.mercredi = calendrier.mercredi;
		this.jeudi = calendrier.jeudi;
		this.vendredi = calendrier.vendredi;
		this.samedi = calendrier.samedi;
		this.dimanche = calendrier.dimanche;
	}

	public void merge(Calendrier calendrier) {
		this.lundi = this.lundi || calendrier.lundi;
		this.mardi = this.mardi || calendrier.mardi;
		this.mercredi = this.mercredi || calendrier.mercredi;
		this.jeudi = this.jeudi || calendrier.jeudi;
		this.vendredi = this.vendredi || calendrier.vendredi;
		this.samedi = this.samedi || calendrier.samedi;
		this.dimanche = this.dimanche || calendrier.dimanche;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Calendrier that = (Calendrier) o;

		if (dimanche != null ? !dimanche.equals(that.dimanche) : that.dimanche != null) return false;
		if (jeudi != null ? !jeudi.equals(that.jeudi) : that.jeudi != null) return false;
		if (lundi != null ? !lundi.equals(that.lundi) : that.lundi != null) return false;
		if (mardi != null ? !mardi.equals(that.mardi) : that.mardi != null) return false;
		if (mercredi != null ? !mercredi.equals(that.mercredi) : that.mercredi != null) return false;
		if (samedi != null ? !samedi.equals(that.samedi) : that.samedi != null) return false;
		if (vendredi != null ? !vendredi.equals(that.vendredi) : that.vendredi != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = lundi != null ? lundi.hashCode() : 0;
		result = 31 * result + (mardi != null ? mardi.hashCode() : 0);
		result = 31 * result + (mercredi != null ? mercredi.hashCode() : 0);
		result = 31 * result + (jeudi != null ? jeudi.hashCode() : 0);
		result = 31 * result + (vendredi != null ? vendredi.hashCode() : 0);
		result = 31 * result + (samedi != null ? samedi.hashCode() : 0);
		result = 31 * result + (dimanche != null ? dimanche.hashCode() : 0);
		return result;
	}
}
