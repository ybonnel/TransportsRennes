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

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.activity.commun.MenuAccueil;
import fr.ybo.transportsrennes.adapters.bus.NotifAdapter;
import fr.ybo.transportsrennes.application.TransportsRennesApplication;
import fr.ybo.transportsrennes.database.modele.Notification;
import fr.ybo.transportsrennes.util.UpdateTimeUtil;
import fr.ybo.transportsrennes.util.UpdateTimeUtil.UpdateTime;

import java.util.Calendar;

/**
 * @author ybonnel
 */
public class ListNotif extends MenuAccueil.ListActivity {

    private UpdateTimeUtil updateTimeUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listnotif);
        setListAdapter(new NotifAdapter(getApplicationContext(), TransportsRennesApplication.getDataBaseHelper().selectAll(Notification.class)));
        registerForContextMenu(getListView());
        updateTimeUtil = new UpdateTimeUtil(new UpdateTime() {

            public void update(Calendar calendar) {
                ((NotifAdapter) getListAdapter()).majCalendar();
                ((NotifAdapter) getListAdapter()).notifyDataSetChanged();
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
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == android.R.id.list) {
            menu.add(Menu.NONE, R.id.supprimerNotif, 0, getString(R.string.supprimerNotif));
        }
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.supprimerNotif:
                Notification notification = (Notification) getListAdapter().getItem(info.position);
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
