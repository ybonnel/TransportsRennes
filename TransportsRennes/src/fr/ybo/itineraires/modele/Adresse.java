package fr.ybo.itineraires.modele;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@SuppressWarnings({"serial"})
public class Adresse implements Serializable {

	public double latitude;
	public double longitude;

	public String toUrl() throws UnsupportedEncodingException {
		return URLEncoder.encode(new StringBuilder().append(latitude).append('|').append(longitude).toString(), "UTF-8");
	}

}
