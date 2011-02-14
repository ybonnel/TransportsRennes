
package fr.ybo.itineraires.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PortionTrajetPieton complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PortionTrajetPieton">
 *   &lt;complexContent>
 *     &lt;extension base="{}PortionTrajet">
 *       &lt;sequence>
 *         &lt;element name="tempsTrajet" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PortionTrajetPieton", propOrder = {
    "tempsTrajet"
})
@XmlSeeAlso({
    JointurePieton.class,
    JointureCorrespondance.class
})
public abstract class PortionTrajetPieton
    extends PortionTrajet
{

    protected int tempsTrajet;

    /**
     * Gets the value of the tempsTrajet property.
     * 
     */
    public int getTempsTrajet() {
        return tempsTrajet;
    }

    /**
     * Sets the value of the tempsTrajet property.
     * 
     */
    public void setTempsTrajet(int value) {
        this.tempsTrajet = value;
    }

}
