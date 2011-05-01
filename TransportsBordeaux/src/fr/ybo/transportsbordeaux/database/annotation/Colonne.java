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

package fr.ybo.transportsbordeaux.database.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Colonne {
	enum TypeColonne {
		INTEGER("INTEGER"), TEXT("TEXT"), NUMERIC("NUMERIC"), BOOLEAN("INTEGER(1)"), DATE("INTEGER");

		private final String sqlType;

		TypeColonne(String sqlType) {
			this.sqlType = sqlType;
		}

		public String getSqlType() {
			return sqlType;
		}
	}

	String name() default "";

	Colonne.TypeColonne type() default Colonne.TypeColonne.TEXT;

}
