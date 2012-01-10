package fr.ybo.transportsrennes.activity.loading;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ubikod.capptain.android.sdk.activity.CapptainActivity;

import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.application.TransportsRennesApplication;
import fr.ybo.transportsrennes.database.modele.ArretFavori;
import fr.ybo.transportsrennes.database.modele.Ligne;
import fr.ybo.transportsrennes.keolis.LigneInexistanteException;
import fr.ybo.transportsrennes.keolis.gtfs.UpdateDataBase;
import fr.ybo.transportsrennes.util.IconeLigne;
import fr.ybo.transportsrennes.util.LoadingInfo;

public class LoadingActivity extends CapptainActivity {

	private TextView message;
	private ProgressBar loadingBar;

	private ImageView iconeLigne;

	private Handler handler = new Handler();

	private int operation;

	private boolean arretDemande = false;

	@Override
	protected void onCreate(Bundle bundle) {
		TransportsRennesApplication.majTheme(this);
		super.onCreate(bundle);
		setContentView(R.layout.loading);
		message = (TextView) findViewById(R.id.messageLoading);
		loadingBar = (ProgressBar) findViewById(R.id.loadingBar);
		iconeLigne = (ImageView) findViewById(R.id.iconeLigne);
		operation = getIntent().getIntExtra("operation", 1);
		switch (operation) {
			case OPERATION_UPGRADE_DATABASE:
				upgradeDatabase();
				break;
			case OPERATION_LOAD_ALL_LINES:
				loadAllLines();
				break;
			default:
				finish();
				break;
		}
	}

	// Verrue pour faire marcher en 1.6.
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((!(android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT)
				&& keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)) {
			onBackPressed();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onBackPressed() {
		// On ne fait rien.
		arretDemande = true;
	}

	private void upgradeDatabase() {
		message.setText(R.string.infoChargementGtfs);
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... pParams) {
				UpdateDataBase.updateIfNecessaryDatabase(getResources(), new LoadingInfo() {
					@Override
					public void etapeSuivante() {
						super.etapeSuivante();
						handler.post(new Runnable() {
							
							@Override
							public void run() {
								loadingBar.setProgress(100 * getEtapeCourante() / getNbEtape());
							}
						});
					}
				});
				Collection<String> ligneIds = new HashSet<String>();
				for (ArretFavori favori : TransportsRennesApplication.getDataBaseHelper().select(new ArretFavori())) {
					if (!ligneIds.contains(favori.ligneId)) {
						ligneIds.add(favori.ligneId);
					}
				}
				Ligne ligneSelect = new Ligne();
				for (String ligneId : ligneIds) {
					ligneSelect.id = ligneId;
					Ligne ligne = TransportsRennesApplication.getDataBaseHelper().selectSingle(ligneSelect);
					final String nomLigne = ligne.nomCourt;
					runOnUiThread(new Runnable() {
						public void run() {
							message.setText(getString(R.string.infoChargementGtfs)
									+ getString(R.string.chargementLigneFavori, nomLigne));
						}
					});
					try {
						UpdateDataBase.chargeDetailLigne(ligne, getResources());
					} catch (LigneInexistanteException ignore) {
					}
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				LoadingActivity.this.finish();
			}
		}.execute((Void[]) null);
	}

	private int nbLignesToLoad;
	private int ligneCourante = 0;

	private void loadAllLines() {
		message.setText(R.string.infoChargementGtfs);

		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... pParams) {
				List<Ligne> allLines = TransportsRennesApplication.getDataBaseHelper().select(new Ligne());
				Iterator<Ligne> itLignes = allLines.iterator();
				while (itLignes.hasNext()) {
					if (itLignes.next().isChargee()) {
						itLignes.remove();
					}
				}
				Collections.sort(allLines, new Comparator<Ligne>() {

					private Map<String, Double> mapScores = new HashMap<String, Double>();
					private Random random = new Random();

					@Override
					public int compare(Ligne lhs, Ligne rhs) {
						if (!mapScores.containsKey(lhs.id)) {
							mapScores.put(lhs.id, random.nextDouble());
						}
						if (!mapScores.containsKey(rhs.id)) {
							mapScores.put(rhs.id, random.nextDouble());
						}
						return mapScores.get(lhs.id).compareTo(mapScores.get(rhs.id));
					}
				});
				nbLignesToLoad = allLines.size();
				for (Ligne ligne : allLines) {
					if (arretDemande) {
						break;
					}
					final String nomLigne = ligne.nomCourt;
					handler.post(new Runnable() {
						public void run() {
							message.setText(getString(R.string.infoChargementGtfs) + '\n'
									+ getString(R.string.premierAccesLigne, nomLigne));
							iconeLigne.setVisibility(View.VISIBLE);
							iconeLigne.setImageResource(IconeLigne.getIconeResource(nomLigne));
							Animation rotation = new RotateAnimation(0, 360);
							rotation.setDuration(500);
							rotation.setRepeatCount(Animation.INFINITE);
							rotation.setRepeatMode(Animation.RESTART);
							iconeLigne.startAnimation(rotation);
						}
					});
					try {
						UpdateDataBase.chargeDetailLigne(ligne, getResources());
					} catch (LigneInexistanteException ignore) {
					}
					handler.post(new Runnable() {

						@Override
						public void run() {
							loadingBar.setProgress((++ligneCourante) * 100 / nbLignesToLoad);
						}
					});
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				LoadingActivity.this.finish();
			}
		}.execute((Void[]) null);
	}

	public static final int OPERATION_UPGRADE_DATABASE = 1;
	public static final int OPERATION_LOAD_ALL_LINES = 2;
}
