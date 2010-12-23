package fr.ybo.transportsrenneshelper.keolis.gtfs.moteur.adapter;

public interface AdapterCsv<Objet> {

	public Objet parse(String chaine);

	public String toString(Objet objet);
}
