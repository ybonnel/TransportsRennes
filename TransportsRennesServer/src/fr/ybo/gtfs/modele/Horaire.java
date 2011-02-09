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

package fr.ybo.gtfs.modele;


import fr.ybo.gtfs.csv.annotation.BaliseCsv;
import fr.ybo.gtfs.csv.annotation.FichierCsv;
import fr.ybo.gtfs.csv.moteur.adapter.AdapterBoolean;
import fr.ybo.gtfs.csv.moteur.adapter.AdapterInteger;

@FichierCsv("horaires.txt")
public class Horaire {
	@BaliseCsv("arret_id")
	public String arretId;
	@BaliseCsv(value = "trajet_id", adapter = AdapterInteger.class)
	public Integer trajetId;
	@BaliseCsv(value = "heure_depart", adapter = AdapterInteger.class)
	public Integer heureDepart;
	@BaliseCsv(value = "stop_sequence", adapter = AdapterInteger.class)
	public Integer stopSequence;
	@BaliseCsv(value = "terminus", adapter = AdapterBoolean.class)
	public Boolean terminus;
}
