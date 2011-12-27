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
package fr.ybo.transportsrennes.activity;

import java.util.Date;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.activity.bus.ListNotif;
import fr.ybo.transportsrennes.activity.loading.LoadingActivity;
import fr.ybo.transportsrennes.activity.map.AllOnMap;
import fr.ybo.transportsrennes.activity.pointsdevente.ListPointsDeVente;
import fr.ybo.transportsrennes.activity.preferences.PreferencesRennes;
import fr.ybo.transportsrennes.application.TransportsRennesApplication;
import fr.ybo.transportsrennes.database.TransportsRennesDatabase;
import fr.ybo.transportsrennes.database.modele.DernierMiseAJour;
import fr.ybo.transportsrennes.keolis.gtfs.files.GestionZipKeolis;
import fr.ybo.transportsrennes.util.CapptainFragmentActivity;


public class TransportsRennes extends CapptainFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        afficheMessage();
		verifierUpgrade();
    }

    private void afficheMessage() {
        boolean afficheMessage =
                PreferenceManager.getDefaultSharedPreferences(this).getBoolean("TransportsRennes141_dialog", true);
        if (afficheMessage) {
            showDialog();
            saveAfficheMessage();
        }

    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.infoapropos, null);
        TextView textView = (TextView) view.findViewById(R.id.textAPropos);
        Spanned spanned = Html.fromHtml(getString(R.string.dialogAPropos));
        textView.setText(spanned, TextView.BufferType.SPANNABLE);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        builder.setView(view);
        builder.setTitle(R.string.titleTransportsRennes);
        builder.setCancelable(false);
        builder.setNeutralButton(getString(R.string.Terminer), new TransportsRennes.TerminerClickListener());
        builder.create().show();
    }

    private static class TerminerClickListener implements DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.cancel();
        }
    }


    private void saveAfficheMessage() {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putBoolean("TransportsRennes141_dialog", false);
        editor.commit();
    }


    private void upgradeDatabase() {
		Intent intent = new Intent(this, LoadingActivity.class);
		intent.putExtra("operation", LoadingActivity.OPERATION_UPGRADE_DATABASE);
		startActivity(intent);
    }

    private void verifierUpgrade() {
        TransportsRennesDatabase dataBaseHelper = TransportsRennesApplication.getDataBaseHelper();
        DernierMiseAJour dernierMiseAJour = dataBaseHelper.selectSingle(new DernierMiseAJour());
        Date dateDernierFichierKeolis = GestionZipKeolis.getLastUpdate(getResources());
        if (dernierMiseAJour == null || dernierMiseAJour.derniereMiseAJour == null ||
                dateDernierFichierKeolis.after(dernierMiseAJour.derniereMiseAJour)) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(dernierMiseAJour == null ? R.string.premierLancement : R.string.majDispo));
            builder.setCancelable(false);
            builder.setPositiveButton(getString(R.string.oui), new Dialog.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    upgradeDatabase();
                }
            });
            if (dernierMiseAJour == null) {
                builder.setNegativeButton(getString(R.string.non), new Dialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                    }
                });
            } else {
                builder.setNegativeButton(getString(R.string.non), new Dialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
            }
			AlertDialog alert = builder.create();
			alert.show();
        }
    }

    private static final int GROUP_ID = 0;
    private static final int MENU_ID = 1;
    private static final int MENU_MAP_ID = 2;
    private static final int MENU_PREFS = 3;
    private static final int MENU_NOTIF = 4;
    private static final int MENU_SHARE = 5;
    private static final int MENU_LOAD_LINES = 6;
    private static final int MENU_TICKETS = 7;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItem item = menu.add(GROUP_ID, MENU_ID, Menu.NONE, R.string.menu_apropos);
        item.setIcon(android.R.drawable.ic_menu_info_details);
        MenuItem itemMap = menu.add(GROUP_ID, MENU_MAP_ID, Menu.NONE, R.string.menu_carte);
        itemMap.setIcon(android.R.drawable.ic_menu_mapmode);
        MenuItem itemPrefs = menu.add(GROUP_ID, MENU_PREFS, Menu.NONE, R.string.preferences);
        itemPrefs.setIcon(android.R.drawable.ic_menu_manage);
        MenuItem itemNotif = menu.add(GROUP_ID, MENU_NOTIF, Menu.NONE, R.string.notif);
        itemNotif.setIcon(android.R.drawable.ic_menu_agenda);
        MenuItem itemLoadLines = menu.add(GROUP_ID, MENU_LOAD_LINES, Menu.NONE, R.string.menu_loadLines);
        itemLoadLines.setIcon(android.R.drawable.ic_menu_save);
        MenuItem itemShare = menu.add(GROUP_ID, MENU_SHARE, Menu.NONE, R.string.menu_share);
        itemShare.setIcon(android.R.drawable.ic_menu_share);
        MenuItem itemPointDeVentes = menu.add(GROUP_ID, MENU_TICKETS, Menu.NONE, R.string.menu_tickets);
        itemPointDeVentes.setIcon(R.drawable.ic_menu_tickets);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case MENU_ID:
                showDialog();
                return true;
            case MENU_MAP_ID:
                Intent intentMap = new Intent(this, AllOnMap.class);
                startActivity(intentMap);
                return true;
            case MENU_TICKETS:
                Intent intentTickets = new Intent(this, ListPointsDeVente.class);
                startActivity(intentTickets);
                return true;
            case MENU_LOAD_LINES:
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setMessage(getString(R.string.loadAllLineAlert));
                alertBuilder.setCancelable(false);
                alertBuilder.setPositiveButton(getString(R.string.oui), new Dialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        loadAllLines();
                    }
                });
                alertBuilder.setNegativeButton(getString(R.string.non), new Dialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                alertBuilder.show();
                return true;
            case MENU_SHARE:
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.shareText));
                startActivity(Intent.createChooser(shareIntent, getString(R.string.app_name)));
                return true;
            case MENU_PREFS:
                startActivity(new Intent(this, PreferencesRennes.class));
                break;
            case MENU_NOTIF:
                startActivity(new Intent(this, ListNotif.class));
                break;
        }
        return false;
    }

    private void loadAllLines() {
		Intent intent = new Intent(this, LoadingActivity.class);
		intent.putExtra("operation", LoadingActivity.OPERATION_LOAD_ALL_LINES);
		startActivity(intent);
    }
}
