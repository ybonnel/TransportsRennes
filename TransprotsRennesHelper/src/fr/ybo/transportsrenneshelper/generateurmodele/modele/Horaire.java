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

package fr.ybo.transportsrenneshelper.generateurmodele.modele;


import fr.ybo.transportsrenneshelper.annotation.BaliseCsv;
import fr.ybo.transportsrenneshelper.annotation.FichierCsv;
import fr.ybo.transportsrenneshelper.moteurcsv.adapter.AdapterBoolean;
import fr.ybo.transportsrenneshelper.moteurcsv.adapter.AdapterInteger;

@SuppressWarnings("UnusedDeclaration")
@FichierCsv("horaires.txt")
public class Horaire {
	@BaliseCsv("arret_id")
	public String arretId;
	@BaliseCsv(value = "trajet_id", adapter = AdapterInteger.class)
	public int trajetId;
	@BaliseCsv(value = "heure_depart", adapter = AdapterInteger.class)
	public int heureDepart;
	@BaliseCsv(value = "stop_sequence", adapter = AdapterInteger.class)
	public int stopSequence;
	@BaliseCsv(value = "terminus", adapter = AdapterBoolean.class)
	public boolean terminus;
}
