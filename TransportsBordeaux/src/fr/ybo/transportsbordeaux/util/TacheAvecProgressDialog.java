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

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public abstract class TacheAvecProgressDialog<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

    private String message;

    protected ProgressDialog myProgressDialog;
    private Context context;

    public TacheAvecProgressDialog(Context context, String message) {
        this.message = message;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        try {
            myProgressDialog = ProgressDialog.show(context, "", message, true);
        } catch (Exception ignore) {

        }
    }

    @Override
    protected void onPostExecute(Result result) {
        try {
            myProgressDialog.dismiss();
        } catch (IllegalArgumentException ignore) {
        }
        super.onPostExecute(result);
    }
}
