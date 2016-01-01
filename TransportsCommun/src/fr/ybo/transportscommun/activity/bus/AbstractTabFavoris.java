package fr.ybo.transportscommun.activity.bus;

import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import fr.ybo.transportscommun.AbstractTransportsApplication;
import fr.ybo.transportscommun.R;
import fr.ybo.transportscommun.activity.commun.BaseActivity.BaseFragmentActivity;
import fr.ybo.transportscommun.activity.commun.BaseActivity.BaseTabFragmentActivity;
import fr.ybo.transportscommun.donnees.manager.FavorisManager;
import fr.ybo.transportscommun.donnees.modele.ArretFavori;
import fr.ybo.transportscommun.donnees.modele.GroupeFavori;

public abstract class AbstractTabFavoris extends BaseTabFragmentActivity {

    protected abstract int getLayout();

    protected abstract void setupActionBar();

    protected abstract Class<? extends BaseFragmentActivity> getListFavorisForNoGroupClass();

    protected abstract Class<? extends ListFragment> getListFavoris();

    protected abstract void loadFavoris();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        setupActionBar();
        final List<GroupeFavori> groupes = AbstractTransportsApplication.getDataBaseHelper().selectAll(GroupeFavori.class);
        if (groupes.isEmpty()) {
            final Intent intent = new Intent(this, getListFavorisForNoGroupClass());
            startActivity(intent);
            finish();
            return;
        }

        configureTabs();

        addTab("all", getString(R.string.all), getListFavoris());
        for (final GroupeFavori groupe : groupes) {
            final Bundle args = new Bundle();
            args.putString("groupe", groupe.name);
            addTab(groupe.name, groupe.name, getListFavoris(), args);
        }

        setCurrentTab(savedInstanceState);
        if (FavorisManager.hasFavorisToLoad()) {
            loadFavoris();
        }
    }

    private static final int GROUP_ID = 0;
    private static final int MENU_AJOUTER = 1;
    private static final int MENU_SUPPRIMER = 2;

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(GROUP_ID, MENU_AJOUTER, Menu.NONE, R.string.ajouterGroupe).setIcon(android.R.drawable.ic_menu_add);
        menu.add(GROUP_ID, MENU_SUPPRIMER, Menu.NONE, R.string.suprimerGroupe).setIcon(android.R.drawable.ic_menu_close_clear_cancel);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if ("all".equals(getCurrentTab())) {
            menu.findItem(MENU_SUPPRIMER).setVisible(false);
        } else {
            menu.findItem(MENU_SUPPRIMER).setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.menu_export) {
            FavorisManager.INSTANCE.export(this);
        } else if (item.getItemId() == R.id.menu_import) {
            FavorisManager.INSTANCE.load(this);
            startActivity(new Intent(this, getClass()));
            finish();
        } else if (item.getItemId() == MENU_SUPPRIMER) {
            final ArretFavori arretFavori = new ArretFavori();
            arretFavori.groupe = getCurrentTab();
            for (final ArretFavori favori : AbstractTransportsApplication.getDataBaseHelper().select(arretFavori)) {
                favori.groupe = "";
                AbstractTransportsApplication.getDataBaseHelper().update(favori);
            }
            final GroupeFavori groupeFavori = new GroupeFavori();
            groupeFavori.name = getCurrentTab();
            AbstractTransportsApplication.getDataBaseHelper().delete(groupeFavori);
            startActivity(new Intent(this, getClass()));
            finish();
            return true;
        } else if (item.getItemId() == MENU_AJOUTER) {
            createDialogAjoutGroupe();
            return true;
        }
        return false;
    }

    private void createDialogAjoutGroupe() {
        final TextView input = new EditText(this);
        new AlertDialog.Builder(this).setView(input).setPositiveButton(R.string.ajouter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int whichButton) {
                final String value = input.getText().toString().trim();
                if (value.isEmpty()) {
                    Toast.makeText(AbstractTabFavoris.this, R.string.groupeObligatoire, Toast.LENGTH_LONG)
                            .show();
                    return;
                }
                final GroupeFavori groupeFavori = new GroupeFavori();
                groupeFavori.name = value;
                if (!AbstractTransportsApplication.getDataBaseHelper().select(groupeFavori).isEmpty()
                        || value.equals(getString(R.string.all))) {
                    Toast.makeText(AbstractTabFavoris.this, R.string.groupeExistant, Toast.LENGTH_LONG).show();
                    return;
                }
                AbstractTransportsApplication.getDataBaseHelper().insert(groupeFavori);
                startActivity(new Intent(AbstractTabFavoris.this, AbstractTabFavoris.this.getClass()));
                finish();
            }
        }).setNegativeButton(R.string.annuler, new MyOnClickListener()).show();
    }

    private static class MyOnClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(final DialogInterface dialog, final int whichButton) {
            dialog.cancel();
        }
    }
}
