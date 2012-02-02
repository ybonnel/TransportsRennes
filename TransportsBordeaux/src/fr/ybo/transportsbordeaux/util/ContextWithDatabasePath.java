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

package fr.ybo.transportsbordeaux.util;

import java.io.File;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.os.Environment;
import fr.ybo.transportsbordeaux.tbcapi.TcbException;
import fr.ybo.transportscommun.util.LogYbo;

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
        return SQLiteDatabase.openOrCreateDatabase(dbFile, factory);
    }

}
