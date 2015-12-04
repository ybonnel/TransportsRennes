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
package fr.ybo.transportsbordeaux.database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import fr.ybo.database.DataBaseHelper;
import fr.ybo.transportscommun.donnees.modele.Arret;
import fr.ybo.transportscommun.donnees.modele.ArretFavori;
import fr.ybo.transportscommun.donnees.modele.ArretRoute;
import fr.ybo.transportscommun.donnees.modele.Calendrier;
import fr.ybo.transportscommun.donnees.modele.CalendrierException;
import fr.ybo.transportscommun.donnees.modele.DernierMiseAJour;
import fr.ybo.transportscommun.donnees.modele.Direction;
import fr.ybo.transportscommun.donnees.modele.GroupeFavori;
import fr.ybo.transportscommun.donnees.modele.Horaire;
import fr.ybo.transportscommun.donnees.modele.Ligne;
import fr.ybo.transportscommun.donnees.modele.Notification;
import fr.ybo.transportscommun.donnees.modele.Trajet;
import fr.ybo.transportscommun.donnees.modele.VeloFavori;

public class TransportsBordeauxDatabase extends DataBaseHelper {

    public static final String DATABASE_NAME = "transportsbordeaux.db";
	private static final int DATABASE_VERSION = 9;

    private static final List<Class<?>> DATABASE_ENTITITES =
            Arrays.asList(
                    Arret.class,
                    ArretFavori.class,
                    ArretRoute.class,
                    Calendrier.class,
                    CalendrierException.class,
                    DernierMiseAJour.class,
                    Direction.class,
                    GroupeFavori.class,
                    Horaire.class,
                    Ligne.class,
                    Notification.class,
                    Trajet.class,
                    VeloFavori.class);

    public TransportsBordeauxDatabase(final Context context) {
        super(context, DATABASE_ENTITITES, DATABASE_NAME, DATABASE_VERSION);
    }

    private Map<Integer, UpgradeDatabase> mapUpgrades;

    @Override
    protected Map<Integer, UpgradeDatabase> getUpgrades() {
        if (mapUpgrades == null) {
            mapUpgrades = new HashMap<Integer, UpgradeDatabase>();
            mapUpgrades.put(3, new UpgradeDatabase() {

                @Override
                public void upgrade(final SQLiteDatabase db) {
                    getBase().dropDataBase(db);
                    getBase().createDataBase(db);
                }
            });
            mapUpgrades.put(4, new UpgradeDatabase() {

                @Override
                public void upgrade(final SQLiteDatabase db) {

                    final Cursor cursor = db.query("sqlite_master", Collections.singleton("name").toArray(new String[1]),
                            " type = 'table'", null, null, null, null);
                    while (cursor.moveToNext()) {
                        final String tableName = cursor.getString(0);
                        if (!"android_metadata".equals(tableName) && !"VeloFavori".equals(tableName)
                                && !"ArretFavori".equals(tableName)) {
                            db.execSQL("DROP TABLE " + tableName);
                        }
                    }
                    cursor.close();
                    getBase().getTable(Arret.class).createTable(db);
                    getBase().getTable(ArretRoute.class).createTable(db);
                    getBase().getTable(Calendrier.class).createTable(db);
                    getBase().getTable(CalendrierException.class).createTable(db);
                    getBase().getTable(DernierMiseAJour.class).createTable(db);
                    getBase().getTable(Direction.class).createTable(db);
                    getBase().getTable(Ligne.class).createTable(db);
                    getBase().getTable(Trajet.class).createTable(db);
                }
            });
            mapUpgrades.put(5, new UpgradeDatabase() {
                @Override
                public void upgrade(final SQLiteDatabase db) {
                    db.execSQL("ALTER TABLE ArretFavori ADD COLUMN groupe TEXT");
                    getBase().getTable(GroupeFavori.class).createTable(db);
                }
            });
            mapUpgrades.put(6, new UpgradeDatabase() {
                @Override
                public void upgrade(final SQLiteDatabase db) {
                    getBase().deleteAll(db, DernierMiseAJour.class);
                }
            });
            mapUpgrades.put(7, new UpgradeDatabase() {
                @Override
                public void upgrade(final SQLiteDatabase db) {
                    getBase().getTable(Notification.class).createTable(db);
                }
            });
			mapUpgrades.put(8, new UpgradeDatabase() {
				@Override
                public void upgrade(final SQLiteDatabase db) {
					getBase().getTable(Trajet.class).dropTable(db);
					getBase().getTable(ArretRoute.class).dropTable(db);
					getBase().getTable(DernierMiseAJour.class).dropTable(db);
					getBase().getTable(VeloFavori.class).dropTable(db);
					getBase().getTable(Notification.class).dropTable(db);
					getBase().getTable(Trajet.class).createTable(db);
					getBase().getTable(ArretRoute.class).createTable(db);
					getBase().getTable(DernierMiseAJour.class).createTable(db);
					getBase().getTable(VeloFavori.class).createTable(db);
					getBase().getTable(Notification.class).createTable(db);

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
					columns.add("groupe");
					final Cursor arretFavoriTmp = db.query("ArretFavori_tmp", columns.toArray(new String[7]), null, null,
							null, null, null);
					final int arretIdIndex = arretFavoriTmp.getColumnIndex("arretId");
					final int ligneIdIndex = arretFavoriTmp.getColumnIndex("ligneId");
					final int nomArretIndex = arretFavoriTmp.getColumnIndex("nomArret");
					final int directionIndex = arretFavoriTmp.getColumnIndex("direction");
					final int nomCourtIndex = arretFavoriTmp.getColumnIndex("nomCourt");
					final int nomLongIndex = arretFavoriTmp.getColumnIndex("nomLong");
					final int ordreIndex = arretFavoriTmp.getColumnIndex("ordre");
					final int groupeIndex = arretFavoriTmp.getColumnIndex("groupe");
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
						favori.groupe = arretFavoriTmp.getString(groupeIndex);
						favori.macroDirection = 0;
						count++;
						getBase().insert(db, favori);
					}
					db.execSQL("DROP TABLE ArretFavori_tmp");

				}
			});
			mapUpgrades.put(9, new UpgradeDatabase() {
				@Override
                public void upgrade(final SQLiteDatabase db) {
					getBase().getTable(VeloFavori.class).dropTable(db);
					getBase().getTable(VeloFavori.class).createTable(db);
				}
			});
        }
        return mapUpgrades;
    }
}
