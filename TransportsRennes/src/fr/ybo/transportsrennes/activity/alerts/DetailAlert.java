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
package fr.ybo.transportsrennes.activity.alerts;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;
import fr.ybo.transportscommun.activity.commun.BaseActivity.BaseSimpleActivity;
import fr.ybo.transportscommun.util.IconeLigne;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.application.TransportsRennesApplication;
import fr.ybo.transportsrennes.keolis.modele.bus.Alert;

/**
 * Activitée permettant d'afficher les détails d'une station.
 *
 * @author ybonnel
 */
public class DetailAlert extends BaseSimpleActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailalert);
		getActivityHelper().setupActionBar(R.menu.default_menu_items, R.menu.holo_default_menu_items);
        final Alert alert = (Alert) getIntent().getExtras().getSerializable("alert");

        ((TextView) findViewById(R.id.titreAlert)).setText(alert != null ? alert.getTitleFormate() : null);
        if (!(alert != null && alert.lines.isEmpty())) {
            ((ImageView) findViewById(R.id.iconeLigne)).setImageResource(IconeLigne.getIconeResource(alert != null ? alert.lines.iterator().next() : null));
        }
        final Collection<String> arretsToBold = new HashSet<String>(20);
        for (final String line : alert.lines) {
            final Cursor cursor = TransportsRennesApplication.getDataBaseHelper().executeSelectQuery("select Arret.nom from Arret, Ligne, ArretRoute " + "where Ligne.nomCourt = :nomCourt and ArretRoute.ligneId = Ligne.id " + "and Arret.id = ArretRoute.arretId", Collections.singletonList(line));
            while (cursor.moveToNext()) {
                arretsToBold.add(cursor.getString(0));
            }
            cursor.close();
        }
        ((TextView) findViewById(R.id.detailAlert_Detail)).setText(Html.fromHtml(alert.getDetailFormatte(arretsToBold)));
    }

}
