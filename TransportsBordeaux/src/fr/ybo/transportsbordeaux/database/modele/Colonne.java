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

package fr.ybo.transportsbordeaux.database.modele;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;

import android.content.ContentValues;
import android.database.Cursor;
import fr.ybo.transportsbordeaux.database.DataBaseException;
import fr.ybo.transportsbordeaux.database.annotation.Indexed;
import fr.ybo.transportsbordeaux.database.annotation.PrimaryKey;

class Colonne {

	private final fr.ybo.transportsbordeaux.database.annotation.Colonne.TypeColonne type;
	private final Field field;
	private final String name;
	private final boolean primaryKey;
	private Method valueOf;
	private Method methodeEnum;
	private final Indexed indexed;
	private String tableName;

	Colonne(Colonne colonne) {
		type = colonne.type;
		field = colonne.field;
		name = colonne.name;
		primaryKey = colonne.primaryKey;
		valueOf = colonne.valueOf;
		methodeEnum = colonne.methodeEnum;
		indexed = colonne.indexed;
		tableName = colonne.tableName;
	}

	Colonne(Field field, String tableName) throws DataBaseException {
		this.field = field;
		this.tableName = tableName;
		fr.ybo.transportsbordeaux.database.annotation.Colonne colonne = field
				.getAnnotation(fr.ybo.transportsbordeaux.database.annotation.Colonne.class);
		type = colonne.type();
		primaryKey = field.getAnnotation(PrimaryKey.class) != null;
		name = "".equals(colonne.name()) ? field.getName() : colonne.name();
		indexed = field.getAnnotation(Indexed.class);
	}

	<Entite> void ajoutValeur(ContentValues values, Entite entite) throws DataBaseException {
		Object valeur = getValue(entite);
		if (valeur != null) {
			switch (type) {
				case BOOLEAN:
					values.put(name, (Boolean) valeur ? 1 : 0);
					break;
				case DATE:
					values.put(name, ((Date) valeur).getTime());
					break;
				case INTEGER:
					values.put(name, (Integer) valeur);
					break;
				case TEXT:
					values.put(name, (String) valeur);
					break;
				case NUMERIC:
					values.put(name, (Double) valeur);
					break;
			}
		}
	}

	<Entite> void appendWhereIfNotNull(StringBuilder queryBuilder, Entite entite, Collection<String> selectionArgs) throws DataBaseException {
		String valeur = getValueToString(entite);
		if (valeur != null) {
			if (queryBuilder.length() > 0) {
				queryBuilder.append(" AND ");
			}
			queryBuilder.append(name);
			queryBuilder.append(" = :");
			queryBuilder.append(name);
			selectionArgs.add(valeur);
		}
	}

	String getIndexSqlDef() {
		if (indexed == null) {
			return null;
		}
		StringBuilder requete = new StringBuilder("CREATE ");
		if (indexed.unique()) {
			requete.append("UNIQUE ");
		}
		requete.append("INDEX ");
		String nameIndex = "".equals(indexed.name()) ? new StringBuilder(tableName).append('_').append(name).toString() : indexed.name();
		requete.append(nameIndex);
		requete.append(" ON ");
		requete.append(tableName);
		requete.append(" (");
		requete.append(name);
		requete.append(" );");
		return requete.toString();
	}

	String getName() {
		return name;
	}

	String getSqlDefinition() {
		StringBuilder requete = new StringBuilder(name);
		requete.append(' ');
		requete.append(type.getSqlType());
		return requete.toString();
	}

	private Object getValue(Object object) throws DataBaseException {
		try {
			boolean isAccessible = field.isAccessible();
			field.setAccessible(true);
			Object valeur = field.get(object);
			field.setAccessible(isAccessible);
			return valeur;
		} catch (IllegalArgumentException e) {
			throw new DataBaseException(e);
		} catch (IllegalAccessException e) {
			throw new DataBaseException(e);
		}
	}

	<Entite> String getValueToString(Entite entite) throws DataBaseException {
		Object valeur = getValue(entite);
		if (valeur == null) {
			return null;
		}
		String retour;
		switch (type) {
			case BOOLEAN:
				retour = (Boolean) valeur ? "1" : "0";
				break;
			case DATE:
				retour = Long.toString(((Date) valeur).getTime());
				break;
			case INTEGER:
			case NUMERIC:
				retour = valeur.toString();
				break;
			case TEXT:
				retour = (String) valeur;
				break;
			default:
				throw new DataBaseException("Type de colonne inconnu [" + type + ']');
		}
		return retour;
	}

	boolean isIndexed() {
		return indexed != null;
	}

	boolean isPrimaryKey() {
		return primaryKey;
	}

	<Entite> void remplirEntite(Cursor cursor, Entite entite) throws DataBaseException {
		int index = cursor.getColumnIndex(name);
		if (!cursor.isNull(index)) {
			Object value;
			switch (type) {
				case INTEGER:
					value = cursor.getInt(index);
					break;
				case NUMERIC:
					value = cursor.getDouble(index);
					break;
				case TEXT:
					value = cursor.getString(index);
					break;
				case BOOLEAN:
					value = cursor.getInt(index) == 1;
					break;
				case DATE:
					value = new Date(cursor.getLong(index));
					break;
				default:
					throw new DataBaseException("Type de colonne inconnu [" + type + ']');
			}
			setValue(entite, value);
		}
	}

	void setTableName(String tableName) {
		this.tableName = tableName;
	}

	private <Entite> void setValue(Entite entite, Object value) throws DataBaseException {
		try {
			boolean isAccessible = field.isAccessible();
			field.setAccessible(true);
			field.set(entite, value);
			field.setAccessible(isAccessible);
		} catch (IllegalArgumentException e) {
			throw new DataBaseException(e);
		} catch (IllegalAccessException e) {
			throw new DataBaseException(e);
		}
	}

}
