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
package fr.ybo.transportsrenneshelper.keolis.modele;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Réponse Kéolis.
 *
 * @param <ObjectKeolis> type d'objet kéolis.
 * @author ybonnel
 */
public class Answer<ObjectKeolis> {

	/**
	 * Status.
	 */
	private StatusKeolis status;
	/**
	 * Liste d'objet Keolis.
	 */
	private List<ObjectKeolis> data;

	/**
	 * @return les liste d'objet Keolis.
	 */
	public Collection<ObjectKeolis> getData() {
		if (data == null) {
			data = new ArrayList<ObjectKeolis>();
		}
		return data;
	}

	/**
	 * Getter.
	 *
	 * @return le status.
	 */
	public StatusKeolis getStatus() {
		return status;
	}

	/**
	 * Setter.
	 *
	 * @param pStatus le status.
	 */
	public void setStatus(StatusKeolis pStatus) {
		status = pStatus;
	}
}
