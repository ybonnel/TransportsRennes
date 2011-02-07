package fr.ybo.transportsrennes.twitter;

import fr.ybo.transportsrennes.util.LogYbo;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import java.util.ArrayList;

public class GetTwitters {

	private static final LogYbo LOG_YBO = new LogYbo(GetTwitters.class);

	private static TwitterFactory twitterFactory = null;

	synchronized private TwitterFactory getFactory() {
		if (twitterFactory == null) {
			twitterFactory = new TwitterFactory();
		}
		return twitterFactory;
	}

	private static GetTwitters instance = null;

	private GetTwitters() {
	}

	synchronized public static GetTwitters getInstance() {
		if (instance == null) {
			instance = new GetTwitters();
		}
		return instance;
	}

	public ArrayList<MessageTwitter> getMessages() {
		ArrayList<MessageTwitter> messages = new ArrayList<MessageTwitter>();
		Twitter twitter = getFactory().getInstance();
		try {
			for (Status status : twitter.getUserTimeline("@starbusmetro")) {
				messages.add(new MessageTwitter(status.getCreatedAt(), status.getText()));
			}
		} catch (TwitterException ignore) {
		}
		return messages;
	}
}
