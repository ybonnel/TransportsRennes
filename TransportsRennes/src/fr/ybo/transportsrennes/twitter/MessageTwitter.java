package fr.ybo.transportsrennes.twitter;

import java.util.Date;

public class MessageTwitter {

	public Date dateCreation;
	public String texte;

	public MessageTwitter(Date dateCreation, String texte) {
		this.dateCreation = dateCreation;
		this.texte = texte;
	}

	public MessageTwitter() {
	}
}
