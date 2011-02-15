
package fr.ybo.itineraires.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PortionTrajetBus complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PortionTrajetBus">
 *   &lt;complexContent>
 *     &lt;extension base="{}PortionTrajet">
 *       &lt;sequence>
 *         &lt;element name="ligneId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="arretDepartId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="heureDepart" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="arretArriveeId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="heureArrivee" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PortionTrajetBus", propOrder = {
    "ligneId",
    "arretDepartId",
    "heureDepart",
    "arretArriveeId",
    "heureArrivee"
})
public class PortionTrajetBus
    extends PortionTrajet
{

    @XmlElement(required = true)
    protected String ligneId;
    @XmlElement(required = true)
    protected String arretDepartId;
    @XmlElement(required = true)
    protected String heureDepart;
    @XmlElement(required = true)
    protected String arretArriveeId;
    @XmlElement(required = true)
    protected String heureArrivee;

    /**
     * Gets the value of the ligneId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLigneId() {
        return ligneId;
    }

    /**
     * Sets the value of the ligneId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLigneId(final String value) {
	    ligneId = value;
    }

    /**
     * Gets the value of the arretDepartId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArretDepartId() {
        return arretDepartId;
    }

    /**
     * Sets the value of the arretDepartId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArretDepartId(final String value) {
	    arretDepartId = value;
    }

    /**
     * Gets the value of the heureDepart property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHeureDepart() {
        return heureDepart;
    }

    /**
     * Sets the value of the heureDepart property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHeureDepart(final String value) {
	    heureDepart = value;
    }

    /**
     * Gets the value of the arretArriveeId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArretArriveeId() {
        return arretArriveeId;
    }

    /**
     * Sets the value of the arretArriveeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArretArriveeId(final String value) {
	    arretArriveeId = value;
    }

    /**
     * Gets the value of the heureArrivee property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHeureArrivee() {
        return heureArrivee;
    }

    /**
     * Sets the value of the heureArrivee property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHeureArrivee(final String value) {
	    heureArrivee = value;
    }

}
