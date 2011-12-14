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
 * 
 * Contributors:
 *     ybonnel - initial API and implementation
 */
package fr.ybo.transportsrenneshelper.generateurmodele.modele;


import fr.ybo.moteurcsv.adapter.AdapterBoolean;
import fr.ybo.moteurcsv.adapter.AdapterInteger;
import fr.ybo.moteurcsv.annotation.BaliseCsv;
import fr.ybo.moteurcsv.annotation.FichierCsv;

/**
 * Horaire.
 * @author ybonnel
 *
 */
@FichierCsv("horaires.txt")
public class Horaire {
	// CHECKSTYLE:OFF
	@BaliseCsv(value = "arret_id", ordre = 0)
	public String arretId;
	@BaliseCsv(value = "trajet_id", adapter = AdapterInteger.class, ordre = 1)
	public int trajetId;
	@BaliseCsv(value = "heure_depart", adapter = AdapterInteger.class, ordre = 2)
	public int heureDepart;
	@BaliseCsv(value = "stop_sequence", adapter = AdapterInteger.class, ordre = 3)
	public int stopSequence;
	@BaliseCsv(value = "terminus", adapter = AdapterBoolean.class, ordre = 4)
	public boolean terminus;

	public Trajet trajet;
}
