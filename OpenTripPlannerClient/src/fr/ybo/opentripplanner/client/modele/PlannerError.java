/* This program is free software: you can redistribute it and/or
   modify it under the terms of the GNU Lesser General Public License
   as published by the Free Software Foundation, either version 3 of
   the License, or (at your option) any later version.
   
   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.
   
   You should have received a copy of the GNU General Public License
   along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 */

package fr.ybo.opentripplanner.client.modele;


/**
 * This represents an error in trip planning.
 *
 */
public class PlannerError {

    private int    id;
    private String msg;

    private boolean noPath = false;

    /** An error where no path has been found, but no points are missing */
    public PlannerError() {
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * @param noPath whether no path has been found
     */
    public void setNoPath(boolean noPath) {
        this.noPath = noPath;
    }

    /**
     * @return whether no path has been found
     */
    public boolean getNoPath() {
        return noPath;
    }
}
