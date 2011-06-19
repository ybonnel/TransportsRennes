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
package fr.ybo.twitter.starbusmetro.modele;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.gson.annotations.Expose;

@PersistenceCapable
public class MessageTwitter {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

	@Persistent
	@Expose
	public Date dateCreation;

	@Persistent
	@Expose
	public String texte;

	@Persistent
	public String compte;

	public MessageTwitter(Date dateCreation, String texte, String compte) {
		this.dateCreation = dateCreation;
		this.texte = texte;
		this.compte = compte;
	}

	public MessageTwitter() {
	}

	public Date getDateCreation() {
		return dateCreation;
	}

	/**
	 * @return the key
	 */
	public Key getKey() {
		return key;
	}

	public String toXml() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<message>");
		stringBuilder.append("<dateCreation>");
		stringBuilder.append(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(dateCreation));
		stringBuilder.append("</dateCreation>");
		stringBuilder.append("<contenu>");
		stringBuilder.append(texte.replace('&', ' '));
		stringBuilder.append("</contenu>");
		stringBuilder.append("</message>");
		return stringBuilder.toString();
	}
}
