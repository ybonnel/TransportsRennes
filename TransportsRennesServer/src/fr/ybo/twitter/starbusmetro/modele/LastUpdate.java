package fr.ybo.twitter.starbusmetro.modele;

import java.util.Date;

public class LastUpdate {
	
	private static LastUpdate instance = null;
	
	public static synchronized LastUpdate getInstance() {
		if (instance == null) {
			instance = new LastUpdate();
		}
		return instance;
	}
	
	private LastUpdate() {
	}
	
	// 5 minutes.
	private final static long ECART_UPDATE = 300000;
	
	private Date lastUpdate = null;
	
	public synchronized boolean isUpdate() {
		Date dateCourante = new Date();
		if (lastUpdate == null) {
			lastUpdate = dateCourante;
			return false;
		} else {
			return !((dateCourante.getTime() - lastUpdate.getTime()) > ECART_UPDATE); 
		}
	}

}
