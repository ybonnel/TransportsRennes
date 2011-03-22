package com.google.co.geocoder.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.code.geocoder.util.StringUtils;

/**
 * Classe de test de la classe StringUtils.
 * @author ybonnel
 *
 */
public class StringUtilsTest {

	/**
	 * Test de le méthode {@link StringUtils#isNotBlank(CharSequence)}.
	 */
	@Test
	public void testIsNotBlank() {
		assertFalse(StringUtils.isNotBlank(null));
		assertFalse(StringUtils.isNotBlank(""));
		assertTrue(StringUtils.isNotBlank("tutu"));
	}

}
