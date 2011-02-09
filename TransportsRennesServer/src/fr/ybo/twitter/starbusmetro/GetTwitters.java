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

package fr.ybo.twitter.starbusmetro;

import fr.ybo.twitter.starbusmetro.database.PersistenceFactory;
import fr.ybo.twitter.starbusmetro.modele.LastUpdate;
import fr.ybo.twitter.starbusmetro.modele.MessageTwitter;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import javax.jdo.PersistenceManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

class GetTwitters {

	private static final Logger logger = Logger.getLogger(GetTwitters.class.getName());

	private static final String query = "select from " + MessageTwitter.class.getName() + " order by dateCreation desc range 0,20";


	private static TwitterFactory twitterFactory = null;

	synchronized TwitterFactory getFactory() {
		if (twitterFactory == null) {
			twitterFactory = new TwitterFactory();
		}
		return twitterFactory;
	}

	@SuppressWarnings("unchecked")
	public List<MessageTwitter> getMessages() {
		PersistenceManager persistenceManager = PersistenceFactory.get().getPersistenceManager();
		List<MessageTwitter> messages = new ArrayList<MessageTwitter>();
		try {
			if (LastUpdate.getInstance().isUpdate()) {
				logger.fine("Les messages twitter sont à jour, envoie du contenu de la base de donnée");
				messages.addAll((List<MessageTwitter>) persistenceManager.newQuery(query).execute());
			} else {
				logger.fine("Les messages twitter ne sont pas à jour, récupération du contenu de twiter");
				Twitter twitter = getFactory().getInstance();
				ResponseList<Status> listeStatus;
				try {
					listeStatus = twitter.getUserTimeline("@starbusmetro");
				} catch (TwitterException e) {
					logger.log(Level.SEVERE, "Erreur lors de l'accès à twitter", e);
					messages.addAll((List<MessageTwitter>) persistenceManager.newQuery(query).execute());
					return messages;
				}
				for (Status status : listeStatus) {
					messages.add(new MessageTwitter(status.getCreatedAt(), status.getText()));
				}
				Collections.sort(messages, new Comparator<MessageTwitter>() {
					public int compare(MessageTwitter o1, MessageTwitter o2) {
						return o2.getDateCreation().compareTo(o1.getDateCreation());
					}
				});
				List<MessageTwitter> messagesBdd = (List<MessageTwitter>) persistenceManager.newQuery(query).execute();
				Date dateDernierMessage = null;
				if (messagesBdd != null && !messagesBdd.isEmpty()) {
					dateDernierMessage = messagesBdd.iterator().next().getDateCreation();
				}
				for (MessageTwitter message : messages) {
					if (dateDernierMessage == null || message.getDateCreation().after(dateDernierMessage)) {
						persistenceManager.makePersistent(message);
					}
				}
			}
		} finally {
			persistenceManager.close();
		}
		return messages;
	}

}
