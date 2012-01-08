package fr.ybo.transportsrennes.fragments.alerts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.widget.BaseAdapter;
import android.widget.ListView;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.adapters.alerts.TwitterAdapter;
import fr.ybo.transportsrennes.twitter.GetTwitters;
import fr.ybo.transportsrennes.twitter.MessageTwitter;
import fr.ybo.transportsrennes.util.ErreurReseau;
import fr.ybo.transportsrennes.util.TacheAvecProgressDialog;

public class ListTwitter extends ListFragment {

	private final List<MessageTwitter> messages = Collections.synchronizedList(new ArrayList<MessageTwitter>());

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListAdapter(new TwitterAdapter(getActivity(), messages));
		ListView lv = getListView();
		lv.setFastScrollEnabled(true);
		lv.setTextFilterEnabled(true);
		new TacheAvecProgressDialog<Void, Void, Void>(getActivity(), getString(R.string.dialogRequeteTwitter)) {
			@Override
			protected void myDoBackground() throws ErreurReseau {
				messages.addAll(GetTwitters.getInstance().getMessages());
			}

			@Override
			protected void onPostExecute(Void result) {
				((BaseAdapter) getListAdapter()).notifyDataSetChanged();
				super.onPostExecute(result);
			}
		}.execute((Void) null);
	}

}
