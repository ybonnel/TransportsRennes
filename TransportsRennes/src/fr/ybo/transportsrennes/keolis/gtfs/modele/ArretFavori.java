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
package fr.ybo.transportsrennes.keolis.gtfs.modele;

import java.io.Serializable;

import fr.ybo.database.annotation.Column;
import fr.ybo.database.annotation.Entity;
import fr.ybo.database.annotation.PrimaryKey;

@SuppressWarnings("serial")
@Entity
public class ArretFavori implements Serializable {
	@Column
	@PrimaryKey
	public String arretId;
	@Column
	@PrimaryKey
	public String ligneId;
	@Column(type = Column.TypeColumn.INTEGER)
	@PrimaryKey
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
	@Column()
	public String groupe;
}
