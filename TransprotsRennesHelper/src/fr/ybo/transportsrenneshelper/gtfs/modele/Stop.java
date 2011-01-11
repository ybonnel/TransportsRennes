package fr.ybo.transportsrenneshelper.gtfs.modele;

import fr.ybo.transportsrenneshelper.annotation.BaliseCsv;
import fr.ybo.transportsrenneshelper.annotation.FichierCsv;
import fr.ybo.transportsrenneshelper.moteurcsv.adapter.AdapterDouble;

@FichierCsv("stops.txt")
public class Stop {

	@BaliseCsv("stop_id")
	public String id;
	@BaliseCsv("stop_name")
	public String nom;
	@BaliseCsv(value = "stop_lat", adapter = AdapterDouble.class)
	public double latitude;
	@BaliseCsv(value = "stop_lon", adapter = AdapterDouble.class)
	public double longitude;

	@Override
	public String toString() {
		return "Stop{" + "id='" + id + '\'' + ", nom='" + nom + '\'' + ", latitude=" + latitude + ", longitude=" + longitude + '}';
	}
}
