package fr.ybo.transportsrennes;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import fr.ybo.transportsrennes.keolis.gtfs.database.DataBaseException;
import fr.ybo.transportsrennes.keolis.gtfs.database.DataBaseHelper;
import fr.ybo.transportsrennes.keolis.gtfs.modele.ArretFavori;

/**
 * @author ybonnel
 */
public class ListFavoris extends ListActivity {

	private DataBaseHelper dataBaseHelper = null;

	private void construireListe() throws DataBaseException {
		setListAdapter(new FavoriAdapter(getApplicationContext(), getDataBaseHelper().select(new ArretFavori())));
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

	private DataBaseHelper getDataBaseHelper() {
		if (dataBaseHelper == null) {
			dataBaseHelper = ((BusRennesApplication) getApplication()).getDataBaseHelper();
		}
		return dataBaseHelper;
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listfavoris);
		construireListe();
	}
}