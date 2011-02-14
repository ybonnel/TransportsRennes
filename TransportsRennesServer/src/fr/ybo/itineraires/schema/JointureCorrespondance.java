
package fr.ybo.itineraires.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for JointureCorrespondance complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="JointureCorrespondance">
 *   &lt;complexContent>
 *     &lt;extension base="{}PortionTrajetPieton">
 *       &lt;sequence>
 *         &lt;element name="arretDepartId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="arretArriveeId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "JointureCorrespondance", propOrder = {
    "arretDepartId",
    "arretArriveeId"
})
public class JointureCorrespondance
    extends PortionTrajetPieton
{

    @XmlElement(required = true)
    protected String arretDepartId;
    @XmlElement(required = true)
    protected String arretArriveeId;

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
    public void setArretDepartId(String value) {
        this.arretDepartId = value;
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
    public void setArretArriveeId(String value) {
        this.arretArriveeId = value;
    }

}
