package fr.ybo.transportsrennes.keolis.gtfs.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Colonne {
	public enum TypeColonne {
		INTEGER("INTEGER"), TEXT("TEXT"), NUMERIC("NUMERIC"), BOOLEAN("INTEGER(1)"), DATE("INTEGER"), ENUM("INTEGER");

		private String sqlType;

		private TypeColonne(final String sqlType) {
			this.sqlType = sqlType;
		}

		public String getSqlType() {
			return sqlType;
		}
	}

	@SuppressWarnings("rawtypes")
	Class<? extends Enum> clazz() default Enum.class;

	String methode() default "";

	String name() default "";

	TypeColonne type() default TypeColonne.TEXT;

}
