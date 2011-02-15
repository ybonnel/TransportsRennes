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
import fr.ybo.transportsrennes.keolis.gtfs.database.DataBaseHelper;
import fr.ybo.transportsrennes.keolis.gtfs.files.GestionZipKeolis;
import fr.ybo.transportsrennes.keolis.gtfs.moteur.MoteurCsv;
import fr.ybo.transportsrennes.keolis.gtfs.moteur.adapter.AdapterInteger;
import fr.ybo.transportsrennes.util.LogYbo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"UnusedDeclaration", "serial"})
@FichierCsv("lignes.txt")
@Table
public class Ligne implements Serializable {

	private static final LogYbo LOG_YBO = new LogYbo(Ligne.class);

	@BaliseCsv("id")
	@Colonne
	@PrimaryKey
	public String id;
	@BaliseCsv("nom_court")
	@Colonne
	public String nomCourt;
	@BaliseCsv("nom_long")
	@Colonne
	public String nomLong;
	@BaliseCsv(value = "ordre", adapter = AdapterInteger.class)
	@Colonne(type = Colonne.TypeColonne.INTEGER)
	public Integer ordre;
	@Colonne(type = Colonne.TypeColonne.BOOLEAN)
	public Boolean chargee;

	public void chargerHeuresArrets(DataBaseHelper dataBaseHelper) {
		LOG_YBO.debug("Chargement des horaires de la ligne " + nomCourt);
		List<Class<?>> classes = new ArrayList<Class<?>>(1000);
		classes.add(Horaire.class);
		MoteurCsv moteur = new MoteurCsv(classes);
		GestionZipKeolis.chargeLigne(moteur, id, dataBaseHelper);
		LOG_YBO.debug("Chargement des horaires de la ligne " + nomCourt + " terminé.");
	}
}
