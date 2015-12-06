package fr.ybo.transportscommun.donnees.manager;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;
import fr.ybo.moteurcsv.MoteurCsv;
import fr.ybo.moteurcsv.exception.MoteurCsvException;
import fr.ybo.transportscommun.AbstractTransportsApplication;
import fr.ybo.transportscommun.R;
import fr.ybo.transportscommun.donnees.modele.ArretFavori;
import fr.ybo.transportscommun.donnees.modele.Ligne;
import fr.ybo.transportscommun.util.LogYbo;

public final class FavorisManager {

	private static final LogYbo LOG_YBO = new LogYbo(ArretFavori.class);

	private static FavorisManager instance;

	private static final String FILE_NAME = "arrets_favoris.txt";

	private final MoteurCsv moteurCsv;

	private FavorisManager() {
		final List<Class<?>> classes = new ArrayList<Class<?>>();
		classes.add(ArretFavori.class);
		moteurCsv = new MoteurCsv(classes);
	}

	public static synchronized FavorisManager getInstance() {
		if (instance == null) {
			instance = new FavorisManager();
		}
		return instance;
	}

	public void export(final Context context) {
		if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			Toast.makeText(context, R.string.exportErreurSd, Toast.LENGTH_LONG).show();
			return;
		}

		final File outputFile = openCsvFile();
		if (outputFile.exists()) {
			outputFile.delete();
		}
		try {
			moteurCsv.writeFile(outputFile,
					AbstractTransportsApplication.getDataBaseHelper().selectAll(ArretFavori.class), ArretFavori.class);
		} catch (final MoteurCsvException erreurEcriture) {
			LOG_YBO.erreur("Erreur à l'écriture du fichier", erreurEcriture);
			Toast.makeText(context, R.string.exportErreurSd, Toast.LENGTH_LONG).show();
			return;
		}
		Toast.makeText(
				context,
				context.getString(R.string.exportResult, AbstractTransportsApplication.getDonnesSpecifiques()
						.getApplicationName()), Toast.LENGTH_LONG).show();
	}

	public void load(final Context context) {
		if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			Toast.makeText(context, R.string.importErreurSd, Toast.LENGTH_LONG).show();
			return;
		}

		final BufferedReader bufReader = openCsvFileAsBufReader(context);
		if (bufReader == null) {
			return;
		}
		try {
			moteurCsv.parseFileAndInsert(bufReader, ArretFavori.class, new InsertArretFavori());
			Toast.makeText(context, R.string.importResult, Toast.LENGTH_SHORT).show();
		} catch (final Exception exception) {
			LOG_YBO.erreur("Une erreur pendant l'import : ", exception);
			Toast.makeText(context, context.getString(R.string.importErreurGenerique, exception.getMessage()),
					Toast.LENGTH_LONG).show();
		} finally {
			closeBufReader(bufReader);
		}
	}

	private static void closeBufReader(final Closeable bufReader) {
		try {
			bufReader.close();
		} catch (final IOException ignore) {
		}
	}

	private static BufferedReader openCsvFileAsBufReader(final Context context) {
		try {
			return new BufferedReader(new FileReader(openCsvFile()));
		} catch (final FileNotFoundException e) {
			Toast.makeText(
					context,
					context.getString(R.string.importErreurFichierNonPresent, AbstractTransportsApplication
							.getDonnesSpecifiques().getApplicationName()), Toast.LENGTH_LONG).show();
			return null;
		}
	}

	private static File openCsvFile() {
		final File root = Environment.getExternalStorageDirectory();
		final File repertoire = new File(root, AbstractTransportsApplication.getDonnesSpecifiques().getApplicationName());
		if (!repertoire.exists()) {
			repertoire.mkdir();
		}
		return new File(repertoire, FILE_NAME);
	}

	public static boolean hasFavorisToLoad() {
		for (final ArretFavori favori : AbstractTransportsApplication.getDataBaseHelper().selectAll(ArretFavori.class)) {
			final Ligne ligne = Ligne.getLigne(favori.ligneId);
			if (ligne != null && !ligne.isChargee()) {
				return true;
			}
		}
		return false;
	}

}
