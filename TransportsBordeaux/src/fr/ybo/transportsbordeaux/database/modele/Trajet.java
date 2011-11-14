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
package fr.ybo.transportsbordeaux.database.modele;

import fr.ybo.database.annotation.Column;
import fr.ybo.database.annotation.Column.TypeColumn;
import fr.ybo.database.annotation.Entity;
import fr.ybo.database.annotation.PrimaryKey;
import fr.ybo.moteurcsv.adapter.AdapterInteger;
import fr.ybo.moteurcsv.annotation.BaliseCsv;
import fr.ybo.moteurcsv.annotation.FichierCsv;

/**
 * Représente un trajet de bus.
 *
 * @author ybonnel
 */
@FichierCsv("trajets.txt")
@Entity
public class Trajet {
    // CHECKSTYLE:OFF
    @BaliseCsv(value = "id", adapter = AdapterInteger.class)
    @Column(type = TypeColumn.INTEGER)
    @PrimaryKey
    public Integer id;
    @BaliseCsv(value = "calendrier_id", adapter = AdapterInteger.class)
    @Column(type = TypeColumn.INTEGER)
    public Integer calendrierId;
    @BaliseCsv("ligne_id")
    @Column
    public String ligneId;
    @BaliseCsv(value = "direction_id", adapter = AdapterInteger.class)
    @Column(type = TypeColumn.INTEGER)
    public Integer directionId;
}
