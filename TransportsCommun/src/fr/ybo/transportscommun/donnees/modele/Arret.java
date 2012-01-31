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
import fr.ybo.moteurcsv.adapter.AdapterDouble;
import fr.ybo.moteurcsv.annotation.BaliseCsv;
import fr.ybo.moteurcsv.annotation.FichierCsv;
import fr.ybo.transportscommun.AbstractTransportsApplication;

@SuppressWarnings({"serial"})
@FichierCsv("arrets.txt")
@Entity
public class Arret extends ObjetWithDistance implements Serializable {
    @BaliseCsv("id")
    @Column
    @PrimaryKey
    public String id;
    @BaliseCsv("nom")
    @Column
    public String nom;
    @BaliseCsv(value = "latitude", adapter = AdapterDouble.class)
    @Column(type = Column.TypeColumn.NUMERIC)
    public Double latitude;
    @BaliseCsv(value = "longitude", adapter = AdapterDouble.class)
    @Column(type = Column.TypeColumn.NUMERIC)
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

    public static Arret getArret(String arretId) {
        Arret arret = new Arret();
        arret.id = arretId;
		return AbstractTransportsApplication.getDataBaseHelper().selectSingle(arret);
    }
}
