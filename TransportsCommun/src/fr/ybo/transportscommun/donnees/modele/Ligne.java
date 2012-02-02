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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.res.Resources;
import android.database.Cursor;
import fr.ybo.database.DataBaseHelper;
import fr.ybo.database.annotation.Column;
import fr.ybo.database.annotation.Entity;
import fr.ybo.database.annotation.PrimaryKey;
import fr.ybo.database.modele.Table;
import fr.ybo.moteurcsv.MoteurCsv;
import fr.ybo.moteurcsv.adapter.AdapterInteger;
import fr.ybo.moteurcsv.annotation.BaliseCsv;
import fr.ybo.moteurcsv.annotation.FichierCsv;
import fr.ybo.transportscommun.AbstractTransportsApplication;
import fr.ybo.transportscommun.donnees.manager.LigneInexistanteException;
import fr.ybo.transportscommun.donnees.manager.gtfs.GestionZipKeolis;
import fr.ybo.transportscommun.util.LogYbo;
import fr.ybo.transportscommun.util.NoSpaceLeftException;

@SuppressWarnings({"serial"})
@FichierCsv("lignes.txt")
@Entity
public class Ligne implements Serializable {

    private static final LogYbo LOG_YBO = new LogYbo(Ligne.class);

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
    @Column(type = Column.TypeColumn.BOOLEAN)
    public Boolean chargee;

	public void chargerHeuresArrets(Class<?> rawClass, DataBaseHelper dataBaseHelper, Resources resources)
			throws LigneInexistanteException, NoSpaceLeftException {
        LOG_YBO.debug("Chargement des horaires de la ligne " + nomCourt);
        List<Class<?>> classes = new ArrayList<Class<?>>(1000);
        classes.add(Horaire.class);
        MoteurCsv moteur = new MoteurCsv(classes);
		GestionZipKeolis.chargeLigne(rawClass, moteur, id, dataBaseHelper, resources);
        LOG_YBO.debug("Chargement des horaires de la ligne " + nomCourt + " terminé.");
    }

    public static Ligne getLigne(String ligneId) {
        Ligne ligne = new Ligne();
        ligne.id = ligneId;
		return AbstractTransportsApplication.getDataBaseHelper().selectSingle(ligne);
    }

    public boolean isChargee() {
        if (chargee == null || !chargee) {
            return false;
        }
        // On regarde si la table existe.
		Table table = AbstractTransportsApplication.getDataBaseHelper().getBase().getTable(Horaire.class);
        table.addSuffixeToTableName(id);
		Cursor cursor = AbstractTransportsApplication
                .getDataBaseHelper()
                .getReadableDatabase()
                .query("sqlite_master",
                        Collections.singleton("name").toArray(new String[1]),
                        " type = 'table' and name='" + table.getName() + "'",
                        null, null, null, null);
        boolean retour = cursor.getCount() > 0;
        cursor.close();
        return retour;
    }
}
