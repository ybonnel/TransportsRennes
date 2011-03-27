
package fr.ybo.itineraires.schema;

public class PortionTrajetBus
{
    protected String ligneId;
    protected String arretDepartId;
    protected String heureDepart;
    protected String arretArriveeId;
    protected String heureArrivee;
	protected Integer directionId;
	protected Integer macroDirectionId;

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

	public Integer getDirectionId() {
		return directionId;
	}

	public void setDirectionId(Integer directionId) {
		this.directionId = directionId;
	}

	/**
	 * @return the macroDirectionId
	 */
	public Integer getMacroDirectionId() {
		return macroDirectionId;
	}

	/**
	 * @param macroDirectionId
	 *            the macroDirectionId to set
	 */
	public void setMacroDirectionId(Integer macroDirectionId) {
		this.macroDirectionId = macroDirectionId;
	}

}
