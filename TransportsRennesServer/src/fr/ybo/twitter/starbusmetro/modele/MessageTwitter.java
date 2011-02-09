package fr.ybo.twitter.starbusmetro.modele;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class MessageTwitter {
	
	private transient SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;

    @Persistent
	public Date dateCreation;
    
    @Persistent
	public String texte;
	
	public MessageTwitter(Date dateCreation, String texte) {
		this.dateCreation = dateCreation;
		this.texte = texte;
	}

	public MessageTwitter() {
	}

	public Date getDateCreation() {
		return dateCreation;
	}

	public void setDateCreation(Date dateCreation) {
		this.dateCreation = dateCreation;
	}

	public String getTexte() {
		return texte;
	}

	public void setTexte(String texte) {
		this.texte = texte;
	}
	
	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public String toXml() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<message>");
		stringBuilder.append("<dateCreation>");
		stringBuilder.append(simpleDateFormat.format(dateCreation));
		stringBuilder.append("</dateCreation>");
		stringBuilder.append("<contenu>");
		stringBuilder.append(texte);
		stringBuilder.append("</contenu>");
		stringBuilder.append("</message>");
		return stringBuilder.toString();
	}
}
