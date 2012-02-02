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
import fr.ybo.moteurcsv.adapter.AdapterBoolean;
import fr.ybo.moteurcsv.adapter.AdapterInteger;
import fr.ybo.moteurcsv.annotation.BaliseCsv;
import fr.ybo.moteurcsv.annotation.FichierCsv;

@FichierCsv("calendriers.txt")
@Entity
public class Calendrier {
    @BaliseCsv(value = "id", adapter = AdapterInteger.class)
    @Column(type = Column.TypeColumn.INTEGER)
    @PrimaryKey
    public Integer id;
    @BaliseCsv(value = "lundi", adapter = AdapterBoolean.class)
    @Column(type = Column.TypeColumn.BOOLEAN)
    public Boolean lundi;
    @BaliseCsv(value = "mardi", adapter = AdapterBoolean.class)
    @Column(type = Column.TypeColumn.BOOLEAN)
    public Boolean mardi;
    @BaliseCsv(value = "mercredi", adapter = AdapterBoolean.class)
    @Column(type = Column.TypeColumn.BOOLEAN)
    public Boolean mercredi;
    @BaliseCsv(value = "jeudi", adapter = AdapterBoolean.class)
    @Column(type = Column.TypeColumn.BOOLEAN)
    public Boolean jeudi;
    @BaliseCsv(value = "vendredi", adapter = AdapterBoolean.class)
    @Column(type = Column.TypeColumn.BOOLEAN)
    public Boolean vendredi;
    @BaliseCsv(value = "samedi", adapter = AdapterBoolean.class)
    @Column(type = Column.TypeColumn.BOOLEAN)
    public Boolean samedi;
    @BaliseCsv(value = "dimanche", adapter = AdapterBoolean.class)
    @Column(type = Column.TypeColumn.BOOLEAN)
    public Boolean dimanche;
	@BaliseCsv(value = "dateDebut")
	@Column
	public String dateDebut;
	@BaliseCsv(value = "dateFin")
	@Column
	public String dateFin;
}
