package fr.ybo.transportscommun.util;

public abstract class LoadingInfo {

	private int nbEtape;
	private int etapeCourante;

	public void setNbEtape() {
		nbEtape = 9;
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
