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

package fr.ybo.transportsbordeauxhelper.modeletcb;


import fr.ybo.moteurcsv.adapter.AdapterBoolean;
import fr.ybo.moteurcsv.adapter.AdapterInteger;
import fr.ybo.moteurcsv.annotation.BaliseCsv;
import fr.ybo.moteurcsv.annotation.FichierCsv;

/**
 * Un calendrier.
 * @author ybonnel
 *
 */
@FichierCsv("calendriers.txt")
public class Calendrier {
	@BaliseCsv(value = "id", adapter = AdapterInteger.class, ordre = 0)
	public int id;
	@BaliseCsv(value = "lundi", adapter = AdapterBoolean.class, ordre = 1)
	public boolean lundi;
	@BaliseCsv(value = "mardi", adapter = AdapterBoolean.class, ordre = 2)
	public boolean mardi;
	@BaliseCsv(value = "mercredi", adapter = AdapterBoolean.class, ordre = 3)
	public boolean mercredi;
	@BaliseCsv(value = "jeudi", adapter = AdapterBoolean.class, ordre = 4)
	public boolean jeudi;
	@BaliseCsv(value = "vendredi", adapter = AdapterBoolean.class, ordre = 5)
	public boolean vendredi;
	@BaliseCsv(value = "samedi", adapter = AdapterBoolean.class, ordre = 6)
	public boolean samedi;
	@BaliseCsv(value = "dimanche", adapter = AdapterBoolean.class, ordre = 7)
	public boolean dimanche;
	
	
	
	public Calendrier(int pId, boolean pLundi, boolean pMardi,
			boolean pMercredi, boolean pJeudi, boolean pVendredi,
			boolean pSamedi, boolean pDimanche) {
		super();
		this.id = pId;
		this.lundi = pLundi;
		this.mardi = pMardi;
		this.mercredi = pMercredi;
		this.jeudi = pJeudi;
		this.vendredi = pVendredi;
		this.samedi = pSamedi;
		this.dimanche = pDimanche;
	}



	public static final Calendrier LUNDI =    new Calendrier(1, true, false, false, false, false, false, false);
	public static final Calendrier MARDI =    new Calendrier(2, false, true, false, false, false, false, false);
	public static final Calendrier MERCREDI = new Calendrier(3, false, false, true, false, false, false, false);
	public static final Calendrier JEUDI =    new Calendrier(4, false, false, false, true, false, false, false);
	public static final Calendrier VENDREDI = new Calendrier(5, false, false, false, false, true, false, false);
	public static final Calendrier SAMEDI =   new Calendrier(6, false, false, false, false, false, true, false);
	public static final Calendrier DIMANCHE = new Calendrier(7, false, false, false, false, false, false, true);
}
