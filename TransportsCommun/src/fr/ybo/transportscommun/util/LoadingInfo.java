package fr.ybo.transportscommun.util;

public abstract class LoadingInfo {

	private int nbEtape;
	private int etapeCourante;

	public void setNbEtape(final int nbEtape) {
		this.nbEtape = nbEtape;
	}

	public int getNbEtape() {
		return nbEtape;
	}

	public void etapeSuivante() {
		etapeCourante++;
	}

	public int getEtapeCourante() {
		return etapeCourante;
	}

}
