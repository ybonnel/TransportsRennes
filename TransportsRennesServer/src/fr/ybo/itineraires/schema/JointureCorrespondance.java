
package fr.ybo.itineraires.schema;

public class JointureCorrespondance
    extends PortionTrajetPieton
{
    protected String arretDepartId;
    protected String arretArriveeId;

    public String getArretDepartId() {
        return arretDepartId;
    }

    public void setArretDepartId(final String value) {
	    arretDepartId = value;
    }

    public String getArretArriveeId() {
        return arretArriveeId;
    }

    public void setArretArriveeId(final String value) {
	    arretArriveeId = value;
    }

}
