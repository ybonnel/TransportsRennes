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
import fr.ybo.transportsrennes.keolis.gtfs.moteur.adapter.AdapterDouble;
import fr.ybo.transportsrennes.keolis.modele.ObjetWithDistance;

import java.io.Serializable;

@SuppressWarnings({"serial"})
@FichierCsv("arrets.txt")
@Table
public class Arret extends ObjetWithDistance implements Serializable {
	@BaliseCsv("id")
	@Colonne
	@PrimaryKey
	public String id;
	@BaliseCsv("nom")
	@Colonne
	public String nom;
	@BaliseCsv(value = "latitude", adapter = AdapterDouble.class)
	@Colonne(type = Colonne.TypeColonne.NUMERIC)
	public Double latitude;
	@BaliseCsv(value = "longitude", adapter = AdapterDouble.class)
	@Colonne(type = Colonne.TypeColonne.NUMERIC)
	public Double longitude;

	public ArretFavori favori;

	@Override
	public double getLatitude() {
		return latitude;
	}

	@Override
	public double getLongitude() {
		return longitude;
	}
}
