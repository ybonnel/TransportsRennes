package fr.ybo.transportsbordeaux.modele;

import fr.ybo.database.annotation.Column;
import fr.ybo.database.annotation.Entity;
import fr.ybo.database.annotation.PrimaryKey;

@Entity
public class GroupeFavori {

	@PrimaryKey
	@Column
	public String name;

}
