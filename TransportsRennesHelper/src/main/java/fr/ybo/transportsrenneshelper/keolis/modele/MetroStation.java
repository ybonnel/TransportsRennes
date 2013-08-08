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
package fr.ybo.transportsrenneshelper.keolis.modele;

/**
 * Représente une station de métro.
 * @author ybonnel
 *
 */
public class MetroStation {
	// CHECKSTYLE:OFF
	private String id;
	private String name;
	private double latitude;
	private double longitude;
	private boolean hasPlatformDirection1;
	private boolean hasPlatformDirection2;
	private Integer rankingPlatformDirection1;
	private Integer rankingPlatformDirection2;
	private Integer floors;
	private String lastupdate;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public boolean isHasPlatformDirection1() {
		return hasPlatformDirection1;
	}

	public void setHasPlatformDirection1(boolean hasPlatformDirection1) {
		this.hasPlatformDirection1 = hasPlatformDirection1;
	}

	public boolean isHasPlatformDirection2() {
		return hasPlatformDirection2;
	}

	public void setHasPlatformDirection2(boolean hasPlatformDirection2) {
		this.hasPlatformDirection2 = hasPlatformDirection2;
	}

	public Integer getRankingPlatformDirection1() {
		return rankingPlatformDirection1;
	}

	public void setRankingPlatformDirection1(Integer rankingPlatformDirection1) {
		this.rankingPlatformDirection1 = rankingPlatformDirection1;
	}

	public Integer getRankingPlatformDirection2() {
		return rankingPlatformDirection2;
	}

	public void setRankingPlatformDirection2(Integer rankingPlatformDirection2) {
		this.rankingPlatformDirection2 = rankingPlatformDirection2;
	}

	public Integer getFloors() {
		return floors;
	}

	public void setFloors(Integer floors) {
		this.floors = floors;
	}

	public String getLastupdate() {
		return lastupdate;
	}

	public void setLastupdate(String lastupdate) {
		this.lastupdate = lastupdate;
	}
}
