package fr.ybo.transportsrennes.keolis.gtfs.files;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import fr.ybo.transportsrennes.keolis.gtfs.database.DataBaseException;
import fr.ybo.transportsrennes.keolis.gtfs.database.DataBaseHelper;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Route;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Trip;
import fr.ybo.transportsrennes.keolis.gtfs.moteur.ErreurMoteurCsv;
import fr.ybo.transportsrennes.keolis.gtfs.moteur.MoteurCsv;
import fr.ybo.transportsrennes.util.ChangeMessage;
import fr.ybo.transportsrennes.util.LogYbo;

public final class GestionZipKeolis {

	private static final LogYbo LOG_YBO = new LogYbo(GestionZipKeolis.class);
	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMdd");

	private static final int NB_JOURS_RECHERCHE = 200;

	private static final String URL_RELATIVE = "fileadmin/OpenDataFiles/GTFS/GTFS-";
	private static final String URL_KEOLIS = "http://data.keolis-rennes.com/";
	private static final String BASE_URL = URL_KEOLIS + URL_RELATIVE;
	private static final String URL_DONNEES_TELECHARGEABLES = URL_KEOLIS + "fr/les-donnees/donnees-telechargeables.html";
	private static final String EXTENSION_URL = ".zip";

	public static boolean fichierRoutesPresents(final Context context, final List<Route> routes) {
		for (final Route route : routes) {
			final File file = new File(context.getExternalFilesDir(null), "stopTimes" + route.getId() + ".txt");
			if (!file.exists()) {
				return false;
			}
		}
		return true;
	}

	public static void getAndParseZipKeolis(final Date dateLastUpdate, final Context context, final MoteurCsv moteur,
			final DataBaseHelper dataBaseHelper, final ProgressDialog progressDialog, final Activity currentActivity)
			throws ErreurGestionFiles, ErreurMoteurCsv, DataBaseException {
		try {
			LOG_YBO.debug("Suppression des fichiers stopTimes...");
			for (final File file : context.getExternalFilesDir(null).listFiles(new FilenameFilter() {
				public boolean accept(final File dir, final String filename) {
					return filename.startsWith("stopTimes");
				}
			})) {
				file.delete();
			}
			LOG_YBO.debug("Debut copieContenuZipToSdCard...");
			final HttpURLConnection connection = openHttpConnection(dateLastUpdate);
			final ZipInputStream zipInputStream = new ZipInputStream(connection.getInputStream());
			ZipEntry zipEntry;
			String ligne;
			BufferedReader bufReader;
			final File fichierTmp = getNewFile(context, "stopTimes.tmp");
			BufferedWriter bufWriter = getBufferedWriterFromFile(fichierTmp);
			while ((zipEntry = zipInputStream.getNextEntry()) != null) {
				LOG_YBO.debug("Debut du traitement du fichier " + zipEntry.getName());
				if (progressDialog != null) {
					currentActivity
							.runOnUiThread(new ChangeMessage(progressDialog, "Traitement du fichier " + zipEntry.getName()));
				}
				bufReader = new BufferedReader(new InputStreamReader(zipInputStream), 8 * 1024);
				if (!"stop_times.txt".equals(zipEntry.getName())) {
					moteur.nouveauFichier(zipEntry.getName(), bufReader.readLine());
					dataBaseHelper.beginTransaction();
				} else {
					bufWriter.write(bufReader.readLine());
					bufWriter.write('\n');
				}
				while ((ligne = bufReader.readLine()) != null) {
					if (!"stop_times.txt".equals(zipEntry.getName())) {
						dataBaseHelper.insert(moteur.creerObjet(ligne));
					} else {
						bufWriter.write(ligne);
						bufWriter.write('\n');
					}
				}
				if (!"stop_times.txt".equals(zipEntry.getName())) {
					dataBaseHelper.endTransaction();
				}
				LOG_YBO.debug("Fin du traitement du fichier " + zipEntry.getName());
			}
			dataBaseHelper.close();
			bufWriter.close();
			connection.disconnect();
			LOG_YBO.debug("Debut du traitement du fichier stopTimes.txt");

			if (progressDialog != null) {
				currentActivity.runOnUiThread(new ChangeMessage(progressDialog,
						"D�coupage du fichier stopTimes.txt pour chargement opportuniste"));
			}
			LOG_YBO.debug("Mise en place des map de refs");

			final Map<String, BufferedWriter> mapTripIdFichier = new HashMap<String, BufferedWriter>();
			final List<Route> routes = dataBaseHelper.select(new Route());
			final List<BufferedWriter> routesFiles = new ArrayList<BufferedWriter>();
			final Trip tripRef = new Trip();
			for (final Route route : routes) {
				bufWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(
						context.getExternalFilesDir(null), "stopTimes" + route.getId() + ".txt"))), 64 * 1024);
				routesFiles.add(bufWriter);
				tripRef.setRouteId(route.getId());
				for (final Trip trip : dataBaseHelper.select(tripRef)) {
					mapTripIdFichier.put(trip.getId(), bufWriter);
				}
			}
			LOG_YBO.debug("Lecture du fichier");
			bufReader = new BufferedReader(new FileReader(fichierTmp), 8 * 1024);
			final String entete = bufReader.readLine();
			for (final BufferedWriter writer : routesFiles) {
				writer.write(entete);
				writer.write('\n');
			}
			String tripId;
			while ((ligne = bufReader.readLine()) != null) {
				tripId = ligne.substring(0, 7);
				if (mapTripIdFichier.containsKey(tripId)) {
					mapTripIdFichier.get(tripId).write(ligne);
					mapTripIdFichier.get(tripId).write('\n');
				} else {
					LOG_YBO.erreur("Le trip " + tripId + " est inconnu.");
				}
			}
			LOG_YBO.debug("Fin du traitement du fichier stopTimes.txt");
			for (final BufferedWriter bufToClose : routesFiles) {
				bufToClose.close();
			}
			fichierTmp.delete();
			LOG_YBO.debug("Fin copieContenuZipToSdCard.");
		} catch (final IOException e) {
			throw new ErreurGestionFiles(e);
		}
	}

	private static BufferedWriter getBufferedWriterFromFile(final File file) throws ErreurGestionFiles {
		try {
			return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)), 8 * 1024);
		} catch (final FileNotFoundException e) {
			throw new ErreurGestionFiles(e);
		}
	}

	public static Date getLastUpdate() {

		try {
			final HttpURLConnection connection = (HttpURLConnection) new URL(URL_DONNEES_TELECHARGEABLES).openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.connect();
			final BufferedReader bufReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			final String chaineRecherchee = URL_RELATIVE;
			String ligne;
			while ((ligne = bufReader.readLine()) != null) {
				if (ligne.contains(chaineRecherchee)) {
					final String chaineDate = ligne.substring(ligne.indexOf(chaineRecherchee) + chaineRecherchee.length(),
							ligne.indexOf(chaineRecherchee) + chaineRecherchee.length() + 8);
					return SDF.parse(chaineDate);
				}
			}

		} catch (final Exception e) {
			e.printStackTrace();
		}
		LOG_YBO.erreur("Aucune date n'a pu �tre trouv�e avec la m�thode rapide, passage en mode brute-force");
		return getLastUpdateBruteForce();
	}

	public static Date getLastUpdateBruteForce() {
		final GregorianCalendar calendar = new GregorianCalendar();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		HttpURLConnection connection;
		int nbJours = 0;
		while (nbJours < NB_JOURS_RECHERCHE) {
			try {
				connection = openHttpConnection(calendar.getTime());
				connection.getInputStream();
				return calendar.getTime();
			} catch (final IOException ioEx) {
				calendar.add(Calendar.DAY_OF_YEAR, -1);
				nbJours++;
			} catch (final ErreurGestionFiles exception) {
				calendar.add(Calendar.DAY_OF_YEAR, -1);
				nbJours++;
			}
		}
		return null;
	}

	private static File getNewFile(final Context context, final String name) {
		final File fichier = new File(context.getExternalFilesDir(null), name);
		if (fichier.exists()) {
			fichier.delete();
		}
		return fichier;
	}

	private static URL getUrlKeolisFromDate(final Date date) throws ErreurGestionFiles {
		try {
			LOG_YBO.debug("Recherche du fichier " + BASE_URL + SDF.format(date) + EXTENSION_URL);
			return new URL(BASE_URL + SDF.format(date) + EXTENSION_URL);
		} catch (final MalformedURLException e) {
			throw new ErreurGestionFiles(e);
		}
	}

	private static HttpURLConnection openHttpConnection(final Date dateFileKeolis) throws ErreurGestionFiles {
		try {
			final HttpURLConnection connection = (HttpURLConnection) getUrlKeolisFromDate(dateFileKeolis).openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.connect();
			return connection;
		} catch (final IOException ioException) {
			throw new ErreurGestionFiles(ioException);
		}
	}

	private GestionZipKeolis() {
	}

}
