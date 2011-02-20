
package fr.ybo.itineraires.schema;

public class JointurePieton
    extends PortionTrajetPieton
{
    protected String arretId;
    protected Adresse adresse;

    public String getArretId() {
        return arretId;
    }

    public void setArretId(final String value) {
	    arretId = value;
    }

    public Adresse getAdresse() {
        return adresse;
    }

    public void setAdresse(final Adresse value) {
	    adresse = value;
    }

}
