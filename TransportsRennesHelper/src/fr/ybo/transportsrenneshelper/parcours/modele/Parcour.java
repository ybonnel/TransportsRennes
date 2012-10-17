package fr.ybo.transportsrenneshelper.parcours.modele;

import java.util.ArrayList;
import java.util.List;

public class Parcour {

	public static class Arret {
		private String stopId;
		private String stopName;
		private Integer stopSequence;

		public String getStopId() {
			return stopId;
		}

		public void setStopId(String stopId) {
			this.stopId = stopId;
		}

		public String getStopName() {
			return stopName;
		}

		public void setStopName(String stopName) {
			this.stopName = stopName;
		}

		public Integer getStopSequence() {
			return stopSequence;
		}

		public void setStopSequence(Integer stopSequence) {
			this.stopSequence = stopSequence;
		}
	}

	private String ligneId;

	private String headSign;

	private Integer macroDirection;

	private String ligneName;

	private List<Parcour.Arret> stops;

	public String getLigneId() {
		return ligneId;
	}

	public void setLigneId(String ligneId) {
		this.ligneId = ligneId;
	}

	public String getHeadSign() {
		return headSign;
	}

	public void setHeadSign(String headSign) {
		String[] champs = headSign.split("\\|");
		this.headSign = champs[1];
		while (headSign.length() > 0 && headSign.charAt(0) == ' ') {
			headSign = headSign.substring(1);
		}
	}

	public Integer getMacroDirection() {
		return macroDirection;
	}

	public void setMacroDirection(Integer macroDirection) {
		this.macroDirection = macroDirection;
	}

	public String getLigneName() {
		return ligneName;
	}

	public void setLigneName(String ligneName) {
		this.ligneName = ligneName;
	}

	public List<Parcour.Arret> getStops() {
		if (stops == null) {
			stops = new ArrayList<Parcour.Arret>();
		}
		return stops;
	}
}
