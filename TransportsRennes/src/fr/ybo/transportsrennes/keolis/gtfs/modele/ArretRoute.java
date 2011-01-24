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

package fr.ybo.transportsrennes.keolis.gtfs.modele;


import fr.ybo.transportsrennes.keolis.gtfs.annotation.BaliseCsv;
import fr.ybo.transportsrennes.keolis.gtfs.annotation.Colonne;
import fr.ybo.transportsrennes.keolis.gtfs.annotation.FichierCsv;
import fr.ybo.transportsrennes.keolis.gtfs.annotation.PrimaryKey;
import fr.ybo.transportsrennes.keolis.gtfs.annotation.Table;
import fr.ybo.transportsrennes.keolis.gtfs.moteur.adapter.AdapterBoolean;
import fr.ybo.transportsrennes.keolis.gtfs.moteur.adapter.AdapterInteger;

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
	@BaliseCsv(value = "direction_id", adapter = AdapterInteger.class)
	@Colonne(type = Colonne.TypeColonne.INTEGER)
	public Integer directionId;
	@BaliseCsv(value = "sequence", adapter = AdapterInteger.class)
	@Colonne(type = Colonne.TypeColonne.INTEGER)
	public Integer sequence;
	@BaliseCsv(value = "accessible", adapter = AdapterBoolean.class)
	@Colonne(type = Colonne.TypeColonne.BOOLEAN)
	public Boolean accessible;

}
