package fr.ybo.transportsrennes;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import fr.ybo.transportsrennes.activity.MenuAccueil;
import fr.ybo.transportsrennes.adapters.FavoriAdapter;
import fr.ybo.transportsrennes.keolis.gtfs.database.DataBaseException;
import fr.ybo.transportsrennes.keolis.gtfs.modele.ArretFavori;
import fr.ybo.transportsrennes.util.LogYbo;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * @author ybonnel
 */
public class ListFavoris extends MenuAccueil.ListActivity {

	private static final LogYbo LOG_YBO = new LogYbo(ListFavoris.class);

	private void construireListe() throws DataBaseException {
		setListAdapter(new FavoriAdapter(getApplicationContext(), TransportsRennesApplication.getDataBaseHelper().select(new ArretFavori())));
		final ListView lv = getListView();
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(final AdapterView<?> adapterView, final View view, final int position, final long id) {
				final FavoriAdapter favoriAdapter = (FavoriAdapter) ((ListView) adapterView).getAdapter();
				final Intent intent = new Intent(ListFavoris.this, DetailArret.class);
				intent.putExtra("favori", favoriAdapter.getItem(position));
				startActivity(intent);
			}
		});
		lv.setTextFilterEnabled(true);
		registerForContextMenu(lv);
	}

	private Runnable runnableMajToRunOnUiThread = new Runnable() {
		public void run() {
		    ((FavoriAdapter) ListFavoris.this.getListAdapter()).majCalendar();
			((FavoriAdapter) ListFavoris.this.getListAdapter()).getFavoris().clear();
			((FavoriAdapter) ListFavoris.this.getListAdapter()).getFavoris()
					.addAll(TransportsRennesApplication.getDataBaseHelper().select(new ArretFavori()));
			((FavoriAdapter) ListFavoris.this.getListAdapter()).notifyDataSetChanged();
		}
	};

	private Runnable runnableMajHoraires = new Runnable() {
		public void run() {
			while (true) {
				try {
					LOG_YBO.debug("Dort pendant 20 secondes");
					TimeUnit.SECONDS.sleep(20);
					LOG_YBO.debug("Réveil, mise à jour des favoris");
					ListFavoris.this.runOnUiThread(runnableMajToRunOnUiThread);
				} catch (InterruptedException e) {
					LOG_YBO.debug("Fin du thread");
					break;
				}
			}
		}
	};

	private Thread threadCourant;

	@Override
	protected void onPause() {
		if (threadCourant != null) {
			LOG_YBO.debug("Arret du threadMiseAJour");
			threadCourant.interrupt();
			threadCourant = null;
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (threadCourant == null) {
			LOG_YBO.debug("Démarrage du threadMiseAJour");
			runnableMajToRunOnUiThread.run();
			threadCourant = new Thread(runnableMajHoraires);
			threadCourant.start();
		}
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listfavoris);
		construireListe();
		if (threadCourant == null) {
			LOG_YBO.debug("Démarrage du threadMiseAJour (dans onCreate)");
			threadCourant = new Thread(runnableMajHoraires);
			threadCourant.start();
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		if (v.getId() == android.R.id.list) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			ArretFavori favori = (ArretFavori) getListAdapter().getItem(info.position);
			menu.setHeaderTitle(favori.getNomArret());
			menu.add(Menu.NONE, R.id.supprimerFavori, 0, "Supprimer des favoris");
		}
	}


	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
			case R.id.supprimerFavori:
				ArretFavori favori = (ArretFavori) getListAdapter().getItem(info.position);
				TransportsRennesApplication.getDataBaseHelper().delete(favori);
				((FavoriAdapter) getListAdapter()).getFavoris().clear();
				((FavoriAdapter) getListAdapter()).getFavoris().addAll(TransportsRennesApplication.getDataBaseHelper().select(new ArretFavori()));
				((FavoriAdapter) getListAdapter()).notifyDataSetChanged();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}