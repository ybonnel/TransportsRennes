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

import java.io.Serializable;

import fr.ybo.database.annotation.Column;
import fr.ybo.database.annotation.Entity;
import fr.ybo.database.annotation.PrimaryKey;
import fr.ybo.moteurcsv.adapter.AdapterInteger;
import fr.ybo.moteurcsv.annotation.BaliseCsv;
import fr.ybo.moteurcsv.annotation.FichierCsv;
import fr.ybo.transportsbordeaux.TransportsBordeauxApplication;

@SuppressWarnings({"serial"})
@FichierCsv("lignes.txt")
@Entity
public class Ligne implements Serializable {

	@BaliseCsv("id")
	@Column
	@PrimaryKey
	public String id;
	@BaliseCsv("nom_court")
	@Column
	public String nomCourt;
	@BaliseCsv("nom_long")
	@Column
	public String nomLong;
	@BaliseCsv(value = "ordre", adapter = AdapterInteger.class)
	@Column(type = Column.TypeColumn.INTEGER)
	public Integer ordre;

    public static Ligne getLigne(String ligneId) {
        Ligne ligne = new Ligne();
        ligne.id = ligneId;
		return TransportsBordeauxApplication.getDataBaseHelper().selectSingle(ligne);
    }
}
