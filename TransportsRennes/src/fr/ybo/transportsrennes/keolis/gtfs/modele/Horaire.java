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


import fr.ybo.moteurcsv.adapter.AdapterBoolean;
import fr.ybo.moteurcsv.adapter.AdapterInteger;
import fr.ybo.moteurcsv.annotation.BaliseCsv;
import fr.ybo.moteurcsv.annotation.FichierCsv;
import fr.ybo.transportsrennes.keolis.gtfs.annotation.Colonne;
import fr.ybo.transportsrennes.keolis.gtfs.annotation.PrimaryKey;
import fr.ybo.transportsrennes.keolis.gtfs.annotation.Table;

@FichierCsv("horaires.txt")
@Table
public class Horaire {
	@BaliseCsv("arret_id")
	@Colonne
	@PrimaryKey
	public String arretId;
	@BaliseCsv(value = "trajet_id", adapter = AdapterInteger.class)
	@Colonne(type = Colonne.TypeColonne.INTEGER)
	@PrimaryKey
	public Integer trajetId;
	@BaliseCsv(value = "heure_depart", adapter = AdapterInteger.class)
	@Colonne(type = Colonne.TypeColonne.INTEGER)
	public Integer heureDepart;
	@BaliseCsv(value = "stop_sequence", adapter = AdapterInteger.class)
	@Colonne(type = Colonne.TypeColonne.INTEGER)
	public Integer stopSequence;
	@BaliseCsv(value = "terminus", adapter = AdapterBoolean.class)
	@Colonne(type = Colonne.TypeColonne.BOOLEAN)
	public Boolean terminus;
}
