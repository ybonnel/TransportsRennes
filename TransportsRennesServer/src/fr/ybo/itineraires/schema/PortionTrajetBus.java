
package fr.ybo.itineraires.schema;

public class PortionTrajetBus
{
    protected String ligneId;
    protected String arretDepartId;
    protected String heureDepart;
    protected String arretArriveeId;
    protected String heureArrivee;

    public String getLigneId() {
        return ligneId;
    }

    public void setLigneId(final String value) {
	    ligneId = value;
    }

    public String getArretDepartId() {
        return arretDepartId;
    }

    public void setArretDepartId(final String value) {
	    arretDepartId = value;
    }

    public String getHeureDepart() {
        return heureDepart;
    }

    public void setHeureDepart(final String value) {
	    heureDepart = value;
    }

    public String getArretArriveeId() {
        return arretArriveeId;
    }

    public void setArretArriveeId(final String value) {
	    arretArriveeId = value;
    }

    public String getHeureArrivee() {
        return heureArrivee;
    }

    public void setHeureArrivee(final String value) {
	    heureArrivee = value;
    }

}
