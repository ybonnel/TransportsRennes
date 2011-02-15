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

package fr.ybo.transportsrennes.keolis.gtfs.database.modele;

import android.content.ContentValues;
import android.database.Cursor;
import fr.ybo.transportsrennes.keolis.gtfs.annotation.Colonne.TypeColonne;
import fr.ybo.transportsrennes.keolis.gtfs.annotation.Indexed;
import fr.ybo.transportsrennes.keolis.gtfs.annotation.PrimaryKey;
import fr.ybo.transportsrennes.keolis.gtfs.database.DataBaseException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;

class Colonne {

	private final TypeColonne type;
	private final Field field;
	private final String name;
	private final boolean primaryKey;
	private Method valueOf;
	private Method methodeEnum;
	private final Indexed indexed;
	private String tableName;

	Colonne(final Colonne colonne) {
		super();
		type = colonne.type;
		field = colonne.field;
		name = colonne.name;
		primaryKey = colonne.primaryKey;
		valueOf = colonne.valueOf;
		methodeEnum = colonne.methodeEnum;
		indexed = colonne.indexed;
		tableName = colonne.tableName;
	}

	Colonne(final Field field, final String tableName) throws DataBaseException {
		super();
		this.field = field;
		this.tableName = tableName;
		final fr.ybo.transportsrennes.keolis.gtfs.annotation.Colonne colonne =
				field.getAnnotation(fr.ybo.transportsrennes.keolis.gtfs.annotation.Colonne.class);
		type = colonne.type();
		primaryKey = field.getAnnotation(PrimaryKey.class) != null;
		name = "".equals(colonne.name()) ? field.getName() : colonne.name();
		indexed = field.getAnnotation(Indexed.class);
	}

	<Entite> void ajoutValeur(final ContentValues values, final Entite entite) throws DataBaseException {
		final Object valeur = getValue(entite);
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

	<Entite> void appendWhereIfNotNull(final StringBuilder queryBuilder, final Entite entite, final Collection<String> selectionArgs)
			throws DataBaseException {
		final String valeur = getValueToString(entite);
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
		final StringBuilder requete = new StringBuilder("CREATE ");
		if (indexed.unique()) {
			requete.append("UNIQUE ");
		}
		requete.append("INDEX ");
		final String nameIndex;
		nameIndex = "".equals(indexed.name()) ? new StringBuilder(tableName).append('_').append(name).toString() : indexed.name();
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
		final StringBuilder requete = new StringBuilder(name);
		requete.append(" ");
		requete.append(type.getSqlType());
		return requete.toString();
	}

	private Object getValue(final Object object) throws DataBaseException {
		try {
			final boolean isAccessible = field.isAccessible();
			field.setAccessible(true);
			final Object valeur = field.get(object);
			field.setAccessible(isAccessible);
			return valeur;
		} catch (final IllegalArgumentException e) {
			throw new DataBaseException(e);
		} catch (final IllegalAccessException e) {
			throw new DataBaseException(e);
		}
	}

	<Entite> String getValueToString(final Entite entite) throws DataBaseException {
		final Object valeur = getValue(entite);
		if (valeur == null) {
			return null;
		}
		final String retour;
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
				throw new DataBaseException("Type de colonne inconnu [" + type + "]");
		}
		return retour;
	}

	boolean isIndexed() {
		return indexed != null;
	}

	boolean isPrimaryKey() {
		return primaryKey;
	}

	<Entite> void remplirEntite(final Cursor cursor, final Entite entite) throws DataBaseException {
		final int index = cursor.getColumnIndex(name);
		if (!cursor.isNull(index)) {
			final Object value;
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
					throw new DataBaseException("Type de colonne inconnu [" + type + "]");
			}
			setValue(entite, value);
		}
	}

	void setTableName(final String tableName) {
		this.tableName = tableName;
	}

	private <Entite> void setValue(final Entite entite, final Object value) throws DataBaseException {
		try {
			final boolean isAccessible = field.isAccessible();
			field.setAccessible(true);
			field.set(entite, value);
			field.setAccessible(isAccessible);
		} catch (final IllegalArgumentException e) {
			throw new DataBaseException(e);
		} catch (final IllegalAccessException e) {
			throw new DataBaseException(e);
		}
	}

}
