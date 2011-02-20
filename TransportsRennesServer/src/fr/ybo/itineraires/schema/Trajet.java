
package fr.ybo.itineraires.schema;

import java.util.ArrayList;
import java.util.List;

public class Trajet {

    protected List<PortionTrajet> portions;

    public List<PortionTrajet> getPortions() {
        if (portions == null) {
            portions = new ArrayList<PortionTrajet>();
        }
        return portions;
    }

}
