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

import java.io.Serializable;

import fr.ybo.database.annotation.Column;
import fr.ybo.database.annotation.Entity;
import fr.ybo.database.annotation.PrimaryKey;
import fr.ybo.moteurcsv.adapter.AdapterInteger;
import fr.ybo.moteurcsv.annotation.BaliseCsv;
import fr.ybo.moteurcsv.annotation.FichierCsv;

@SuppressWarnings("serial")
@Entity
@FichierCsv("arrets_favoris.txt")
public class ArretFavori implements Serializable {
	@Column
	@PrimaryKey
	@BaliseCsv(value = "arret_id", ordre = 1)
	public String arretId;
	@Column
	@PrimaryKey
	@BaliseCsv(value = "ligne_id", ordre = 2)
	public String ligneId;
	@Column(type = Column.TypeColumn.INTEGER)
	@PrimaryKey
	@BaliseCsv(value = "macro_direction", adapter = AdapterInteger.class)
	public Integer macroDirection;
	@Column
	public String nomArret;
	@Column
	public String direction;
	@Column
	public String nomCourt;
	@Column
	public String nomLong;
	@Column(type = Column.TypeColumn.INTEGER)
	public Integer ordre;
	@Column
	@BaliseCsv(value = "groupe")
	public String groupe;
}
