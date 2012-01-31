/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package fr.ybo.transportscommun.donnees.modele;

import fr.ybo.database.annotation.Column;
import fr.ybo.database.annotation.Entity;
import fr.ybo.database.annotation.PrimaryKey;

@Entity
public class Notification {

    @Column
    @PrimaryKey
    private String ligneId;
    @Column
    @PrimaryKey
    private String arretId;
    @PrimaryKey
    @Column(type = Column.TypeColumn.INTEGER)
    private Integer heure;
    @Column(type = Column.TypeColumn.INTEGER)
    private Integer tempsAttente;
    @Column(type = Column.TypeColumn.INTEGER)
    public Integer macroDirection;
    @Column
    public String direction;

    public String getLigneId() {
        return ligneId;
    }

    public void setLigneId(String ligneId) {
        this.ligneId = ligneId;
    }

    public String getArretId() {
        return arretId;
    }

    public void setArretId(String arretId) {
        this.arretId = arretId;
    }

    public Integer getHeure() {
        return heure;
    }

    public void setHeure(Integer heure) {
        this.heure = heure;
    }

    public Integer getTempsAttente() {
        return tempsAttente;
    }

    public void setTempsAttente(Integer tempsAttente) {
        this.tempsAttente = tempsAttente;
    }

    public Integer getMacroDirection() {
        return macroDirection;
    }

    public void setMacroDirection(Integer macroDirection) {
        this.macroDirection = macroDirection;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}
