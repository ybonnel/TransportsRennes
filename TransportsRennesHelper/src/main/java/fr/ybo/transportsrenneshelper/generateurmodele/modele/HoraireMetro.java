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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import fr.ybo.transportsrenneshelper.gtfs.modele.StopTime;
import fr.ybo.transportsrenneshelper.gtfs.modele.Trip;
import fr.ybonnel.csvengine.adapter.AdapterTime;
import fr.ybonnel.csvengine.annotation.CsvColumn;
import fr.ybonnel.csvengine.annotation.CsvFile;


/**
 * Horaires du métro.
 */
@SuppressWarnings("serial")
@CsvFile
public class HoraireMetro implements Serializable {
	// CHECKSTYLE:OFF
	@CsvColumn(value = "POT1", adapter = AdapterTime.class)
	public Integer pot1;
	@CsvColumn(value = "BLO1", adapter = AdapterTime.class)
	public Integer blo1;
	@CsvColumn(value = "TRI1", adapter = AdapterTime.class)
	public Integer tri1;
	@CsvColumn(value = "ITA1", adapter = AdapterTime.class)
	public Integer ita1;
	@CsvColumn(value = "HFR1", adapter = AdapterTime.class)
	public Integer hfr1;
	@CsvColumn(value = "GCL1", adapter = AdapterTime.class)
	public Integer gcl1;
	@CsvColumn(value = "JCA1", adapter = AdapterTime.class)
	public Integer jca1;
	@CsvColumn(value = "GAR1", adapter = AdapterTime.class)
	public Integer gar1;
	@CsvColumn(value = "CDG1", adapter = AdapterTime.class)
	public Integer cdg1;
	@CsvColumn(value = "REP1", adapter = AdapterTime.class)
	public Integer rep1;
	@CsvColumn(value = "STA1", adapter = AdapterTime.class)
	public Integer sta1;
	@CsvColumn(value = "ANF1", adapter = AdapterTime.class)
	public Integer anf1;
	@CsvColumn(value = "PON1", adapter = AdapterTime.class)
	public Integer pon1;
	@CsvColumn(value = "VU1", adapter = AdapterTime.class)
	public Integer vu1;
	@CsvColumn(value = "JFK1", adapter = AdapterTime.class)
	public Integer jfk1;
	@CsvColumn(value = "JFK2", adapter = AdapterTime.class)
	public Integer jfk2;
	@CsvColumn(value = "VU2", adapter = AdapterTime.class)
	public Integer vu2;
	@CsvColumn(value = "PON2", adapter = AdapterTime.class)
	public Integer pon2;
	@CsvColumn(value = "ANF2", adapter = AdapterTime.class)
	public Integer anf2;
	@CsvColumn(value = "STA2", adapter = AdapterTime.class)
	public Integer sta2;
	@CsvColumn(value = "REP2", adapter = AdapterTime.class)
	public Integer rep2;
	@CsvColumn(value = "CDG2", adapter = AdapterTime.class)
	public Integer cdg2;
	@CsvColumn(value = "GAR2", adapter = AdapterTime.class)
	public Integer gar2;
	@CsvColumn(value = "JCA2", adapter = AdapterTime.class)
	public Integer jca2;
	@CsvColumn(value = "GCL2", adapter = AdapterTime.class)
	public Integer gcl2;
	@CsvColumn(value = "HFR2", adapter = AdapterTime.class)
	public Integer hfr2;
	@CsvColumn(value = "ITA2", adapter = AdapterTime.class)
	public Integer ita2;
	@CsvColumn(value = "TRI2", adapter = AdapterTime.class)
	public Integer tri2;
	@CsvColumn(value = "BLO2", adapter = AdapterTime.class)
	public Integer blo2;
	@CsvColumn(value = "POT2", adapter = AdapterTime.class)
	public Integer pot2;

	public List<Horaire> getHoraires(int trajetId, int calendrierId, int direction1Id, int direction2Id) {
		List<Horaire> horaires = new ArrayList<Horaire>();
		// Génération du trajet.
		Trajet trajet1 = new Trajet();
		trajet1.calendrierId = calendrierId;
		trajet1.directionId = direction1Id;
		trajet1.id = trajetId;
		trajet1.ligneId = "a";
		trajet1.macroDirection = 0;
		Trajet trajet2 = new Trajet();
		trajet2.calendrierId = calendrierId;
		trajet2.directionId = direction2Id;
		trajet2.id = trajetId + 1;
		trajet2.ligneId = "a";
		trajet2.macroDirection = 1;
		horaires.add(newHoraire("POT1", pot1, 1, false, trajet1));
		horaires.add(newHoraire("BLO1", blo1, 2, false, trajet1));
		horaires.add(newHoraire("TRI1", tri1, 3, false, trajet1));
		horaires.add(newHoraire("ITA1", ita1, 4, false, trajet1));
		horaires.add(newHoraire("HFR1", hfr1, 5, false, trajet1));
		horaires.add(newHoraire("GCL1", gcl1, 6, false, trajet1));
		horaires.add(newHoraire("JCA1", jca1, 7, false, trajet1));
		horaires.add(newHoraire("GAR1", gar1, 8, false, trajet1));
		horaires.add(newHoraire("CDG1", cdg1, 9, false, trajet1));
		horaires.add(newHoraire("REP1", rep1, 10, false, trajet1));
		horaires.add(newHoraire("STA1", sta1, 11, false, trajet1));
		horaires.add(newHoraire("ANF1", anf1, 12, false, trajet1));
		horaires.add(newHoraire("PON1", pon1, 13, false, trajet1));
		horaires.add(newHoraire("VU1", vu1, 14, false, trajet1));
		horaires.add(newHoraire("JFK1", jfk1, 15, true, trajet1));
		horaires.add(newHoraire("JFK2", jfk2, 1, false, trajet2));
		horaires.add(newHoraire("VU2", vu2, 2, false, trajet2));
		horaires.add(newHoraire("PON2", pon2, 3, false, trajet2));
		horaires.add(newHoraire("ANF2", anf2, 4, false, trajet2));
		horaires.add(newHoraire("STA2", sta2, 5, false, trajet2));
		horaires.add(newHoraire("REP2", rep2, 6, false, trajet2));
		horaires.add(newHoraire("CDG2", cdg2, 7, false, trajet2));
		horaires.add(newHoraire("GAR2", gar2, 8, false, trajet2));
		horaires.add(newHoraire("JCA2", jca2, 9, false, trajet2));
		horaires.add(newHoraire("GCL2", gcl2, 10, false, trajet2));
		horaires.add(newHoraire("HFR2", hfr2, 11, false, trajet2));
		horaires.add(newHoraire("ITA2", ita2, 12, false, trajet2));
		horaires.add(newHoraire("TRI2", tri2, 13, false, trajet2));
		horaires.add(newHoraire("BLO2", blo2, 14, false, trajet2));
		horaires.add(newHoraire("POT2", pot2, 15, true, trajet2));
		return horaires;
	}

	private Horaire newHoraire(String arretId, int heureDepart, int stopSequence, boolean terminus, Trajet trajet) {
		Horaire horaire = new Horaire();
		horaire.arretId = arretId;
		horaire.heureDepart = heureDepart;
		horaire.stopSequence = stopSequence;
		horaire.terminus = terminus;
		horaire.trajet = trajet;
		horaire.trajetId = trajet.id;
		return horaire;
	}

	public List<StopTime> getStopTime(int trajetId, int calendrierId, String headSign1, String headSign2) {
		List<StopTime> horaires = new ArrayList<StopTime>();
		// Génération du trajet.
		Trip trip1 = new Trip();
		trip1.routeId = "a";
		trip1.serviceId = Integer.toString(calendrierId);
		trip1.id = Integer.toString(trajetId);
		trip1.headSign = headSign1;
		trip1.directionId = 0;
		Trip trip2 = new Trip();
		trip2.routeId = "a";
		trip2.serviceId = Integer.toString(calendrierId);
		trip2.id = Integer.toString(trajetId+1);
		trip2.headSign = headSign2;
		trip2.directionId = 0;
		horaires.add(newStopTime("POT1", pot1, 1, trip1));
		horaires.add(newStopTime("BLO1", blo1, 2, trip1));
		horaires.add(newStopTime("TRI1", tri1, 3, trip1));
		horaires.add(newStopTime("ITA1", ita1, 4, trip1));
		horaires.add(newStopTime("HFR1", hfr1, 5, trip1));
		horaires.add(newStopTime("GCL1", gcl1, 6, trip1));
		horaires.add(newStopTime("JCA1", jca1, 7, trip1));
		horaires.add(newStopTime("GAR1", gar1, 8, trip1));
		horaires.add(newStopTime("CDG1", cdg1, 9, trip1));
		horaires.add(newStopTime("REP1", rep1, 10, trip1));
		horaires.add(newStopTime("STA1", sta1, 11, trip1));
		horaires.add(newStopTime("ANF1", anf1, 12, trip1));
		horaires.add(newStopTime("PON1", pon1, 13, trip1));
		horaires.add(newStopTime("VU1", vu1, 14, trip1));
		horaires.add(newStopTime("JFK1", jfk1, 15, trip1));
		horaires.add(newStopTime("JFK2", jfk2, 1, trip2));
		horaires.add(newStopTime("VU2", vu2, 2, trip2));
		horaires.add(newStopTime("PON2", pon2, 3, trip2));
		horaires.add(newStopTime("ANF2", anf2, 4, trip2));
		horaires.add(newStopTime("STA2", sta2, 5, trip2));
		horaires.add(newStopTime("REP2", rep2, 6, trip2));
		horaires.add(newStopTime("CDG2", cdg2, 7, trip2));
		horaires.add(newStopTime("GAR2", gar2, 8, trip2));
		horaires.add(newStopTime("JCA2", jca2, 9, trip2));
		horaires.add(newStopTime("GCL2", gcl2, 10, trip2));
		horaires.add(newStopTime("HFR2", hfr2, 11, trip2));
		horaires.add(newStopTime("ITA2", ita2, 12, trip2));
		horaires.add(newStopTime("TRI2", tri2, 13, trip2));
		horaires.add(newStopTime("BLO2", blo2, 14, trip2));
		horaires.add(newStopTime("POT2", pot2, 15, trip2));
		return horaires;
	}

	private StopTime newStopTime(String arretId, int heureDepart, int stopSequence, Trip trip) {
		StopTime stopTime = new StopTime();
		stopTime.tripId = trip.id;
		stopTime.trip = trip;
		stopTime.heureArrivee = heureDepart;
		stopTime.heureDepart = heureDepart;
		stopTime.stopId = arretId;
		stopTime.stopSequence = stopSequence;
		return stopTime;
	}


}
