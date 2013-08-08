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
 * 
 * Contributors:
 *     ybonnel - initial API and implementation
 */
package fr.ybo.transportsrenneshelper.util;

/**
 * Classe permettant le calcul de distance entre deux points GPS.
 * @author ybonnel
 */
public class CalculDistance {

	// CHECKSTYLE:OFF

	private static final int MAXITERS = 20;

	private double lat1;
	private double lon1;
	private double lat2;
	private double lon2;
	private static final double a = 6378137.0; // WGS84 major axis
	private static final double b = 6356752.3142; // WGS84 semi-major axis
	private static final double f = (a - b) / a;
	private static final double aSqMinusBSqOverBSq = (a * a - b * b) / (b * b);

	public CalculDistance(double lat1, double lon1, double lat2, double lon2) {
		this.lat1 = lat1;
		this.lon1 = lon1;
		this.lat2 = lat2;
		this.lon2 = lon2;
	}

	private double cosU2;
	private double cosU1;
	private double sinU2;
	private double sinU1;
	private double cosU1cosU2;
	private double sinU1sinU2;
	private double A;
	private double L;
	private double deltaSigma;
	private double sigma;


	
	private double iterationCalcul(double lambda) {
		double cosLambda = Math.cos(lambda);
		double sinLambda = Math.sin(lambda);
		double t1 = cosU2 * sinLambda;
		double t2 = cosU1 * sinU2 - sinU1 * cosU2 * cosLambda;
		double sinSqSigma = t1 * t1 + t2 * t2; // (14)
		double sinSigma = Math.sqrt(sinSqSigma);
		double cosSigma = sinU1sinU2 + cosU1cosU2 * cosLambda; // (15)
		sigma = Math.atan2(sinSigma, cosSigma); // (16)
		double sinAlpha = sinSigma == 0 ? 0.0 : cosU1cosU2 * sinLambda / sinSigma; // (17)
		double cosSqAlpha = 1.0 - sinAlpha * sinAlpha;
		double cos2SM = cosSqAlpha == 0 ? 0.0 : cosSigma - 2.0 * sinU1sinU2 / cosSqAlpha; // (18)

		double uSquared = cosSqAlpha * aSqMinusBSqOverBSq; // defn
		A = 1 + uSquared / 16384.0 * // (3)
				(4096.0 + uSquared * (-768 + uSquared * (320.0 - 175.0 * uSquared)));
		double B = uSquared / 1024.0 * // (4)
				(256.0 + uSquared * (-128.0 + uSquared * (74.0 - 47.0 * uSquared)));
		double C = f / 16.0 * cosSqAlpha * (4.0 + f * (4.0 - 3.0 * cosSqAlpha)); // (10)
		double cos2SMSq = cos2SM * cos2SM;
		deltaSigma = B * sinSigma * // (6)
				(cos2SM + B / 4.0 *
						(cosSigma * (-1.0 + 2.0 * cos2SMSq) - B / 6.0 * cos2SM * (-3.0 + 4.0 * sinSigma * sinSigma) * (-3.0 + 4.0 * cos2SMSq)));

		return L + (1.0 - C) * f * sinAlpha * (sigma + C * sinSigma * (cos2SM + C * cosSigma * (-1.0 + 2.0 * cos2SM * cos2SM))); // (11)

	}

	public double calculDistance() {
		// Based on http://www.ngs.noaa.gov/PUBS_LIB/inverse.pdf
		// using the "Inverse Formula" (section 4)
		// Convert lat/long to radians
		lat1 *= Math.PI / 180.0;
		lat2 *= Math.PI / 180.0;
		lon1 *= Math.PI / 180.0;
		lon2 *= Math.PI / 180.0;

		L = lon2 - lon1;
		A = 0.0;
		double U1 = Math.atan((1.0 - f) * Math.tan(lat1));
		double U2 = Math.atan((1.0 - f) * Math.tan(lat2));

		cosU1 = Math.cos(U1);
		cosU2 = Math.cos(U2);
		sinU1 = Math.sin(U1);
		sinU2 = Math.sin(U2);
		cosU1cosU2 = cosU1 * cosU2;
		sinU1sinU2 = sinU1 * sinU2;

		sigma = 0.0;
		deltaSigma = 0.0;

		double lambda = L; // initial guess
		for (int iter = 0; iter < MAXITERS; iter++) {
			double lambdaOrig = lambda;
			lambda = iterationCalcul(lambda);
			double delta = (lambda - lambdaOrig) / lambda;
			if (Math.abs(delta) < 1.0e-12) {
				break;
			}
		}

		return b * A * (sigma - deltaSigma);

	}
}
