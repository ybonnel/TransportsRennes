package fr.ybo.itineraires.schema;

import java.io.Serializable;

@SuppressWarnings({"serial", "ClassMayBeInterface"})
public abstract class PortionTrajet implements Serializable {

	public abstract void remplirAttribut(String name, String contenu);
}
