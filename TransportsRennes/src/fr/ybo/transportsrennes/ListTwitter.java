package fr.ybo.transportsrennes;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;
import fr.ybo.transportsrennes.activity.MenuAccueil;
import fr.ybo.transportsrennes.adapters.TwitterAdapter;
import fr.ybo.transportsrennes.util.LogYbo;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import java.util.ArrayList;
import java.util.List;

public class ListTwitter extends MenuAccueil.ListActivity {

	private static final LogYbo LOG_YBO = new LogYbo(ListTwitter.class);

	private ProgressDialog myProgressDialog;

	private List<Status> allStatus = new ArrayList<Status>();

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.liste);
		setListAdapter(new TwitterAdapter(this, R.layout.onetwitter, allStatus));
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		myProgressDialog = ProgressDialog.show(this, "", getString(R.string.dialogRequeteTwitter), true);
		new AsyncTask<Void, Void, Void>() {

			private boolean erreur = false;

			@Override
			protected Void doInBackground(final Void... pParams) {
				Twitter twitter = new TwitterFactory().getInstance();
				try {
					allStatus.addAll(twitter.getUserTimeline("@starbusmetro"));
				} catch (TwitterException e) {
					LOG_YBO.erreur("Erreur lors de la récupération des status twitter", e);
					erreur = true;
				}
				return null;
			}

			@Override
			@SuppressWarnings("unchecked")
			protected void onPostExecute(final Void pResult) {
				super.onPostExecute(pResult);
				((TwitterAdapter) getListAdapter()).notifyDataSetChanged();
				myProgressDialog.dismiss();
				if (erreur) {
					Toast.makeText(ListTwitter.this, "Erreur lors de la récupération des messages twitter.", Toast.LENGTH_LONG).show();
					ListTwitter.this.finish();
				}
			}
		}.execute();
	}

}
