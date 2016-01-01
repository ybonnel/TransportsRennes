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
package fr.ybo.transportsrennes.database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import fr.ybo.database.DataBaseHelper;
import fr.ybo.moteurcsv.MoteurCsv;
import fr.ybo.transportscommun.donnees.modele.Arret;
import fr.ybo.transportscommun.donnees.modele.ArretFavori;
import fr.ybo.transportscommun.donnees.modele.ArretRoute;
import fr.ybo.transportscommun.donnees.modele.Bounds;
import fr.ybo.transportscommun.donnees.modele.Calendrier;
import fr.ybo.transportscommun.donnees.modele.CalendrierException;
import fr.ybo.transportscommun.donnees.modele.DernierMiseAJour;
import fr.ybo.transportscommun.donnees.modele.Direction;
import fr.ybo.transportscommun.donnees.modele.GroupeFavori;
import fr.ybo.transportscommun.donnees.modele.Ligne;
import fr.ybo.transportscommun.donnees.modele.Notification;
import fr.ybo.transportscommun.donnees.modele.Trajet;
import fr.ybo.transportscommun.donnees.modele.VeloFavori;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.database.modele.AlertBdd;
import fr.ybo.transportsrennes.keolis.ConstantesKeolis;

public class TransportsRennesDatabase extends DataBaseHelper {

    private static final String DATABASE_NAME = "keolis.db";
	private static final int DATABASE_VERSION = 18;

    private final Context context;

    public TransportsRennesDatabase(final Context context) {
        super(context, ConstantesKeolis.LIST_CLASSES_DATABASE, DATABASE_NAME, DATABASE_VERSION);
        this.context = context;
    }

    private Map<Integer, UpgradeDatabase> mapUpgrades;

    private abstract static class UpgradeDatabaseWithError implements UpgradeDatabase {

        @Override
        public final void upgrade(final SQLiteDatabase arg0) {
            try {
                myUpgrade(arg0);
            } catch (final Exception exception) {
                throw new RuntimeException(exception);
            }
        }

        abstract void myUpgrade(SQLiteDatabase db);

    }

    @Override
    protected Map<Integer, UpgradeDatabase> getUpgrades() {
        if (mapUpgrades == null) {
            mapUpgrades = new HashMap<Integer, UpgradeDatabase>();
            mapUpgrades.put(2, new UpgradeDatabaseWithError() {
                @Override
                public void myUpgrade(final SQLiteDatabase db) {
                    getBase().getTable(VeloFavori.class).createTable(db);
                }
            });
            mapUpgrades.put(3, new UpgradeDatabaseWithError() {
                @Override
                public void myUpgrade(final SQLiteDatabase db) {
                    getBase().dropDataBase(db);
                    getBase().createDataBase(db);
                }
            });
            mapUpgrades.put(4, new MyUpgradeDatabaseWithError4());
            mapUpgrades.put(5, new UpgradeDatabaseWithError() {
                @Override
                public void myUpgrade(final SQLiteDatabase db) {
                    final Cursor cursor = db.query("sqlite_master", Collections.singleton("name").toArray(new String[1]),
                            " type = 'table'", null, null, null, null);
                    while (cursor.moveToNext()) {
                        final String tableName = cursor.getString(0);
                        if (!"android_metadata".equals(tableName) && !"VeloFavori".equals(tableName) && !"ArretFavori"
                                .equals(tableName)) {
                            db.execSQL("DROP TABLE " + tableName);
                        }
                    }
                    cursor.close();
                    getBase().getTable(Direction.class).createTable(db);
                    getBase().getTable(ArretRoute.class).createTable(db);
                    getBase().getTable(DernierMiseAJour.class).createTable(db);
                    getBase().getTable(Ligne.class).createTable(db);
                    getBase().getTable(Arret.class).createTable(db);
                    getBase().getTable(Calendrier.class).createTable(db);
                    getBase().getTable(Trajet.class).createTable(db);
                    // Gestion des favoris.
                    db.execSQL("ALTER TABLE ArretFavori RENAME TO ArretFavori_tmp");
                    getBase().getTable(ArretFavori.class).createTable(db);
                    final List<String> columns = new ArrayList<String>(7);
                    columns.add("stopId");
                    columns.add("routeId");
                    columns.add("nomArret");
                    columns.add("direction");
                    columns.add("routeNomCourt");
                    columns.add("routeNomLong");
                    columns.add("ordre");
                    final Cursor arretFavoriTmp =
                            db.query("ArretFavori_tmp", columns.toArray(new String[7]), null, null, null, null, null);
                    final int arretIdIndex = arretFavoriTmp.getColumnIndex("stopId");
                    final int ligneIdIndex = arretFavoriTmp.getColumnIndex("routeId");
                    final int nomArretIndex = arretFavoriTmp.getColumnIndex("nomArret");
                    final int directionIndex = arretFavoriTmp.getColumnIndex("direction");
                    final int nomCourtIndex = arretFavoriTmp.getColumnIndex("routeNomCourt");
                    final int nomLongIndex = arretFavoriTmp.getColumnIndex("routeNomLong");
                    final int ordreIndex = arretFavoriTmp.getColumnIndex("ordre");
                    final ArretFavori favori = new ArretFavori();
                    int count = 1;
                    while (arretFavoriTmp.moveToNext()) {
                        favori.arretId = arretFavoriTmp.getString(arretIdIndex);
                        favori.ligneId = arretFavoriTmp.getString(ligneIdIndex);
                        favori.nomArret = arretFavoriTmp.getString(nomArretIndex);
                        favori.direction = arretFavoriTmp.getString(directionIndex);
                        favori.nomCourt = arretFavoriTmp.getString(nomCourtIndex);
                        favori.nomLong = arretFavoriTmp.getString(nomLongIndex);
                        favori.ordre = arretFavoriTmp.isNull(ordreIndex) ? count : arretFavoriTmp.getInt(ordreIndex);
                        count++;
                        getBase().insert(db, favori);
                    }
                    db.execSQL("DROP TABLE ArretFavori_tmp");
                }
            });
            mapUpgrades.put(6, new UpgradeDatabaseWithError() {
                @Override
                public void myUpgrade(final SQLiteDatabase db) {
                    final Cursor cursor = db.query("sqlite_master", Collections.singleton("name").toArray(new String[1]),
                            " type = 'table'", null, null, null, null);
                    while (cursor.moveToNext()) {
                        final String tableName = cursor.getString(0);
                        if (!"android_metadata".equals(tableName) && !"VeloFavori".equals(tableName) && !"ArretFavori"
                                .equals(tableName)) {
                            db.execSQL("DROP TABLE " + tableName);
                        }
                    }
                    cursor.close();
                    getBase().getTable(Direction.class).createTable(db);
                    getBase().getTable(ArretRoute.class).createTable(db);
                    getBase().getTable(DernierMiseAJour.class).createTable(db);
                    getBase().getTable(Ligne.class).createTable(db);
                    getBase().getTable(Arret.class).createTable(db);
                    getBase().getTable(Calendrier.class).createTable(db);
                    getBase().getTable(Trajet.class).createTable(db);
                }
            });
            mapUpgrades.put(7, new MyUpgradeDatabaseWithError3());
            mapUpgrades.put(9, new UpgradeDatabaseWithError() {
                @Override
                public void myUpgrade(final SQLiteDatabase db) {
                    // Gestion des favoris.
                    db.execSQL("ALTER TABLE ArretFavori RENAME TO ArretFavori_tmp");
                    getBase().getTable(ArretFavori.class).createTable(db);
                    final List<String> columns = new ArrayList<String>(7);
                    columns.add("arretId");
                    columns.add("ligneId");
                    columns.add("nomArret");
                    columns.add("direction");
                    columns.add("nomCourt");
                    columns.add("nomLong");
                    columns.add("ordre");
                    final Cursor arretFavoriTmp =
                            db.query("ArretFavori_tmp", columns.toArray(new String[7]), null, null, null, null, null);
                    final int arretIdIndex = arretFavoriTmp.getColumnIndex("arretId");
                    final int ligneIdIndex = arretFavoriTmp.getColumnIndex("ligneId");
                    final int nomArretIndex = arretFavoriTmp.getColumnIndex("nomArret");
                    final int directionIndex = arretFavoriTmp.getColumnIndex("direction");
                    final int nomCourtIndex = arretFavoriTmp.getColumnIndex("nomCourt");
                    final int nomLongIndex = arretFavoriTmp.getColumnIndex("nomLong");
                    final int ordreIndex = arretFavoriTmp.getColumnIndex("ordre");
                    final ArretFavori favori = new ArretFavori();
                    while (arretFavoriTmp.moveToNext()) {
                        favori.arretId = arretFavoriTmp.getString(arretIdIndex);
                        favori.ligneId = arretFavoriTmp.getString(ligneIdIndex);
                        favori.nomArret = arretFavoriTmp.getString(nomArretIndex);
                        favori.direction = arretFavoriTmp.getString(directionIndex);
                        favori.nomCourt = arretFavoriTmp.getString(nomCourtIndex);
                        favori.nomLong = arretFavoriTmp.getString(nomLongIndex);
                        favori.ordre = arretFavoriTmp.getInt(ordreIndex);
                        getBase().insert(db, favori);
                    }
                    db.execSQL("DROP TABLE ArretFavori_tmp");
                    // Verrue pour la ligne 67 et sa boucle.
                    final Cursor ligne67 = db.query("Ligne", new String[]{"Chargee"}, "id = 67", null, null, null, null);
                    if (ligne67.moveToFirst() && !ligne67.isNull(0) && ligne67.getInt(0) == 1) {
                        db.execSQL("UPDATE Horaire_67 SET terminus = 1 WHERE arretId = 'repto1' AND stopSequence > 3");
                    }
                }
            });
            mapUpgrades.put(10, new UpgradeDatabaseWithError() {
                @Override
                public void myUpgrade(final SQLiteDatabase db) {
                    final Cursor cursor = db.query("sqlite_master", Collections.singleton("name").toArray(new String[1]),
                            " type = 'table'", null, null, null, null);
                    while (cursor.moveToNext()) {
                        final String tableName = cursor.getString(0);
                        if (!"android_metadata".equals(tableName) && !"VeloFavori".equals(tableName) && !"ArretFavori"
                                .equals(tableName)) {
                            db.execSQL("DROP TABLE " + tableName);
                        }
                    }
                    cursor.close();
                    getBase().getTable(Direction.class).createTable(db);
                    getBase().getTable(ArretRoute.class).createTable(db);
                    getBase().getTable(DernierMiseAJour.class).createTable(db);
                    getBase().getTable(Ligne.class).createTable(db);
                    getBase().getTable(Arret.class).createTable(db);
                    getBase().getTable(Calendrier.class).createTable(db);
                    getBase().getTable(Trajet.class).createTable(db);
                    // Gestion des favoris.
                    db.execSQL("ALTER TABLE ArretFavori RENAME TO ArretFavori_tmp");
                    getBase().getTable(ArretFavori.class).createTable(db);
                    final List<String> columns = new ArrayList<String>(7);
                    columns.add("arretId");
                    columns.add("ligneId");
                    columns.add("nomArret");
                    columns.add("direction");
                    columns.add("nomCourt");
                    columns.add("nomLong");
                    columns.add("ordre");
                    final Cursor arretFavoriTmp =
                            db.query("ArretFavori_tmp", columns.toArray(new String[7]), null, null, null, null, null);
                    final int arretIdIndex = arretFavoriTmp.getColumnIndex("arretId");
                    final int ligneIdIndex = arretFavoriTmp.getColumnIndex("ligneId");
                    final int nomArretIndex = arretFavoriTmp.getColumnIndex("nomArret");
                    final int directionIndex = arretFavoriTmp.getColumnIndex("direction");
                    final int nomCourtIndex = arretFavoriTmp.getColumnIndex("nomCourt");
                    final int nomLongIndex = arretFavoriTmp.getColumnIndex("nomLong");
                    final int ordreIndex = arretFavoriTmp.getColumnIndex("ordre");
                    final ArretFavori favori = new ArretFavori();
                    final List<Class<?>> classCsv = new ArrayList<Class<?>>();
                    classCsv.add(ArretRoute.class);
                    classCsv.add(Direction.class);
                    final MoteurCsv moteur = new MoteurCsv(classCsv);
                    final Map<String, Map<String, List<ArretRoute>>> mapArretsRoutes =
                            new HashMap<String, Map<String, List<ArretRoute>>>();
                    for (final ArretRoute arretRoute : moteur
                            .parseInputStream(context.getResources().openRawResource(R.raw.arrets_routes),
                                    ArretRoute.class)) {
                        if (!mapArretsRoutes.containsKey(arretRoute.ligneId)) {
                            mapArretsRoutes.put(arretRoute.ligneId, new HashMap<String, List<ArretRoute>>());
                        }
                        if (!mapArretsRoutes.get(arretRoute.ligneId).containsKey(arretRoute.arretId)) {
                            mapArretsRoutes.get(arretRoute.ligneId)
                                    .put(arretRoute.arretId, new ArrayList<ArretRoute>());
                        }
                        mapArretsRoutes.get(arretRoute.ligneId).get(arretRoute.arretId).add(arretRoute);
                    }
                    while (arretFavoriTmp.moveToNext()) {
                        favori.arretId = arretFavoriTmp.getString(arretIdIndex);
                        favori.ligneId = arretFavoriTmp.getString(ligneIdIndex);
                        favori.nomArret = arretFavoriTmp.getString(nomArretIndex);
                        favori.direction = arretFavoriTmp.getString(directionIndex);
                        favori.nomCourt = arretFavoriTmp.getString(nomCourtIndex);
                        favori.nomLong = arretFavoriTmp.getString(nomLongIndex);
                        favori.ordre = arretFavoriTmp.getInt(ordreIndex);
                        if (mapArretsRoutes.containsKey(favori.ligneId) && mapArretsRoutes.get(favori.ligneId)
                                .containsKey(favori.arretId)) {
                            for (final ArretRoute arretRoute : mapArretsRoutes.get(favori.ligneId).get(favori.arretId)) {
                                favori.macroDirection = arretRoute.macroDirection;
                            }
                        }
                        if (favori.macroDirection != null) {
                            getBase().insert(db, favori);
                        }
                    }
                    db.execSQL("DROP TABLE ArretFavori_tmp");
                }
            });
            mapUpgrades.put(11, new UpgradeDatabaseWithError() {
                @Override
                public void myUpgrade(final SQLiteDatabase db) {
                    try {
                        db.execSQL("ALTER TABLE ArretFavori ADD COLUMN groupe TEXT");
                    } catch (final Exception ignore) {

                    }
                    getBase().getTable(GroupeFavori.class).createTable(db);
                }
            });
            mapUpgrades.put(12, new UpgradeDatabaseWithError() {
                @Override
                public void myUpgrade(final SQLiteDatabase db) {
                    boolean tableTrouve = false;
                    final Cursor cursor = db.query("sqlite_master", Collections.singleton("name").toArray(new String[1]),
                            " type = 'table'", null, null, null, null);
                    while (cursor.moveToNext()) {
                        final String tableName = cursor.getString(0);
                        if ("GroupeFavori".equals(tableName)) {
                            tableTrouve = true;
                        }
                    }
                    cursor.close();
                    if (!tableTrouve) {
                        getBase().getTable(GroupeFavori.class).createTable(db);
                    }
                }
            });
            mapUpgrades.put(13, new UpgradeDatabaseWithError() {
                @Override
                public void myUpgrade(final SQLiteDatabase db) {
                    getBase().getTable(Notification.class).createTable(db);
                }
            });
            mapUpgrades.put(14, new UpgradeDatabaseWithError() {
                @Override
                public void myUpgrade(final SQLiteDatabase db) {
                    getBase().getTable(AlertBdd.class).createTable(db);
                    getBase().getTable(Bounds.class).createTable(db);
                }
            });
            mapUpgrades.put(15, new UpgradeDatabaseWithError() {
                @Override
                public void myUpgrade(final SQLiteDatabase db) {
                    final Ligne ligneA = new Ligne();
                    ligneA.id = "a";
                    final List<Ligne> result = getBase().select(db, ligneA, null, null, null);
                    if (result != null && !result.isEmpty()) {
                        final Ligne ligneA2 = result.get(0);
                        ligneA2.ordre = 0;
                        getBase().update(db, ligneA2);
                    }
                }
            });
			mapUpgrades.put(16, new MyUpgradeDatabaseWithError2());
			mapUpgrades.put(17, new UpgradeDatabaseWithError() {
				@Override
                public void myUpgrade(final SQLiteDatabase db) {
					db.execSQL("ALTER TABLE Calendrier ADD COLUMN dateDebut TEXT");
					db.execSQL("ALTER TABLE Calendrier ADD COLUMN dateFin TEXT");
					getBase().getTable(CalendrierException.class).createTable(db);
				}
			});
			mapUpgrades.put(18, new MyUpgradeDatabaseWithError());
        }
        return mapUpgrades;
    }

    private static class MyUpgradeDatabaseWithError extends UpgradeDatabaseWithError {
        @Override
public void myUpgrade(final SQLiteDatabase db) {
db.execSQL("DELETE FROM DernierMiseAJour");
        }
    }

    private static class MyUpgradeDatabaseWithError2 extends UpgradeDatabaseWithError {
        @Override
public void myUpgrade(final SQLiteDatabase db) {
            db.execSQL("CREATE INDEX ArretRoute_directionId ON ArretRoute(directionId)");
        }
    }

    private static class MyUpgradeDatabaseWithError3 extends UpgradeDatabaseWithError {
        @Override
        public void myUpgrade(final SQLiteDatabase db) {
            db.execSQL("DELETE FROM DernierMiseAJour");
        }
    }

    private static class MyUpgradeDatabaseWithError4 extends UpgradeDatabaseWithError {
        @Override
        public void myUpgrade(final SQLiteDatabase db) {
            db.execSQL("ALTER TABLE ArretFavori ADD COLUMN ordre INTEGER");
        }
    }
}
