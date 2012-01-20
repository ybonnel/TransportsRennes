package fr.ybo.transportsrennes.database;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;
import fr.ybo.moteurcsv.MoteurCsv;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.application.TransportsRennesApplication;
import fr.ybo.transportsrennes.database.modele.ArretFavori;

public class FavorisManager {

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

		File root = Environment.getExternalStorageDirectory();
		File repertoire = new File(root, "transportsrennes");
		if (!repertoire.exists()) {
			repertoire.mkdir();
		}
		File outputFile = new File(repertoire, FILE_NAME);
		if (outputFile.exists()) {
			outputFile.delete();
		}
		moteurCsv.writeFile(outputFile, TransportsRennesApplication.getDataBaseHelper().selectAll(ArretFavori.class),
				ArretFavori.class);
		Toast.makeText(context, R.string.exportResult, Toast.LENGTH_LONG).show();
	}

	public void load(Context context) {
		Toast.makeText(context, "Not yet implemented", Toast.LENGTH_LONG).show();
	}

}
