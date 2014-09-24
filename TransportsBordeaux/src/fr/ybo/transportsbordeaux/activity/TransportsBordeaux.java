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
package fr.ybo.transportsbordeaux.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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

import com.google.ads.AdRequest;
import com.google.ads.AdView;

import fr.ybo.database.DataBaseException;
import fr.ybo.transportsbordeaux.R;
import fr.ybo.transportsbordeaux.activity.bus.ListNotif;
import fr.ybo.transportsbordeaux.activity.loading.LoadingActivity;
import fr.ybo.transportsbordeaux.application.TransportsBordeauxApplication;
import fr.ybo.transportsbordeaux.database.TransportsBordeauxDatabase;
import fr.ybo.transportsbordeaux.tbcapi.TcbException;
import fr.ybo.transportsbordeaux.util.Version;
import fr.ybo.transportscommun.AbstractTransportsApplication;
import fr.ybo.transportscommun.activity.AccueilActivity;
import fr.ybo.transportscommun.activity.commun.UIUtils;
import fr.ybo.transportscommun.donnees.manager.gtfs.GestionZipKeolis;
import fr.ybo.transportscommun.donnees.modele.DernierMiseAJour;
import fr.ybo.transportscommun.util.Theme;

public class TransportsBordeaux extends AccueilActivity {

	private Theme currentTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		currentTheme = TransportsBordeauxApplication.getTheme(getApplicationContext());
        setContentView(R.layout.main);
		getActivityHelper().setupActionBar(R.menu.accueil_menu_items, R.menu.holo_accueil_menu_items);
        afficheMessage();
		verifierUpgrade();

        // Look up the AdView as a resource and load a request.
        ((AdView) this.findViewById(R.id.adView)).loadAd(new AdRequest());
    }

    @Override
    protected void onResume() {
        super.onResume();
		if (currentTheme != TransportsBordeauxApplication.getTheme(getApplicationContext())) {
			startActivity(new Intent(this, TransportsBordeaux.class));
			finish();
			return;
		}
        if (TransportsBordeauxApplication.isBaseNeuve()) {
            TransportsBordeauxApplication.setBaseNeuve(false);
            verifierUpgrade();
        }
    }

    private void afficheMessage() {
        boolean afficheMessage = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
                "TransportsBordeaux_dialog", true);
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
		if (UIUtils.isHoneycomb()) {
			textView.setTextColor(AbstractTransportsApplication.getTextColor(this));
		}
        textView.setText(spanned, TextView.BufferType.SPANNABLE);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        builder.setView(view);
		builder.setTitle(getString(R.string.titleTransportsBordeaux,
				Version.getVersionCourante(getApplicationContext())));
        builder.setCancelable(false);
        builder.setNeutralButton(getString(R.string.Terminer), new TransportsBordeaux.TerminerClickListener());
        builder.create().show();
    }

    private static class TerminerClickListener implements DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.cancel();
        }
    }

    private void saveAfficheMessage() {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putBoolean("TransportsBordeaux_dialog", false);
        editor.commit();
    }

    private void upgradeDatabase() {
		Intent intent = new Intent(this, LoadingActivity.class);
		intent.putExtra("operation", LoadingActivity.OPERATION_UPGRADE_DATABASE);
		startActivity(intent);
    }

    private void verifierUpgrade() {
		TransportsBordeauxDatabase dataBaseHelper = (TransportsBordeauxDatabase) TransportsBordeauxApplication
				.getDataBaseHelper();
		DernierMiseAJour dernierMiseAJour = null;
		try {
			dernierMiseAJour = dataBaseHelper.selectSingle(new DernierMiseAJour());
		} catch (DataBaseException exception) {
			dataBaseHelper.deleteAll(DernierMiseAJour.class);
		}
		Date dateDernierFichierKeolis = GestionZipKeolis.getLastUpdate(getResources(), R.raw.last_update);
        if (dernierMiseAJour == null || dernierMiseAJour.derniereMiseAJour == null
                || dateDernierFichierKeolis.after(dernierMiseAJour.derniereMiseAJour)) {
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
            runOnUiThread(new Runnable() {
                public void run() {
                    if (!isFinishing()) {
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                }
            });
        }
    }

    private static final int GROUP_ID = 0;
    private static final int MENU_ID = 1;
	private static final int MENU_NOTIF = 2;
	private static final int MENU_LOAD_LINES = 3;
	private static final int MENU_SHARE = 4;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItem item = menu.add(GROUP_ID, MENU_ID, Menu.NONE, R.string.menu_apropos);
        item.setIcon(android.R.drawable.ic_menu_info_details);
        MenuItem itemNotif = menu.add(GROUP_ID, MENU_NOTIF, Menu.NONE, R.string.notif);
        itemNotif.setIcon(android.R.drawable.ic_menu_agenda);
        MenuItem itemLoadLines = menu.add(GROUP_ID, MENU_LOAD_LINES, Menu.NONE, R.string.menu_loadLines);
        itemLoadLines.setIcon(android.R.drawable.ic_menu_save);
        MenuItem itemShare = menu.add(GROUP_ID, MENU_SHARE, Menu.NONE, R.string.menu_share);
        itemShare.setIcon(android.R.drawable.ic_menu_share);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case MENU_ID:
                showDialog();
                return true;
			case R.id.menu_plan:
				copieImageIfNotExists();
				Intent intentMap = new Intent(Intent.ACTION_VIEW);
				intentMap.setDataAndType(Uri.fromFile(new File(getFilesDir(), "tbc_carte_2014_2015.jpg")), "image/*");
				startActivity(intentMap);
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
            case MENU_NOTIF:
                startActivity(new Intent(this, ListNotif.class));
                break;
        }
        return false;
    }
    


	private void copieImageIfNotExists() {
		boolean fichierExistant = false;
		for (String nom : fileList()) {
			if ("tbc_carte_2014_2015.jpg".equals(nom)) {
				fichierExistant = true;
			} else if ("tbc_carte.jpg".equals(nom)) {
				deleteFile(nom);
			}
		}
		if (!fichierExistant) {
			InputStream inputStream = getResources().openRawResource(R.raw.tbc_carte_2014_2015);
			try {
				OutputStream outputStream = openFileOutput("tbc_carte_2014_2015.jpg", Context.MODE_WORLD_READABLE);
				try {
					byte[] buffre = new byte[50 * 1024];
					int result = inputStream.read(buffre);
					while (result != -1) {
						outputStream.write(buffre, 0, result);
						result = inputStream.read(buffre);
					}
				} catch (IOException e) {
					throw new TcbException("Erreur lors de la copie de l'image", e);
				} finally {
					try {
						outputStream.close();
					} catch (IOException ignore) {
					}
				}
			} catch (FileNotFoundException e) {
				throw new TcbException("Erreur lors de la copie de l'image", e);
			} finally {
				try {
					inputStream.close();
				} catch (IOException ignore) {
				}
			}
		}
	}

	private void loadAllLines() {
		Intent intent = new Intent(this, LoadingActivity.class);
		intent.putExtra("operation", LoadingActivity.OPERATION_LOAD_ALL_LINES);
		startActivity(intent);
	}
}
