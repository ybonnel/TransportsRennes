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
import fr.ybo.database.annotation.Column.TypeColumn;
import fr.ybo.database.annotation.Entity;
import fr.ybo.database.annotation.PrimaryKey;
import fr.ybo.moteurcsv.adapter.AdapterBoolean;
import fr.ybo.moteurcsv.adapter.AdapterInteger;
import fr.ybo.moteurcsv.annotation.BaliseCsv;
import fr.ybo.moteurcsv.annotation.FichierCsv;

@FichierCsv("calendriers_exceptions.txt")
@Entity
public class CalendrierException {
    @BaliseCsv(value = "calendrier_id", adapter = AdapterInteger.class)
    @Column(type = TypeColumn.INTEGER)
    @PrimaryKey
    public Integer calendrierId;
    @BaliseCsv(value = "date")
    @Column
    @PrimaryKey
    public String date;
    @BaliseCsv(value = "ajout", adapter = AdapterBoolean.class)
    @Column(type = TypeColumn.BOOLEAN)
    public Boolean ajout;

}
