package fr.ybo.transportsrennes.keolis.gtfs.moteur.adapter;

public interface AdapterCsv<Objet> {

	public Objet parse(String chaine);
}
