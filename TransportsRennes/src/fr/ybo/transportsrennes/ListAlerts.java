package fr.ybo.transportsrennes;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import fr.ybo.transportsrennes.keolis.ErreurKeolis;
import fr.ybo.transportsrennes.keolis.Keolis;
import fr.ybo.transportsrennes.keolis.modele.bus.Alert;
import fr.ybo.transportsrennes.util.LogYbo;

import java.util.List;

public class ListAlerts extends ListActivity {

	/**
	 * Permet d'accéder aux apis keolis.
	 */
	private final Keolis keolis = Keolis.getInstance();

	private static final LogYbo LOG_YBO = new LogYbo(ListAlerts.class);

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.liste);
		try {
			final List<Alert> alerts = keolis.getAlerts();
			setListAdapter(new ArrayAdapter<Alert>(this, R.layout.alert, alerts));
			final ListView lv = getListView();
			lv.setTextFilterEnabled(true);
			lv.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(final AdapterView<?> adapterView, final View view, final int position, final long id) {
					final Alert alert = (Alert) ((ListView) adapterView).getItemAtPosition(position);
					final Intent intent = new Intent(ListAlerts.this, DetailAlert.class);
					intent.putExtra("alert", alert);
					startActivity(intent);
				}

			});
		} catch (final ErreurKeolis erreurKeolis) {
			LOG_YBO.erreur("Erreur lors de l'appel à keolis", erreurKeolis);
			Toast.makeText(this, erreurKeolis.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}

}
