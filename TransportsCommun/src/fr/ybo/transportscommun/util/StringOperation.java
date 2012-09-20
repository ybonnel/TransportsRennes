/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     ybonnel - initial API and implementation
 */
package fr.ybo.transportscommun.util;

/**
 * Classe complementaire du J2SDK sur la manipulation de chaines de caractéres
 * Permet nottament de supprimer les accents d'une chaine de caractères
 * 
 * @author André Sébastien
 **/
public class StringOperation {
	/** Mise en minuscule **/
	public static final int LOWER_CASE = 4;

	/** Mise en majuscule **/
	public static final int UPPER_CASE = 8;

	/** Remplacement des caractères accentués par leur versions sans accents **/
	public static final int WITHOUT_ACCENTS = 16;

	/** Index du 1er caractere accentué **/
	private static final int MIN = 192;

	/** Index du dernier caractere accentué **/
	private static final int MAX = 383;

	/** Vecteur de correspondance entre accent / sans accent **/
	private static final char[] map = initMap();

	/**
	 * Initialisation du tableau de correspondance entre les caractéres
	 * accentués et leur homologues non accentués
	 **/
	private static char[] initMap() {
		char[] result = new char[MAX - MIN + 1];
		char car = ' ';

		car = 'A';
		result[00] = car; /* '\u00C0' À alt-0192 */
		result[01] = car; /* '\u00C1' Á alt-0193 */
		result[02] = car; /* '\u00C2' Â alt-0194 */
		result[03] = car; /* '\u00C3' Ã alt-0195 */
		result[04] = car; /* '\u00C4' Ä alt-0196 */
		result[05] = car; /* '\u00C5' Å alt-0197 */
		car = ' ';
		result[06] = car; /* '\u00C6' Æ alt-0198 ********* BI-CARACTERE ******** */
		car = 'C';
		result[07] = car; /* '\u00C7' Ç alt-0199 */
		car = 'E';
		result[8] = car; /* '\u00C8' È alt-0200 */
		result[9] = car; /* '\u00C9' É alt-0201 */
		result[10] = car; /* '\u00CA' Ê alt-0202 */
		result[11] = car; /* '\u00CB' Ë alt-0203 */
		car = 'I';
		result[12] = car; /* '\u00CC' Ì alt-0204 */
		result[13] = car; /* '\u00CD' Í alt-0205 */
		result[14] = car; /* '\u00CE' Î alt-0206 */
		result[15] = car; /* '\u00CF' Ï alt-0207 */
		car = 'D';
		result[16] = car; /* '\u00D0' Ð alt-0208 */
		car = 'N';
		result[17] = car; /* '\u00D1' Ñ alt-0209 */
		car = 'O';
		result[18] = car; /* '\u00D2' Ò alt-0210 */
		result[19] = car; /* '\u00D3' Ó alt-0211 */
		result[20] = car; /* '\u00D4' Ô alt-0212 */
		result[21] = car; /* '\u00D5' Õ alt-0213 */
		result[22] = car; /* '\u00D6' Ö alt-0214 */
		car = '*';
		result[23] = car; /* '\u00D7' × alt-0215 ***** NON ALPHA **** */
		car = '0';
		result[24] = car; /* '\u00D8' Ø alt-0216 */
		car = 'U';
		result[25] = car; /* '\u00D9' Ù alt-0217 */
		result[26] = car; /* '\u00DA' Ú alt-0218 */
		result[27] = car; /* '\u00DB' Û alt-0219 */
		result[28] = car; /* '\u00DC' Ü alt-0220 */
		car = 'Y';
		result[29] = car; /* '\u00DD' Ý alt-0221 */
		car = ' ';
		result[30] = car; /* '\u00DE' Þ alt-0222 ***** NON ALPHA **** */
		car = 'B';
		result[31] = car; /* '\u00DF' ß alt-0223 ***** NON ALPHA **** */
		car = 'a';
		result[32] = car; /* '\u00E0' à alt-0224 */
		result[33] = car; /* '\u00E1' á alt-0225 */
		result[34] = car; /* '\u00E2' â alt-0226 */
		result[35] = car; /* '\u00E3' ã alt-0227 */
		result[36] = car; /* '\u00E4' ä alt-0228 */
		result[37] = car; /* '\u00E5' å alt-0229 */
		car = ' ';
		result[38] = car; /* '\u00E6' æ alt-0230 ********* BI-CARACTERE ******** */
		car = 'c';
		result[39] = car; /* '\u00E7' ç alt-0231 */
		car = 'e';
		result[40] = car; /* '\u00E8' è alt-0232 */
		result[41] = car; /* '\u00E9' é alt-0233 */
		result[42] = car; /* '\u00EA' ê alt-0234 */
		result[43] = car; /* '\u00EB' ë alt-0235 */
		car = 'i';
		result[44] = car; /* '\u00EC' ì alt-0236 */
		result[45] = car; /* '\u00ED' í alt-0237 */
		result[46] = car; /* '\u00EE' î alt-0238 */
		result[47] = car; /* '\u00EF' ï alt-0239 */
		car = 'd';
		result[48] = car; /* '\u00F0' ð alt-0240 */
		car = 'n';
		result[49] = car; /* '\u00F1' ñ alt-0241 */
		car = 'o';
		result[50] = car; /* '\u00F2' ò alt-0242 */
		result[51] = car; /* '\u00F3' ó alt-0243 */
		result[52] = car; /* '\u00F4' ô alt-0244 */
		result[53] = car; /* '\u00F5' õ alt-0245 */
		result[54] = car; /* '\u00F6' ö alt-0246 */
		car = '/';
		result[55] = car; /* '\u00F7' ÷ alt-0247 ***** NON ALPHA **** */
		car = '0';
		result[56] = car; /* '\u00F8' ø alt-0248 ***** NON ALPHA **** */
		car = 'u';
		result[57] = car; /* '\u00F9' ù alt-0249 */
		result[58] = car; /* '\u00FA' ú alt-0250 */
		result[59] = car; /* '\u00FB' û alt-0251 */
		result[60] = car; /* '\u00FC' ü alt-0252 */
		car = 'y';
		result[61] = car; /* '\u00FD' ý alt-0253 */
		car = ' ';
		result[62] = car; /* '\u00FE' þ alt-0254 ***** NON ALPHA **** */
		car = 'y';
		result[63] = car; /* '\u00FF' ÿ alt-0255 */

		result[64] = 'A'; /* '\u0100' ? */
		result[65] = 'a'; /* '\u0101' ? */
		result[66] = 'A'; /* '\u0102' ? */
		result[67] = 'a'; /* '\u0103' ? */
		result[68] = 'A'; /* '\u0104' ? */
		result[69] = 'a'; /* '\u0105' ? */

		result[70] = 'C'; /* '\u0106' ? */
		result[71] = 'c'; /* '\u0107' ? */
		result[72] = 'C'; /* '\u0108' ? */
		result[73] = 'c'; /* '\u0109' ? */
		result[74] = 'C'; /* '\u010A' ? */
		result[75] = 'c'; /* '\u010B' ? */
		result[76] = 'C'; /* '\u010C' ? */
		result[77] = 'c'; /* '\u010D' ? */

		result[78] = 'D'; /* '\u010e' ? */
		result[79] = 'd'; /* '\u010f' ? */
		result[80] = 'D'; /* '\u0110' ? */
		result[81] = 'd'; /* '\u0111' ? */

		result[82] = 'E'; /* '\u0112' ? */
		result[83] = 'e'; /* '\u0113' ? */
		result[84] = 'E'; /* '\u0114' ? */
		result[85] = 'e'; /* '\u0115' ? */
		result[86] = 'E'; /* '\u0116' ? */
		result[87] = 'e'; /* '\u0117' ? */
		result[88] = 'E'; /* '\u0118' ? */
		result[89] = 'e'; /* '\u0119' ? */
		result[90] = 'E'; /* '\u011A' ? */
		result[91] = 'e'; /* '\u011B' ? */

		result[92] = 'G'; /* '\u011C' ? */
		result[93] = 'g'; /* '\u011D' ? */
		result[94] = 'G'; /* '\u011E' ? */
		result[95] = 'g'; /* '\u011F' ? */
		result[96] = 'G'; /* '\u0120' ? */
		result[97] = 'g'; /* '\u0121' ? */
		result[98] = 'G'; /* '\u0122' ? */
		result[99] = 'g'; /* '\u0123' ? */

		result[100] = 'H'; /* '\u0124' ? */
		result[101] = 'h'; /* '\u0125' ? */
		result[102] = 'H'; /* '\u0126' ? */
		result[103] = 'h'; /* '\u0127' ? */

		result[104] = 'I'; /* '\u0128' ? */
		result[105] = 'i'; /* '\u0129' ? */
		result[106] = 'I'; /* '\u012A' ? */
		result[107] = 'i'; /* '\u012B' ? */
		result[108] = 'I'; /* '\u012C' ? */
		result[109] = 'i'; /* '\u012D' ? */
		result[110] = 'I'; /* '\u012E' ? */
		result[111] = 'i'; /* '\u012F' ? */
		result[112] = 'I'; /* '\u0130' ? */
		result[113] = 'i'; /* '\u0131' ? */

		result[114] = ' '; /* '\u0132' ? ********* BI-CARACTERE ******** */
		result[115] = ' '; /* '\u0133' ? ********* BI-CARACTERE ******** */
		result[116] = 'J'; /* '\u0134' ? */
		result[117] = 'j'; /* '\u0135' ? */

		result[118] = 'K'; /* '\u0136' ? */
		result[119] = 'k'; /* '\u0137' ? */
		result[120] = 'k'; /* '\u0138' ? */

		result[121] = 'L'; /* '\u0139' ? */
		result[122] = 'l'; /* '\u013A' ? */
		result[123] = 'L'; /* '\u013B' ? */
		result[124] = 'l'; /* '\u013C' ? */
		result[125] = 'L'; /* '\u013D' ? */
		result[126] = 'l'; /* '\u013E' ? */
		result[127] = 'L'; /* '\u013F' ? */
		result[128] = 'l'; /* '\u0140' ? */
		result[129] = 'L'; /* '\u0141' ? */
		result[130] = 'l'; /* '\u0142' ? */

		result[131] = 'N'; /* '\u0143' ? */
		result[132] = 'n'; /* '\u0144' ? */
		result[133] = 'N'; /* '\u0145' ? */
		result[134] = 'n'; /* '\u0146' ? */
		result[135] = 'N'; /* '\u0147' ? */
		result[136] = 'n'; /* '\u0148' ? */
		result[137] = 'n'; /* '\u0149' ? */
		result[138] = 'N'; /* '\u014A' ? */
		result[139] = 'n'; /* '\u014B' ? */

		result[140] = 'O'; /* '\u014C' ? */
		result[141] = 'o'; /* '\u014D' ? */
		result[142] = 'O'; /* '\u014E' ? */
		result[143] = 'o'; /* '\u014F' ? */
		result[144] = 'O'; /* '\u0150' ? */
		result[145] = 'o'; /* '\u0151' ? */
		result[146] = ' '; /* '\u0152' Œ ********* BI-CARACTERE ******** */
		result[147] = ' '; /* '\u0153' œ ********* BI-CARACTERE ******** */

		result[148] = 'R'; /* '\u0154' ? *//* --> non testé dans test JUnit */
		result[149] = 'r'; /* '\u0155' ? */
		result[150] = 'R'; /* '\u0156' ? */
		result[151] = 'r'; /* '\u0157' ? */
		result[152] = 'R'; /* '\u0158' ? */
		result[153] = 'r'; /* '\u0159' ? */

		result[154] = 'S'; /* '\u015A' ? */
		result[155] = 's'; /* '\u015B' ? */
		result[156] = 'S'; /* '\u015C' ? */
		result[157] = 's'; /* '\u015D' ? */
		result[158] = 'S'; /* '\u015E' ? */
		result[159] = 's'; /* '\u015F' ? */
		result[160] = 'S'; /* '\u0160' Š */
		result[161] = 's'; /* '\u0161' š */

		result[162] = 'T'; /* '\u0162' ? */
		result[163] = 't'; /* '\u0163' ? */
		result[164] = 'T'; /* '\u0164' ? */
		result[165] = 't'; /* '\u0165' ? */
		result[166] = 'T'; /* '\u0166' ? */
		result[167] = 't'; /* '\u0167' ? */

		result[168] = 'U'; /* '\u0168' ? */
		result[169] = 'u'; /* '\u0169' ? */
		result[170] = 'U'; /* '\u016A' ? */
		result[171] = 'u'; /* '\u016B' ? */
		result[172] = 'U'; /* '\u016C' ? */
		result[173] = 'u'; /* '\u016D' ? */
		result[174] = 'U'; /* '\u016E' ? */
		result[175] = 'u'; /* '\u016F' ? */
		result[176] = 'U'; /* '\u0170' ? */
		result[177] = 'u'; /* '\u0171' ? */
		result[178] = 'U'; /* '\u0172' ? */
		result[179] = 'u'; /* '\u0173' ? */

		result[180] = 'W'; /* '\u0174' ? */
		result[181] = 'w'; /* '\u0175' ? */

		result[182] = 'Y'; /* '\u0176' ? */
		result[183] = 'y'; /* '\u0177' ? */
		result[184] = 'Y'; /* '\u0178' Ÿ */

		result[185] = 'Z'; /* '\u0179' ? */
		result[186] = 'z'; /* '\u017A' ? */
		result[187] = 'Z'; /* '\u017B' ? */
		result[188] = 'z'; /* '\u017C' ? */
		result[189] = 'Z'; /* '\u017D' Ž */
		result[190] = 'z'; /* '\u017E' ž */

		result[191] = 'f'; /* '\u017F' ? */

		return result;
	}

	/**
	 * Transforme une chaine de caractères selon differents critères (paramètre
	 * mode),
	 * 
	 * @param chaine
	 *            Chaine sur laquelle on veut effectuer une transformation
	 * @param mode
	 *            Mode de transformation, plusieurs mode sont accessibles,
	 *            ceux-ci peuvent être combiné - LOWER_CASE : Mise en minuscule
	 *            - UPPER_CASE : Mise en majuscule - WITHOUT_ACCENTS : Remplace
	 *            les caractères accentués par leur version sans accent La
	 *            combinaision de mode se fait de la sorte LOWER_CASE |
	 *            WITHOUT_ACCENTS
	 * @return Chain transformée
	 **/
	public static java.lang.String transform(java.lang.String chaine, int mode) {
		if (mode == UPPER_CASE)
			return chaine.toUpperCase();
		if (mode == LOWER_CASE)
			return chaine.toLowerCase();

		int firstReplacement = scan(chaine, mode);

		if (firstReplacement == -1)
			return chaine;

		char[] result = chaine.toCharArray();
		int offset = firstReplacement;

		boolean toUpper = (mode & UPPER_CASE) > 0;
		boolean toLower = (mode & LOWER_CASE) > 0;
		boolean withoutAccents = (mode & WITHOUT_ACCENTS) > 0;

		for (int bcl = firstReplacement; bcl < chaine.length(); bcl++) {
			char c = result[bcl];
			int type = Character.getType(c);

			/**
			 * Remplacement
			 */
			char r = c;

			/**
			 * isLetter()
			 */
			if (type == Character.UPPERCASE_LETTER || type == Character.LOWERCASE_LETTER
					|| type == Character.TITLECASE_LETTER || type == Character.MODIFIER_LETTER
					|| type == Character.OTHER_LETTER) // || type ==
														// Character.OTHER_PUNCTUATION
														// )
			{
				if (toUpper && (type == Character.LOWERCASE_LETTER || type == Character.OTHER_PUNCTUATION)) {
					r = Character.toUpperCase(c);
				} else if (toLower && (type == Character.UPPERCASE_LETTER || type == Character.OTHER_PUNCTUATION))
					r = Character.toLowerCase(c);

				if (withoutAccents && r >= MIN && r <= MAX
						&& (r != 198 && r != 230 && r != 306 && r != 307 && r != 339 && r != 340)) {
					r = map[(int) r - MIN];
				}
			}

			result[offset] = r;
			offset++;
		}

		return new String(result, 0, offset);
	}

	/**
	 * Donne l'index du 1er caractère donnant lieu a une transformation selon le
	 * mode donné
	 * 
	 * @param chaine
	 *            Chaine à tester
	 * @param mode
	 *            Mode pilotant la future transformation de la chaine
	 * @return index du 1er caractère à transformer, -1 si aucun caractère n'est
	 *         a transformer
	 */
	private static int scan(java.lang.String chaine, int mode) {
		/**
		 * ALPHA_NUM WITHOUT_SPECIALS_CHARS LOWER_CASE UPPER_CASE
		 * WITHOUT_ACCENTS
		 */
		int computedMode = 0;
		for (int bcl = 0; bcl < chaine.length(); bcl++) {
			char c = chaine.charAt(bcl);
			int type = Character.getType(c);

			/**
			 * isLetter()
			 */
			if (type == Character.UPPERCASE_LETTER || type == Character.LOWERCASE_LETTER
					|| type == Character.TITLECASE_LETTER || type == Character.MODIFIER_LETTER
					|| type == Character.OTHER_LETTER) // || type ==
														// Character.OTHER_PUNCTUATION
														// )
			{
				if (type == Character.LOWERCASE_LETTER)
					computedMode = computedMode | UPPER_CASE;
				else if (type == Character.UPPERCASE_LETTER)
					computedMode = computedMode | LOWER_CASE;

				if (c >= MIN && c <= MAX) {
					computedMode = computedMode | WITHOUT_ACCENTS;
				}
			}

			if ((computedMode & mode) > 0)
				return bcl;
		}

		return -1;
	}

	/**
	 * Transforme une chaine pouvant contenir des accents dans une version sans
	 * accent
	 * 
	 * @param chaine
	 *            Chaine a convertir sans accent
	 * @return Chaine dont les accents ont été supprimé
	 **/
	public static java.lang.String sansAccents(java.lang.String chaine) {
		return transform(chaine, WITHOUT_ACCENTS);
	}
}
