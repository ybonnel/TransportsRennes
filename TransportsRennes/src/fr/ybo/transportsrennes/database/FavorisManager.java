package fr.ybo.transportsrennes.database;

import java.io.BufferedReader;
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
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.application.TransportsRennesApplication;
import fr.ybo.transportsrennes.database.modele.ArretFavori;
import fr.ybo.transportsrennes.util.LogYbo;

public class FavorisManager {

	private static final LogYbo LOG_YBO = new LogYbo(ArretFavori.class);

	private static FavorisManager instance = null;

	private static final String FILE_NAME = "arrets_favoris.txt";

	private final MoteurCsv moteurCsv;

	private FavorisManager() {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		classes.add(ArretFavori.class);
		moteurCsv = new MoteurCsv(classes);
	}

	synchronized public static FavorisManager getInstance() {
		if (instance == null) {
			instance = new FavorisManager();
		}
		return instance;
	}

	public void export(Context context) {
		if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			Toast.makeText(context, R.string.exportErreurSd, Toast.LENGTH_LONG).show();
			return;
		}

		File outputFile = openCsvFile();
		if (outputFile.exists()) {
			outputFile.delete();
		}
		moteurCsv.writeFile(outputFile, TransportsRennesApplication.getDataBaseHelper().selectAll(ArretFavori.class),
				ArretFavori.class);
		Toast.makeText(context, R.string.exportResult, Toast.LENGTH_LONG).show();
	}

	public void load(Context context) {
		if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			Toast.makeText(context, R.string.importErreurSd, Toast.LENGTH_LONG).show();
			return;
		}

		BufferedReader bufReader = openCsvFileAsBufReader(context);
		if (bufReader == null) {
			return;
		}
		try {
			moteurCsv.parseFileAndInsert(bufReader, ArretFavori.class, new InsertArretFavori());
			Toast.makeText(context, R.string.importResult, Toast.LENGTH_SHORT).show();
		} catch (Exception exception) {
			LOG_YBO.erreur("Une erreur pendant l'import : ", exception);
			Toast.makeText(context, context.getString(R.string.importErreurGenerique, exception.getMessage()),
					Toast.LENGTH_LONG).show();
		} finally {
			closeBufReader(bufReader);
		}
	}

	private void closeBufReader(BufferedReader bufReader) {
		try {
			bufReader.close();
		} catch (IOException ignore) {
		}
	}

	private BufferedReader openCsvFileAsBufReader(Context context) {
		try {
			return new BufferedReader(new FileReader(openCsvFile()));
		} catch (FileNotFoundException e) {
			Toast.makeText(context, R.string.importErreurFichierNonPresent, Toast.LENGTH_LONG).show();
			return null;
		}
	}

	private File openCsvFile() {
		File root = Environment.getExternalStorageDirectory();
		File repertoire = new File(root, "transportsrennes");
		if (!repertoire.exists()) {
			repertoire.mkdir();
		}
		return new File(repertoire, FILE_NAME);
	}

}
