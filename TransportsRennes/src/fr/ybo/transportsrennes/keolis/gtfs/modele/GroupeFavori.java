package fr.ybo.transportsrennes.keolis.gtfs.modele;

import fr.ybo.database.annotation.Column;
import fr.ybo.database.annotation.Column.TypeColumn;
import fr.ybo.database.annotation.Entity;
import fr.ybo.database.annotation.PrimaryKey;

@Entity
public class GroupeFavori {

	@PrimaryKey(autoIncrement = true)
	@Column(type = TypeColumn.INTEGER)
	public Integer id;

	@Column
	public String name;

}
