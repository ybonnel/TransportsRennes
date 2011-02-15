
package fr.ybo.itineraires.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="erreur" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="adresseDepart" type="{}Adresse" minOccurs="0"/>
 *         &lt;element name="adresseArrivee" type="{}Adresse" minOccurs="0"/>
 *         &lt;element name="trajets" type="{}Trajet" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "erreur",
    "adresseDepart",
    "adresseArrivee",
    "trajets"
})
@XmlRootElement(name = "ItineraireReponse")
public class ItineraireReponse {

    protected String erreur;
    protected Adresse adresseDepart;
    protected Adresse adresseArrivee;
    protected List<Trajet> trajets;

    /**
     * Gets the value of the erreur property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getErreur() {
        return erreur;
    }

    /**
     * Sets the value of the erreur property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setErreur(final String value) {
	    erreur = value;
    }

    /**
     * Gets the value of the adresseDepart property.
     * 
     * @return
     *     possible object is
     *     {@link Adresse }
     *     
     */
    public Adresse getAdresseDepart() {
        return adresseDepart;
    }

    /**
     * Sets the value of the adresseDepart property.
     * 
     * @param value
     *     allowed object is
     *     {@link Adresse }
     *     
     */
    public void setAdresseDepart(final Adresse value) {
	    adresseDepart = value;
    }

    /**
     * Gets the value of the adresseArrivee property.
     * 
     * @return
     *     possible object is
     *     {@link Adresse }
     *     
     */
    public Adresse getAdresseArrivee() {
        return adresseArrivee;
    }

    /**
     * Sets the value of the adresseArrivee property.
     * 
     * @param value
     *     allowed object is
     *     {@link Adresse }
     *     
     */
    public void setAdresseArrivee(final Adresse value) {
	    adresseArrivee = value;
    }

    /**
     * Gets the value of the trajets property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the trajets property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTrajets().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Trajet }
     * 
     * 
     */
    public Collection<Trajet> getTrajets() {
        if (trajets == null) {
            trajets = new ArrayList<Trajet>();
        }
        return trajets;
    }

}
