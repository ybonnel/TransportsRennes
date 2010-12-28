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
import fr.ybo.transportsrennes.adapters.FavoriAdapter;
import fr.ybo.transportsrennes.keolis.gtfs.database.DataBaseException;
import fr.ybo.transportsrennes.keolis.gtfs.modele.ArretFavori;

/**
 * @author ybonnel
 */
public class ListFavoris extends ListActivity {

	private void construireListe() throws DataBaseException {
		setListAdapter(new FavoriAdapter(getApplicationContext(), BusRennesApplication.getDataBaseHelper().select(new ArretFavori())));
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

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listfavoris);
		construireListe();
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
				BusRennesApplication.getDataBaseHelper().delete(favori);
				((FavoriAdapter) getListAdapter()).getFavoris().clear();
				((FavoriAdapter) getListAdapter()).getFavoris().addAll(BusRennesApplication.getDataBaseHelper().select(new ArretFavori()));
				((FavoriAdapter) getListAdapter()).notifyDataSetChanged();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}