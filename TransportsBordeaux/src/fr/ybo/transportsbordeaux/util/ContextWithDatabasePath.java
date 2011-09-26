package fr.ybo.transportsbordeaux.util;

import java.io.File;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.os.Environment;
import fr.ybo.transportsbordeaux.tbc.TcbException;

public class ContextWithDatabasePath extends ContextWrapper {

	private static final LogYbo LOG_YBO = new LogYbo(ContextWithDatabasePath.class);

	public ContextWithDatabasePath(Context context) {
		super(context);
	}

	public File getDatabasePath(String name) {
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			File root = Environment.getExternalStorageDirectory();
			File repertoire = new File(root, ".transportsbordeaux");
			File outputFile = new File(repertoire, name);
			LOG_YBO.debug("Répertoire pour la base de données : " + outputFile.getAbsolutePath());
			return outputFile;
		}
		throw new TcbException("Impossible de créer la base sur la carte.");
	}

	public SQLiteDatabase openOrCreateDatabase(String name, int mode, CursorFactory factory) {
		File dbFile = getDatabasePath(name);
		dbFile.getParentFile().mkdirs();
		SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbFile, factory);
		return db;
	}

}
