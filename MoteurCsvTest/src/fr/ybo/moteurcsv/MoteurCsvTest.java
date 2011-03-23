package fr.ybo.moteurcsv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import fr.ybo.moteurcsv.adapter.AdapterBoolean;
import fr.ybo.moteurcsv.adapter.AdapterDouble;
import fr.ybo.moteurcsv.adapter.AdapterInteger;
import fr.ybo.moteurcsv.adapter.AdapterString;
import fr.ybo.moteurcsv.adapter.AdapterTime;
import fr.ybo.moteurcsv.annotation.BaliseCsv;
import fr.ybo.moteurcsv.annotation.FichierCsv;
import fr.ybo.moteurcsv.exception.MoteurCsvException;

public class MoteurCsvTest {

	@FichierCsv(value = "objet_csv.txt", separateur = "\\|")
	public static class ObjetCsv {

		@BaliseCsv(value = "att_1", ordre = 0)
		private String attribut1;

		@BaliseCsv(value = "att_2", ordre = 1, adapter = AdapterBoolean.class)
		private Boolean attribut2;

		@BaliseCsv(value = "att_3", ordre = 2, adapter = AdapterDouble.class)
		private Double attribut3;

		@BaliseCsv(value = "att_4", ordre = 3, adapter = AdapterInteger.class)
		private Integer attribut4;

		@BaliseCsv(value = "att_5", ordre = 5, adapter = AdapterString.class)
		private String attribut5;

		@BaliseCsv(value = "att_6", ordre = 6, adapter = AdapterTime.class)
		private Integer attribut6;

		protected boolean equals(String att1, Boolean att2, Double att3, Integer att4, String att5, Integer att6) {
			return ((att1 == null && attribut1 == null || att1 != null && attribut1 != null && att1.equals(attribut1))
					&& (att2 == null && attribut2 == null || att2 != null && attribut2 != null
							&& att2.equals(attribut2))
					&& (att3 == null && attribut3 == null || att3 != null && attribut3 != null
							&& att3.equals(attribut3))
					&& (att4 == null && attribut4 == null || att4 != null && attribut4 != null
							&& att4.equals(attribut4))
					&& (att5 == null && attribut5 == null || att5 != null && attribut5 != null
							&& att5.equals(attribut5)) && (att6 == null && attribut6 == null || att6 != null
					&& attribut6 != null && att6.equals(attribut6)));
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((attribut1 == null) ? 0 : attribut1.hashCode());
			result = prime * result + ((attribut2 == null) ? 0 : attribut2.hashCode());
			result = prime * result + ((attribut3 == null) ? 0 : attribut3.hashCode());
			result = prime * result + ((attribut4 == null) ? 0 : attribut4.hashCode());
			result = prime * result + ((attribut5 == null) ? 0 : attribut5.hashCode());
			result = prime * result + ((attribut6 == null) ? 0 : attribut6.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ObjetCsv other = (ObjetCsv) obj;
			if (attribut1 == null) {
				if (other.attribut1 != null)
					return false;
			} else if (!attribut1.equals(other.attribut1))
				return false;
			if (attribut2 == null) {
				if (other.attribut2 != null)
					return false;
			} else if (!attribut2.equals(other.attribut2))
				return false;
			if (attribut3 == null) {
				if (other.attribut3 != null)
					return false;
			} else if (!attribut3.equals(other.attribut3))
				return false;
			if (attribut4 == null) {
				if (other.attribut4 != null)
					return false;
			} else if (!attribut4.equals(other.attribut4))
				return false;
			if (attribut5 == null) {
				if (other.attribut5 != null)
					return false;
			} else if (!attribut5.equals(other.attribut5))
				return false;
			if (attribut6 == null) {
				if (other.attribut6 != null)
					return false;
			} else if (!attribut6.equals(other.attribut6))
				return false;
			return true;
		}
		
		

	}

	private MoteurCsv moteur = null;

	private final static String ENTETE_654321 = "att_6|att_5|att_4|att_3|att_2|att_1";

	@Before
	public void setup() {
		moteur = new MoteurCsv(new ArrayList<Class<?>>(Collections.singletonList(ObjetCsv.class)));
	}

	@Test(expected = MoteurCsvException.class)
	public void testNouveauFichier_erreur() {
		moteur.nouveauFichier("tutu.txt", ENTETE_654321);
	}

	@Test(expected = MoteurCsvException.class)
	public void testCreerObjet_erreurNouveauFicheir() {
		moteur.creerObjet("tutu");
	}

	@Test(expected = MoteurCsvException.class)
	public void testCreerObjet_erreurInstanciation() {
		moteur.nouveauFichier("objet_csv.txt", ENTETE_654321);
		moteur.creerObjet("0|1|2|3|4|5|6|7");
	}

	@Test
	public void testCreerObjetNominal() {
		moteur.nouveauFichier("objet_csv.txt", ENTETE_654321);
		ObjetCsv objetCsv = (ObjetCsv) moteur.creerObjet("01:30|String1|5|8.0|1|String2");
		assertNotNull(objetCsv);
		assertTrue(objetCsv.equals("String2", true, 8.0, 5, "String1", 90));
		objetCsv = (ObjetCsv) moteur.creerObjet("|||||");
		assertNotNull(objetCsv);
		assertTrue(objetCsv.equals(null, null, null, null, null, null));
	}

	@Test
	public void testParseInputStream() throws IOException {

		InputStream stream = new InputStream() {
			String chaine = ENTETE_654321 + "\n01:30|String1|5|8.0|1|String2\n" + "|String1|5|8.0|1|String2\n"
					+ "01:30||5|8.0|1|String2\n" + "01:30|String1||8.0|1|String2\n" + "01:30|String1|5||1|String2\n"
					+ "01:30|String1|5|8.0||String2\n" + "01:30|String1|5|8.0|1|\n";
			private int count = 0;

			@Override
			public int read() throws IOException {
				if (count >= chaine.length()) {
					return -1;
				}
				return chaine.charAt(count++);
			}
		};
		List<ObjetCsv> objets = moteur.parseInputStream(stream, ObjetCsv.class);
		assertEquals(7, objets.size());
		assertTrue(objets.get(0).equals("String2", true, 8.0, 5, "String1", 90));
		assertTrue(objets.get(1).equals("String2", true, 8.0, 5, "String1", null));
		assertTrue(objets.get(2).equals("String2", true, 8.0, 5, null, 90));
		assertTrue(objets.get(3).equals("String2", true, 8.0, null, "String1", 90));
		assertTrue(objets.get(4).equals("String2", true, null, 5, "String1", 90));
		assertTrue(objets.get(5).equals("String2", null, 8.0, 5, "String1", 90));
		assertTrue(objets.get(6).equals(null, true, 8.0, 5, "String1", 90));
		
		File file = File.createTempFile("objet_csv", "txt");
		
		moteur.writeFile(file, objets, ObjetCsv.class);
		
		List<ObjetCsv> newObjets = moteur.parseInputStream(new FileInputStream(file), ObjetCsv.class);
		assertEquals(objets, newObjets);
		
		
	}
}
