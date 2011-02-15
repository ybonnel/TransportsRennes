
package fr.ybo.itineraires.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PortionTrajet complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PortionTrajet">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@SuppressWarnings({"ClassMayBeInterface", "EmptyClass"})
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PortionTrajet")
@XmlSeeAlso({
    PortionTrajetBus.class,
    PortionTrajetPieton.class
})
public abstract class PortionTrajet {


}
