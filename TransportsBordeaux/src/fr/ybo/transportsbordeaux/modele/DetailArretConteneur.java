package fr.ybo.transportsbordeaux.modele;

public class DetailArretConteneur {

	private int horaire;
	private int trajetId;
	private int sequence;

	public DetailArretConteneur(int horaire, int trajetId, int sequence) {
		super();
		this.horaire = horaire;
		this.trajetId = trajetId;
		this.sequence = sequence;
	}

	public int getHoraire() {
		return horaire;
	}

	public int getTrajetId() {
		return trajetId;
	}

	public int getSequence() {
		return sequence;
	}

}
