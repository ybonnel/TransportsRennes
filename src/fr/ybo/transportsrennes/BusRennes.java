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

	private ProgressDialog myProgressDialog;

	private static final LogYbo LOG_YBO = new LogYbo(BusRennes.class);

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
		verifierUpgrade();
		constructionListe();
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		Intent intent;
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.updateKeolis:
			upgradeDatabase();
			return true;
        case R.id.loadAllLines:
            loadAllLines();
            return true;
		case R.id.menuListAlert:
			intent = new Intent(BusRennes.this, ListAlerts.class);
			startActivity(intent);
			return true;
		case R.id.menuListFavoris:
			intent = new Intent(BusRennes.this, ListFavoris.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

    private void loadAllLines() {
        myProgressDialog = ProgressDialog.show(BusRennes.this, "", "Chargement des données de chaque ligne...", true);

		LOG_YBO.debug("###### Lancement du chargement de lignes de bus ");

		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(final Void... pParams) {
				try {
					DataBaseHelper dataBaseHelper = BusRennesApplication.getDataBaseHelper();
                    List<Route> routes = dataBaseHelper.select(new Route());
                    for (Route route : routes) {
                        runOnUiThread(new ChangeMessage(myProgressDialog, getString(R.string.loadAllLines, route.getNomCourt())));
                        UpdateDataBase.chargeDetailRoute(route, getApplicationContext());
                    }
				} catch (final Exception e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(final Void pResult) {
				super.onPostExecute(pResult);
				LOG_YBO.debug("###### Fin du chargement des lignes de bus ");
				myProgressDialog.dismiss();
			}
		}.execute((Void[]) null);

    }

	private void upgradeDatabase() {
		myProgressDialog = ProgressDialog.show(BusRennes.this, "", "Chargement des données Keolis...", true);

		LOG_YBO.debug("###### Lancement de la mise à jour ");

		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(final Void... pParams) {
				try {
					UpdateDataBase.updateIfNecessaryDatabase(
							((BusRennesApplication) BusRennes.this.getApplication()).getDataBaseHelper(),
							BusRennes.this.getApplicationContext(), myProgressDialog, BusRennes.this);
				} catch (final Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(final Void pResult) {
				super.onPostExecute(pResult);
				LOG_YBO.debug("###### Fin de la mise à jour ");
				majListe();
				myProgressDialog.dismiss();
			}
		}.execute((Void[]) null);
	}

	private void verifierUpgrade() {
		try {
			final DataBaseHelper dataBaseHelper = ((BusRennesApplication) getApplication()).getDataBaseHelper();
			final DernierMiseAJour dernierMiseAJour = dataBaseHelper.selectSingle(new DernierMiseAJour());
			if (dernierMiseAJour == null) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(
						getString(R.string.premierLancement))
						.setCancelable(false).setPositiveButton("Oui", new DialogInterface.OnClickListener() {
							public void onClick(final DialogInterface dialog, final int id) {
								dialog.dismiss();
								BusRennes.this.upgradeDatabase();
							}
						}).setNegativeButton("Non", new DialogInterface.OnClickListener() {
							public void onClick(final DialogInterface dialog, final int id) {
								dialog.cancel();
								BusRennes.this.finish();
							}
						});
				final AlertDialog alert = builder.create();
				alert.show();
			}
		} catch (final DataBaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}