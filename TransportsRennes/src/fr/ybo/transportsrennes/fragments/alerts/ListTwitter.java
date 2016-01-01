package fr.ybo.transportsrennes.fragments.alerts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;
import fr.ybo.transportscommun.util.ErreurReseau;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.adapters.alerts.TwitterAdapter;
import fr.ybo.transportsrennes.twitter.GetTwitters;
import fr.ybo.transportsrennes.twitter.MessageTwitter;

public class ListTwitter extends ListFragment {

	private final List<MessageTwitter> messages = Collections.synchronizedList(new ArrayList<MessageTwitter>());

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListAdapter(new TwitterAdapter(getActivity(), messages));
		final MessageTwitter messageChargement = new MessageTwitter();
		messageChargement.texte = getString(R.string.dialogRequeteTwitter);
		messages.add(messageChargement);
		final ListView lv = getListView();
		lv.setFastScrollEnabled(true);
		lv.setTextFilterEnabled(true);
		lv.setCacheColorHint(Color.TRANSPARENT);
		new AsyncTask<Void, Void, Void>() {

			private boolean erreurReseau;

			private final Collection<MessageTwitter> messagesTmp = new ArrayList<MessageTwitter>();

			@Override
			protected Void doInBackground(final Void... params) {
				try {
					final Collection<MessageTwitter> messagesReponse = GetTwitters.getMessages();
					if (messagesReponse != null) {
						messagesTmp.addAll(messagesReponse);
					} else {
						erreurReseau = true;
					}
				} catch (final ErreurReseau e) {
					erreurReseau = true;
				}
				return null;
			}

			@Override
			protected void onPostExecute(final Void result) {
				if (erreurReseau) {
					try {
						Toast.makeText(getActivity(), R.string.erreurReseau, Toast.LENGTH_LONG).show();
					} catch (final Exception ignore) {

					}
				} else {
					messages.clear();
					messages.addAll(messagesTmp);
					((BaseAdapter) getListAdapter()).notifyDataSetChanged();
				}
				super.onPostExecute(result);
			}

		}.execute((Void) null);
	}

}
