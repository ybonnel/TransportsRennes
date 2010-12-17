package fr.ybo.transportsrennes;

import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import fr.ybo.transportsrennes.keolis.ErreurKeolis;
import fr.ybo.transportsrennes.keolis.gtfs.UpdateDataBase;
import fr.ybo.transportsrennes.keolis.gtfs.database.DataBaseException;
import fr.ybo.transportsrennes.keolis.gtfs.database.DataBaseHelper;
import fr.ybo.transportsrennes.keolis.gtfs.modele.DernierMiseAJour;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Route;
import fr.ybo.transportsrennes.util.ChangeMessage;
import fr.ybo.transportsrennes.util.LogYbo;

/**
 * Activité principale affichant un menu des différentes fonctions.
 * 
 * @author ybonnel
 * 
 */
public class BusRennes extends ListActivity {

	private void constructionListe() {
		try {
			final List<Route> routes = ((BusRennesApplication) getApplication()).getDataBaseHelper().select(new Route(), null,
					null, "id");
			setListAdapter(new RouteAdapter(getApplicationContext(), routes));
			final ListView lv = getListView();
			lv.setTextFilterEnabled(true);
			lv.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(final AdapterView<?> adapterView, final View view, final int position, final long id) {
					final Route route = (Route) ((ListView) adapterView).getItemAtPosition(position);
					final Intent intent = new Intent(BusRennes.this, ListArret.class);
					intent.putExtra("route", route);
					startActivity(intent);
				}

			});

		} catch (final DataBaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final ErreurKeolis e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void majListe() {
		try {
			final List<Route> routes = ((BusRennesApplication) getApplication()).getDataBaseHelper().select(new Route(), null,
					null, "id");
			((RouteAdapter) getListAdapter()).majRoutes(routes);
			((RouteAdapter) getListAdapter()).notifyDataSetChanged();
		} catch (final DataBaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bus);
		constructionListe();
	}
}