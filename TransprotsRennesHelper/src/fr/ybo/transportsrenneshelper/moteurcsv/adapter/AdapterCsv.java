package fr.ybo.transportsrenneshelper.moteurcsv.adapter;

public interface AdapterCsv<Objet> {

	public Objet parse(String chaine);

	public String toString(Objet objet);
}
