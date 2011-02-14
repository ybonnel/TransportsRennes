
package fr.ybo.itineraires.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for JointurePieton complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="JointurePieton">
 *   &lt;complexContent>
 *     &lt;extension base="{}PortionTrajetPieton">
 *       &lt;sequence>
 *         &lt;element name="arretId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="adresse" type="{}Adresse"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "JointurePieton", propOrder = {
    "arretId",
    "adresse"
})
public class JointurePieton
    extends PortionTrajetPieton
{

    @XmlElement(required = true)
    protected String arretId;
    @XmlElement(required = true)
    protected Adresse adresse;

    /**
     * Gets the value of the arretId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArretId() {
        return arretId;
    }

    /**
     * Sets the value of the arretId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArretId(String value) {
        this.arretId = value;
    }

    /**
     * Gets the value of the adresse property.
     * 
     * @return
     *     possible object is
     *     {@link Adresse }
     *     
     */
    public Adresse getAdresse() {
        return adresse;
    }

    /**
     * Sets the value of the adresse property.
     * 
     * @param value
     *     allowed object is
     *     {@link Adresse }
     *     
     */
    public void setAdresse(Adresse value) {
        this.adresse = value;
    }

}
