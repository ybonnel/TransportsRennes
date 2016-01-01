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

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import fr.ybo.transportscommun.activity.commun.BaseActivity.BaseListActivity;
import fr.ybo.transportscommun.donnees.modele.Ligne;
import fr.ybo.transportscommun.util.IconeLigne;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.adapters.bus.LigneAdapter;
import fr.ybo.transportsrennes.application.TransportsRennesApplication;

/**
 * Activité affichant les lignes de bus..
 *
 * @author ybonnel
 */
public class BusShortcutPicker extends BaseListActivity {

    private void constructionListe() {
        final List<Ligne> lignes = TransportsRennesApplication.getDataBaseHelper().select(new Ligne(), "ordre");
        setListAdapter(new LigneAdapter(this, lignes));
        final ListView lv = getListView();
        lv.setFastScrollEnabled(true);
        lv.setTextFilterEnabled(true);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, final View view, final int position, final long id) {
                final Ligne ligne = (Ligne) adapterView.getItemAtPosition(position);
                setupShortcut(ligne);
                finish();
            }

        });
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bus);
        constructionListe();
    }


    private void setupShortcut(final Ligne ligne) {
        // First, set up the shortcut intent.
        final Intent shortcutIntent = new Intent(this, ListArret.class).putExtra("ligneId", ligne.id);

        // Then, set up the container intent (the response to the caller)
        final Parcelable iconResource = Intent.ShortcutIconResource.fromContext(this, IconeLigne.getIconeResource(ligne.nomCourt));
        final Intent intent = new Intent().putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent).putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.lineName, ligne.nomCourt)).putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconResource);

        // Now, return the result to the launcher
        setResult(RESULT_OK, intent);
    }
}
