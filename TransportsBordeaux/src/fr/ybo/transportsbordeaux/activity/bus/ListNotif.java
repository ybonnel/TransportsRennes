/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.ybo.transportsbordeaux.activity.bus;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import fr.ybo.transportsbordeaux.R;
import fr.ybo.transportsbordeaux.adapters.bus.NotifAdapter;
import fr.ybo.transportsbordeaux.application.TransportsBordeauxApplication;
import fr.ybo.transportsbordeaux.util.UpdateTimeUtil;
import fr.ybo.transportscommun.activity.commun.BaseActivity.BaseListActivity;
import fr.ybo.transportscommun.donnees.modele.Arret;
import fr.ybo.transportscommun.donnees.modele.Ligne;
import fr.ybo.transportscommun.donnees.modele.Notification;

/**
 * @author ybonnel
 */
public class ListNotif extends BaseListActivity {

    private UpdateTimeUtil updateTimeUtil;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listnotif);
		getActivityHelper().setupActionBar(R.menu.default_menu_items, R.menu.holo_default_menu_items);
        setListAdapter(new NotifAdapter(getApplicationContext(), TransportsBordeauxApplication.getDataBaseHelper().selectAll(Notification.class)));
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, final View view, final int i, final long l) {
                final Notification notification = (Notification) adapterView.getItemAtPosition(i);
                final Intent intent = new Intent(ListNotif.this, DetailArret.class);
                final Ligne ligne = Ligne.getLigne(notification.getLigneId());
                final Arret arret = Arret.getArret(notification.getArretId());
                intent.putExtra("ligne", ligne);
                intent.putExtra("idArret", notification.getArretId());
                intent.putExtra("nomArret", arret.nom);
                intent.putExtra("direction", notification.getDirection());
                startActivity(intent);
            }
        });
        registerForContextMenu(getListView());
        updateTimeUtil = new UpdateTimeUtil(new UpdateTimeUtil.UpdateTime() {

            @Override
            public void update() {
                ((NotifAdapter) getListAdapter()).majCalendar();
                ((BaseAdapter) getListAdapter()).notifyDataSetChanged();
            }
        }, this);
        updateTimeUtil.start();
    }

    @Override
    protected void onResume() {
        updateTimeUtil.start();
        super.onResume();
    }

    @Override
    protected void onPause() {
        updateTimeUtil.stop();
        super.onPause();
    }

    @Override
    public void onCreateContextMenu(final ContextMenu menu, final View v, final ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == android.R.id.list) {
            menu.add(Menu.NONE, R.id.supprimerNotif, 0, getString(R.string.supprimerNotif));
        }
    }


    @Override
    public boolean onContextItemSelected(final MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.supprimerNotif:
                final Notification notification = (Notification) getListAdapter().getItem(info.position);
                TransportsBordeauxApplication.getDataBaseHelper().delete(notification);
                ((NotifAdapter) getListAdapter()).getNotifications().clear();
                ((NotifAdapter) getListAdapter()).getNotifications().addAll(TransportsBordeauxApplication.getDataBaseHelper().selectAll(Notification.class));
                ((BaseAdapter) getListAdapter()).notifyDataSetChanged();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}
