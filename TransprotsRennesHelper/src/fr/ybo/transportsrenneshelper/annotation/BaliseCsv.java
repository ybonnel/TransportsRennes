package fr.ybo.transportsrenneshelper.annotation;

import fr.ybo.transportsrenneshelper.moteurcsv.adapter.AdapterCsv;
import fr.ybo.transportsrenneshelper.moteurcsv.adapter.AdapterString;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface BaliseCsv {

	Class<? extends AdapterCsv<?>> adapter() default AdapterString.class;

	String value();
}
