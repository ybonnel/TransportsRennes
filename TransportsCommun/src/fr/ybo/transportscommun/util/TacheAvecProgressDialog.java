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
 * 
 * Contributors:
 *     ybonnel - initial API and implementation
 */
package fr.ybo.transportscommun.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.widget.Toast;
import fr.ybo.transportscommun.R;

public abstract class TacheAvecProgressDialog<Params, Progress, Result> extends AsyncTask<Params, Progress, Result>
		implements OnCancelListener {

	private String message;

	private ProgressDialog myProgressDialog;
	private Context context;

	private boolean cancellable = false;

	public TacheAvecProgressDialog(Context context, String message, boolean cancellable) {
		this.message = message;
		this.context = context;
		this.cancellable = cancellable;
	}

	private boolean erreur = false;

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		try {
			myProgressDialog = ProgressDialog.show(context, "", message, true, cancellable, this);
		} catch (Exception ignore) {

		}
	}

	protected abstract void myDoBackground() throws ErreurReseau;

	protected Result doInBackground(Params... params) {
		try {
			myDoBackground();
		} catch (ErreurReseau erreurReseau) {
			erreurReseau.printStackTrace();
			erreur = true;
		}
		return null;
	}

    @Override
	protected void onPostExecute(Result result) {
		try {
			myProgressDialog.dismiss();
		} catch (IllegalArgumentException ignore) {
		}
		if (erreur) {
			Toast.makeText(context, context.getString(R.string.erreurReseau), Toast.LENGTH_LONG).show();
		}
		super.onPostExecute(result);
	}

	boolean isCancelled = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.content.DialogInterface.OnCancelListener#onCancel(android.content
	 * .DialogInterface)
	 */
	@Override
	public void onCancel(DialogInterface dialog) {
		this.cancel(true);
	}
}
