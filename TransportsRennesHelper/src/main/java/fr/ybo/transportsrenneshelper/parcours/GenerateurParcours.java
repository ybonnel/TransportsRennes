package fr.ybo.transportsrenneshelper.parcours;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;

import fr.ybo.transportsrenneshelper.gtfs.gestionnaire.GestionnaireGtfs;
import fr.ybo.transportsrenneshelper.gtfs.modele.Route;
import fr.ybo.transportsrenneshelper.gtfs.modele.Stop;
import fr.ybo.transportsrenneshelper.gtfs.modele.StopTime;
import fr.ybo.transportsrenneshelper.gtfs.modele.Trip;
import fr.ybo.transportsrenneshelper.parcours.modele.Parcour;

public class GenerateurParcours {
	
	private List<Parcour> parcours = new ArrayList<Parcour>();

	@SuppressWarnings("unused")
	private static class ArretParcours {
		public String stopId;
		public String routeId;
		public Integer macroDirection;
		public String headSign;
		public Integer stopSequence;
	}
	
	public String getParcours() {
		Gson gson = new Gson();
		return gson.toJson(parcours);
	}
	
	public void genereParcours() {
		
		Map<String, List<StopTime>> mapHorairesByTripId = new HashMap<String, List<StopTime>>();
        for (StopTime stopTime : GestionnaireGtfs.getInstance().getMapStopTimes().values()) {
            if (!mapHorairesByTripId.containsKey(stopTime.tripId)) {
            	mapHorairesByTripId.put(stopTime.tripId, new ArrayList<StopTime>());
            }
            mapHorairesByTripId.get(stopTime.tripId).add(stopTime);
        }
        // Trip des horaires.
        for (List<StopTime> horairesATrier : mapHorairesByTripId.values()) {
            Collections.sort(horairesATrier, new Comparator<StopTime>() {
                public int compare(StopTime o1, StopTime o2) {
                    return o1.stopSequence < o2.stopSequence ? -1 : o1.stopSequence == o2.stopSequence ? 0 : 1;
                }
            });
        }
        
        Map<String, List<Trip>> tripByRouteId = new HashMap<String, List<Trip>>();
        for (Trip trip : GestionnaireGtfs.getInstance().getMapTrips().values()) {
        	if (!tripByRouteId.containsKey(trip.routeId)) {
        		tripByRouteId.put(trip.routeId, new ArrayList<Trip>());
        	}
        	tripByRouteId.get(trip.routeId).add(trip);
        }
        
        for (Route route : GestionnaireGtfs.getInstance().getMapRoutes().values()) {
            Map<String, Integer> countByChaine = new HashMap<String, Integer>();
            Map<String, List<Trip>> mapTrajetChaine = new HashMap<String, List<Trip>>();
            Map<String, Stop> arretOfLigne = new HashMap<String, Stop>();
            Map<String, Integer> macroDirectionsParChaine = new HashMap<String, Integer>();
            
            // Parcours des trajets.
            for (Trip trip : tripByRouteId.get(route.id)) {
                StringBuilder chaineBuilder = new StringBuilder();
                for (StopTime stopTime : mapHorairesByTripId.get(trip.id)) {
                    if (!arretOfLigne.containsKey(stopTime.stopId)) {
                        arretOfLigne.put(stopTime.stopId, GestionnaireGtfs.getInstance().getMapStops().get(stopTime.stopId));
                    }
                    chaineBuilder.append(stopTime.stopId);
                    chaineBuilder.append(',');
                }
                if (!countByChaine.containsKey(chaineBuilder.toString())) {
                    countByChaine.put(chaineBuilder.toString(), 0);
                    mapTrajetChaine.put(chaineBuilder.toString(), new ArrayList<Trip>());
                    macroDirectionsParChaine.put(chaineBuilder.toString(), trip.directionId);
                }
                countByChaine.put(chaineBuilder.toString(), countByChaine.get(chaineBuilder.toString()) + 1);
                mapTrajetChaine.get(chaineBuilder.toString()).add(trip);
            }
            Map<Integer, List<ArretParcours>> mapArretParcours = new HashMap<Integer, List<ArretParcours>>();
            // parcours des arrêts
            for (Stop stop : arretOfLigne.values()) {
                // Recherche du trajet adéquat.
                Map<Integer, String> mapMacroDirectionChaine = new HashMap<Integer, String>();
                Map<Integer, Integer> mapMacroDirectionMax = new HashMap<Integer, Integer>();
                for (Map.Entry<String, Integer> entryChaineCount : countByChaine.entrySet()) {
                    if (entryChaineCount.getKey().startsWith(stop.id + ',')
                            || entryChaineCount.getKey()
                            .contains(',' + stop.id + ',')) {
                        // Chemin trouvé
                        Integer macroDirection = macroDirectionsParChaine.get(entryChaineCount.getKey());
                        if (!mapMacroDirectionMax.containsKey(macroDirection)) {
                            mapMacroDirectionMax.put(macroDirection, 0);
                        }
                        if (entryChaineCount.getValue() > mapMacroDirectionMax.get(macroDirection)) {
                            mapMacroDirectionMax.put(macroDirection, entryChaineCount.getValue());
                            mapMacroDirectionChaine.put(macroDirection, entryChaineCount.getKey());
                        }
                    }
                }
                for (Entry<Integer, String> entryMacroDirectionChaine : mapMacroDirectionChaine.entrySet()) {
                	ArretParcours arretParcours = new ArretParcours();
                	arretParcours.stopId = stop.id;
                	arretParcours.routeId = route.id;
                	arretParcours.macroDirection = entryMacroDirectionChaine.getKey();
                    String[] champs = entryMacroDirectionChaine.getValue().split(",");
                    int sequence = 1;
                    for (String champ : champs) {
                        if (champ.equals(stop.id)) {
                            break;
                        }
                        sequence++;
                    }
                    arretParcours.stopSequence = sequence;
                    Map<String, Integer> countDirection = new HashMap<String, Integer>();
                    for (Trip trip : mapTrajetChaine.get(entryMacroDirectionChaine.getValue())) {
                        if (!countDirection.containsKey(trip.headSign)) {
                        	countDirection.put(trip.headSign, 0);
                        }
                        countDirection.put(trip.headSign, countDirection.get(trip.headSign) + 1);
                    }
                    int directionCount = 0;
                    String headSign = null;
                    for (Map.Entry<String, Integer> entryDirectionIdCount : countDirection.entrySet()) {
                        if (entryDirectionIdCount.getValue() > directionCount) {
                        	headSign = entryDirectionIdCount.getKey();
                            directionCount = entryDirectionIdCount.getValue();
                        }
                    }
                    
                    arretParcours.headSign = headSign;
                    if (!mapArretParcours.containsKey(arretParcours.macroDirection)) {
                    	mapArretParcours.put(arretParcours.macroDirection, new ArrayList<ArretParcours>());
                    }
                    mapArretParcours.get(arretParcours.macroDirection).add(arretParcours);
                }
            }
            
            for (Entry<Integer, List<ArretParcours>> entry : mapArretParcours.entrySet()) {
            	
            	Collections.sort(entry.getValue(), new Comparator<ArretParcours>() {
					@Override
					public int compare(ArretParcours o1, ArretParcours o2) {
	                    return o1.stopSequence < o2.stopSequence ? -1 : o1.stopSequence == o2.stopSequence ? 0 : 1;
					}
				});
            	
            	Parcour parcour = new Parcour();
            	parcour.setLigneId(route.id);
            	parcour.setLigneName(route.nomCourt);
            	parcour.setMacroDirection(entry.getKey());
            	Map<String, Integer> countHeadSign = new HashMap<String, Integer>();
            	
            	for (ArretParcours arretParcours : entry.getValue()) {
            		Parcour.Arret arret = new Parcour.Arret();
            		arret.setStopId(arretParcours.stopId);
            		arret.setStopSequence(arretParcours.stopSequence);
            		arret.setStopName(GestionnaireGtfs.getInstance().getMapStops().get(arretParcours.stopId).nom);
            		parcour.getStops().add(arret);
            		if (!countHeadSign.containsKey(arretParcours.headSign)) {
            			countHeadSign.put(arretParcours.headSign, 0);
            		}
            		countHeadSign.put(arretParcours.headSign, countHeadSign.get(arretParcours.headSign) + 1);
            	}
            	
            	int countMax = 0;
            	String headSign = null;
            	for (Entry<String, Integer> entryCount : countHeadSign.entrySet()) {
            		if (entryCount.getValue() > countMax) {
            			countMax = entryCount.getValue();
            			headSign = entryCount.getKey();
            		}
            	}
            	parcour.setHeadSign(headSign);
            	//if (parcour.getLigneId().equals("0003")) {
            		parcours.add(parcour);
            	//}
            }
        }
	}

}
