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

package fr.ybo.twitter.starbusmetro.modele;

import com.google.appengine.api.datastore.Key;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import java.text.SimpleDateFormat;
import java.util.Date;

@PersistenceCapable
public class MessageTwitter {

	private final transient SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

	@Persistent
	public Date dateCreation;

	@Persistent
	public String texte;

	public MessageTwitter(Date dateCreation, String texte) {
		this.dateCreation = dateCreation;
		this.texte = texte;
	}

	public MessageTwitter() {
	}

	public Date getDateCreation() {
		return dateCreation;
	}

	public void setTexte(String texte) {
		this.texte = texte;
	}

	public String toXml() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<message>");
		stringBuilder.append("<dateCreation>");
		stringBuilder.append(simpleDateFormat.format(dateCreation));
		stringBuilder.append("</dateCreation>");
		stringBuilder.append("<contenu>");
		stringBuilder.append(texte);
		stringBuilder.append("</contenu>");
		stringBuilder.append("</message>");
		return stringBuilder.toString();
	}
}
