package fr.ybo.transportsbordeaux.modele;

import fr.ybo.database.annotation.Column;
import fr.ybo.database.annotation.Entity;
import fr.ybo.database.annotation.PrimaryKey;
import fr.ybo.database.annotation.Column.TypeColumn;
import fr.ybo.moteurcsv.adapter.AdapterBoolean;
import fr.ybo.moteurcsv.adapter.AdapterInteger;
import fr.ybo.moteurcsv.annotation.BaliseCsv;
import fr.ybo.moteurcsv.annotation.FichierCsv;

@FichierCsv("calendrier_exception.txt")
@Entity
public class CalendrierException {
	@BaliseCsv(value = "calendrier_id", adapter = AdapterInteger.class)
	@Column( type = TypeColumn.INTEGER )
	@PrimaryKey
	public Integer calendrierId;
	@BaliseCsv(value = "date")
	@Column
	@PrimaryKey
	public String date;
	@BaliseCsv(value = "ajout", adapter = AdapterBoolean.class)
	@Column( type = TypeColumn.BOOLEAN )
	public Boolean ajout;

}
