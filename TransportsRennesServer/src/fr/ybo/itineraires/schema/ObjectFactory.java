
package fr.ybo.itineraires.schema;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the fr.ybo.itineraires.schema package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: fr.ybo.itineraires.schema
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link JointureCorrespondance }
     * 
     */
    public JointureCorrespondance createJointureCorrespondance() {
        return new JointureCorrespondance();
    }

    /**
     * Create an instance of {@link PortionTrajetBus }
     * 
     */
    public PortionTrajetBus createPortionTrajetBus() {
        return new PortionTrajetBus();
    }

    /**
     * Create an instance of {@link ItineraireReponse }
     * 
     */
    public ItineraireReponse createItineraireReponse() {
        return new ItineraireReponse();
    }

    /**
     * Create an instance of {@link Adresse }
     * 
     */
    public Adresse createAdresse() {
        return new Adresse();
    }

    /**
     * Create an instance of {@link Trajet }
     * 
     */
    public Trajet createTrajet() {
        return new Trajet();
    }

    /**
     * Create an instance of {@link JointurePieton }
     * 
     */
    public JointurePieton createJointurePieton() {
        return new JointurePieton();
    }

}
