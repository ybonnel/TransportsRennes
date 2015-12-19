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
package fr.ybo.transportsrennes.activity.bus;

import java.util.Calendar;
import java.util.Set;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import fr.ybo.transportscommun.activity.commun.BaseActivity.BaseListActivity;
import fr.ybo.transportscommun.donnees.modele.Arret;
import fr.ybo.transportscommun.donnees.modele.Ligne;
import fr.ybo.transportscommun.donnees.modele.Notification;
import fr.ybo.transportscommun.util.UpdateTimeUtil;
import fr.ybo.transportscommun.util.UpdateTimeUtil.UpdateTime;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.adapters.bus.NotifAdapter;
import fr.ybo.transportsrennes.application.TransportsRennesApplication;

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
        setListAdapter(new NotifAdapter(getApplicationContext(), TransportsRennesApplication.getDataBaseHelper().selectAll(Notification.class)));
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, final View view, final int i, final long l) {
                final Notification notification = (Notification) adapterView.getItemAtPosition(i);
                final Ligne ligne = Ligne.getLigne(notification.getLigneId());
                final Arret arret = Arret.getArret(notification.getArretId());
                final Intent intent = new Intent(ListNotif.this, DetailArret.class).putExtra("ligne", ligne).putExtra("idArret", notification.getArretId()).putExtra("nomArret", arret.nom).putExtra("direction", notification.getDirection()).putExtra("macroDirection", notification.getMacroDirection());
                startActivity(intent);
            }
        });
        registerForContextMenu(getListView());
        updateTimeUtil = new UpdateTimeUtil(new UpdateTime() {

            @Override
            public void update(final Calendar calendar) {
                ((NotifAdapter) getListAdapter()).majCalendar();
                ((BaseAdapter) getListAdapter()).notifyDataSetChanged();
            }

			@Override
			public boolean updateSecond() {
				return false;
			}

			@Override
			public Set<Integer> secondesToUpdate() {
				return null;
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
                TransportsRennesApplication.getDataBaseHelper().delete(notification);
                ((NotifAdapter) getListAdapter()).getNotifications().clear();
                ((NotifAdapter) getListAdapter()).getNotifications().addAll(TransportsRennesApplication.getDataBaseHelper().selectAll(Notification.class));
                ((BaseAdapter) getListAdapter()).notifyDataSetChanged();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}
