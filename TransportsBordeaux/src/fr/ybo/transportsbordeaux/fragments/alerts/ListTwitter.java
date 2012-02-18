package fr.ybo.transportsbordeaux.fragments.alerts;

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
import fr.ybo.transportsbordeaux.R;
import fr.ybo.transportsbordeaux.adapters.alerts.TwitterAdapter;
import fr.ybo.transportsbordeaux.tbcapi.TbcErreurReseaux;
import fr.ybo.transportsbordeaux.twitter.GetTwitters;
import fr.ybo.transportsbordeaux.twitter.MessageTwitter;

public class ListTwitter extends ListFragment {

	private final List<MessageTwitter> messages = Collections.synchronizedList(new ArrayList<MessageTwitter>());

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListAdapter(new TwitterAdapter(getActivity(), messages));
		MessageTwitter messageChargement = new MessageTwitter();
		messageChargement.texte = getString(R.string.dialogRequeteTwitter);
		messages.add(messageChargement);
		ListView lv = getListView();
		lv.setFastScrollEnabled(true);
		lv.setTextFilterEnabled(true);
		lv.setCacheColorHint(Color.TRANSPARENT);
		new AsyncTask<Void, Void, Void>() {

			private boolean erreurReseau = false;

			private List<MessageTwitter> messagesTmp = new ArrayList<MessageTwitter>();

			@Override
			protected Void doInBackground(Void... params) {
				try {
					Collection<MessageTwitter> messagesReponse = GetTwitters.getInstance().getMessages();
					if (messagesReponse != null) {
						messagesTmp.addAll(messagesReponse);
					} else {
						erreurReseau = true;
					}
				} catch (TbcErreurReseaux e) {
					erreurReseau = true;
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				if (erreurReseau) {
					try {
						Toast.makeText(getActivity(), getString(R.string.erreurReseau), Toast.LENGTH_LONG).show();
					} catch (Exception ignore) {

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
