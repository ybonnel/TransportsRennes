/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
import fr.ybo.transportsrennes.keolis.gtfs.moteur.adapter.AdapterInteger;

@FichierCsv("trajets.txt")
@Table
public class Trajet {
	@BaliseCsv(value = "id", adapter = AdapterInteger.class)
	@Colonne(type = Colonne.TypeColonne.INTEGER)
	@PrimaryKey
	public Integer id;
	@BaliseCsv(value = "calendrier_id", adapter = AdapterInteger.class)
	@Colonne(type = Colonne.TypeColonne.INTEGER)
	public Integer calendrierId;
	@BaliseCsv("ligne_id")
	@Colonne
	public String ligneId;
	@BaliseCsv(value = "direction_id", adapter = AdapterInteger.class)
	@Colonne(type = Colonne.TypeColonne.INTEGER)
	public Integer directionId;
}
