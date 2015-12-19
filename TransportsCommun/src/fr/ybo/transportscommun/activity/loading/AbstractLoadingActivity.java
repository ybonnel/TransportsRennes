package fr.ybo.transportscommun.activity.loading;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import fr.ybo.transportscommun.AbstractTransportsApplication;
import fr.ybo.transportscommun.R;
import fr.ybo.transportscommun.donnees.manager.gtfs.UpdateDataBase;
import fr.ybo.transportscommun.donnees.modele.ArretFavori;
import fr.ybo.transportscommun.donnees.modele.DernierMiseAJour;
import fr.ybo.transportscommun.donnees.modele.Ligne;
import fr.ybo.transportscommun.util.IconeLigne;
import fr.ybo.transportscommun.util.LoadingInfo;
import fr.ybo.transportscommun.util.LogYbo;
import fr.ybo.transportscommun.util.NoSpaceLeftException;

public abstract class AbstractLoadingActivity extends Activity {

	private static final LogYbo LOG_YBO = new LogYbo(AbstractLoadingActivity.class);

	private TextView message;
	private ProgressBar loadingBar;

	private ImageView iconeLigne;

	private final Handler handler = new Handler();

	private boolean arretDemande;

	@Override
	protected void onCreate(final Bundle bundle) {
		AbstractTransportsApplication.majTheme(this);
		super.onCreate(bundle);
		setContentView(R.layout.loading);
		message = (TextView) findViewById(R.id.messageLoading);
		loadingBar = (ProgressBar) findViewById(R.id.loadingBar);
		iconeLigne = (ImageView) findViewById(R.id.iconeLigne);
		final int operation = getIntent().getIntExtra("operation", 1);
		switch (operation) {
			case OPERATION_UPGRADE_DATABASE:
				upgradeDatabase();
				break;
			case OPERATION_LOAD_ALL_LINES:
				loadAllLines();
				break;
			case OPERATION_LOAD_FAVORIS:
				loadFavoris();
				break;
			default:
				finish();
				break;
		}
	}

	// Verrue pour faire marcher en 1.6.
	@Override
	public boolean onKeyDown(final int keyCode, final KeyEvent event) {
		if (!(Build.VERSION.SDK_INT > Build.VERSION_CODES.DONUT)
				&& keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
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

	protected abstract int getInfoChangementGtfs();

	protected abstract int getChargementLigneFavori();

	protected abstract Class<?> getRawClass();

	protected abstract int getErreurNoSpaceLeft();

	protected abstract int getLastUpdate();

	protected abstract int getPremierAccesLigne();

	private void loadFavoris() {
		message.setText(getInfoChangementGtfs());
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(final Void... pParams) {
				final Collection<String> ligneIds = new HashSet<String>();
				for (final ArretFavori favori : AbstractTransportsApplication.getDataBaseHelper()
						.selectAll(ArretFavori.class)) {
					if (!ligneIds.contains(favori.ligneId)) {
						ligneIds.add(favori.ligneId);
					}
				}

				if (!ligneIds.isEmpty()) {
					int count = 0;
					final Ligne ligneSelect = new Ligne();
					for (final String ligneId : ligneIds) {
						ligneSelect.id = ligneId;
						final Ligne ligne = AbstractTransportsApplication.getDataBaseHelper().selectSingle(ligneSelect);
						final String nomLigne = ligne.nomCourt;
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								message.setText(getString(getInfoChangementGtfs())
										+ getString(getChargementLigneFavori(), nomLigne));
							}
						});
						try {
							UpdateDataBase.chargeDetailLigne(getRawClass(), ligne, getResources());
						} catch (final NoSpaceLeftException e) {
							Toast.makeText(AbstractLoadingActivity.this, getErreurNoSpaceLeft(), Toast.LENGTH_LONG)
									.show();
						}
						count++;
						final int progress = 100 * count / ligneIds.size();
						handler.post(new Runnable() {

							@Override
							public void run() {
								loadingBar.setProgress(progress);
							}
						});
					}
				}
				return null;
			}

			@Override
			protected void onPostExecute(final Void result) {
				super.onPostExecute(result);
				finish();
			}
		}.execute((Void[]) null);
	}

	private void upgradeDatabase() {
		message.setText(getInfoChangementGtfs());
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(final Void... pParams) {
				try {
					UpdateDataBase.updateIfNecessaryDatabase(getLastUpdate(), getResources(), new LoadingInfo() {
						@Override
						public void etapeSuivante() {
							super.etapeSuivante();
							final int progress = 100 * getEtapeCourante() / getNbEtape();
							handler.post(new Runnable() {

								@Override
								public void run() {
									loadingBar.setProgress(progress);
								}
							});
						}
					});
				} catch (final NoSpaceLeftException e) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(AbstractLoadingActivity.this, getErreurNoSpaceLeft(), Toast.LENGTH_LONG)
									.show();
						}
					});
					AbstractTransportsApplication.getDataBaseHelper().deleteAll(DernierMiseAJour.class);
					return null;
				}
				final Collection<String> ligneIds = new HashSet<String>();
				for (final ArretFavori favori : AbstractTransportsApplication.getDataBaseHelper().select(new ArretFavori())) {
					if (!ligneIds.contains(favori.ligneId)) {
						ligneIds.add(favori.ligneId);
					}
				}
				final Ligne ligneSelect = new Ligne();
				for (final String ligneId : ligneIds) {
					ligneSelect.id = ligneId;
					final Ligne ligne = AbstractTransportsApplication.getDataBaseHelper().selectSingle(ligneSelect);
					if (ligne == null) {
						LOG_YBO.debug("La ligne " + ligneId + " n'existe plus, suppression des favoris associés");
						final ArretFavori favori = new ArretFavori();
						favori.ligneId = ligneId;
						AbstractTransportsApplication.getDataBaseHelper().delete(favori);
						continue;
					}
					final String nomLigne = ligne.nomCourt;
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							message.setText(getString(getInfoChangementGtfs())
									+ getString(getChargementLigneFavori(), nomLigne));
						}
					});
					try {
						UpdateDataBase.chargeDetailLigne(getRawClass(), ligne, getResources());
					} catch (final NoSpaceLeftException e) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(AbstractLoadingActivity.this, getErreurNoSpaceLeft(), Toast.LENGTH_LONG)
										.show();
							}
						});
					}
				}
				return null;
			}

			@Override
			protected void onPostExecute(final Void result) {
				super.onPostExecute(result);
				finish();
			}
		}.execute((Void[]) null);
	}

	private int nbLignesToLoad;
	private int ligneCourante;

	private void loadAllLines() {
		message.setText(getInfoChangementGtfs());

		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(final Void... pParams) {
				final List<Ligne> allLines = AbstractTransportsApplication.getDataBaseHelper().select(new Ligne());
				final Iterator<Ligne> itLignes = allLines.iterator();
				while (itLignes.hasNext()) {
					if (itLignes.next().isChargee()) {
						itLignes.remove();
					}
				}
				Collections.sort(allLines, new LigneComparator());
				nbLignesToLoad = allLines.size();
				for (final Ligne ligne : allLines) {
					if (arretDemande) {
						break;
					}
					final String nomLigne = ligne.nomCourt;
					handler.post(new Runnable() {
						@Override
						public void run() {
							message.setText(getString(getInfoChangementGtfs()) + '\n'
									+ getString(getPremierAccesLigne(), nomLigne));
							iconeLigne.setVisibility(View.VISIBLE);
							iconeLigne.setImageResource(IconeLigne.getIconeResource(nomLigne));
							final Animation rotation = new RotateAnimation(0, 360);
							rotation.setDuration(500);
							rotation.setRepeatCount(Animation.INFINITE);
							rotation.setRepeatMode(Animation.RESTART);
							iconeLigne.startAnimation(rotation);
						}
					});
					try {
						UpdateDataBase.chargeDetailLigne(getRawClass(), ligne, getResources());
					} catch (final NoSpaceLeftException e) {
						handler.post(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(AbstractLoadingActivity.this, getErreurNoSpaceLeft(), Toast.LENGTH_LONG)
										.show();
							}
						});
					}
					handler.post(new Runnable() {

						@Override
						public void run() {
							++ligneCourante;
							loadingBar.setProgress(ligneCourante * 100 / nbLignesToLoad);
						}
					});
				}
				return null;
			}

			@Override
			protected void onPostExecute(final Void result) {
				super.onPostExecute(result);
				finish();
			}
		}.execute((Void[]) null);
	}

	public static final int OPERATION_UPGRADE_DATABASE = 1;
	public static final int OPERATION_LOAD_ALL_LINES = 2;
	public static final int OPERATION_LOAD_FAVORIS = 3;

	private static class LigneComparator implements Comparator<Ligne> {

		private final Map<String, Double> mapScores = new HashMap<String, Double>();
		private final Random random = new Random();

		@Override
        public int compare(final Ligne lhs, final Ligne rhs) {
            if (!mapScores.containsKey(lhs.id)) {
                mapScores.put(lhs.id, random.nextDouble());
            }
            if (!mapScores.containsKey(rhs.id)) {
                mapScores.put(rhs.id, random.nextDouble());
            }
            return mapScores.get(lhs.id).compareTo(mapScores.get(rhs.id));
        }
	}
}
