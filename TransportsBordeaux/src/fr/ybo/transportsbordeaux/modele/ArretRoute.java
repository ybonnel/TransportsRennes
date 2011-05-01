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

package fr.ybo.transportsbordeaux.modele;


import fr.ybo.moteurcsv.adapter.AdapterInteger;
import fr.ybo.moteurcsv.annotation.BaliseCsv;
import fr.ybo.moteurcsv.annotation.FichierCsv;
import fr.ybo.transportsbordeaux.database.annotation.Colonne;
import fr.ybo.transportsbordeaux.database.annotation.PrimaryKey;
import fr.ybo.transportsbordeaux.database.annotation.Table;

@FichierCsv("arrets_routes.txt")
@Table
public class ArretRoute {
	@BaliseCsv("arret_id")
	@Colonne
	@PrimaryKey
	public String arretId;
	@BaliseCsv("ligne_id")
	@Colonne
	@PrimaryKey
	public String ligneId;
	@BaliseCsv( value = "macro_direction", adapter = AdapterInteger.class, ordre = 5)
	@Colonne(type = Colonne.TypeColonne.INTEGER)
	@PrimaryKey
	public Integer macroDirection;
	@BaliseCsv(value = "direction_id", adapter = AdapterInteger.class)
	@Colonne(type = Colonne.TypeColonne.INTEGER)
	public Integer directionId;
	@BaliseCsv(value = "sequence", adapter = AdapterInteger.class)
	@Colonne(type = Colonne.TypeColonne.INTEGER)
	public Integer sequence;

}
