package fr.ybo.transportsrennes.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;
import fr.ybo.transportsrennes.R;

public abstract class TacheAvecProgressDialog<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

	private String message;

	private ProgressDialog myProgressDialog;
	private Context context;

	public TacheAvecProgressDialog(Context context, String message) {
		this.message = message;
		this.context = context;
	}

	private boolean erreur = false;

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		myProgressDialog = ProgressDialog.show(context, "", message, true);
	}

	protected abstract Result myDoBackground(Params... params) throws ErreurReseau;

	protected Result doInBackground(Params... params) {
		try {
			return myDoBackground(params);
		} catch (ErreurReseau erreurReseau) {
			erreur = true;
		}
		return null;
	};

	@Override
	protected void onPostExecute(Result result) {
		myProgressDialog.dismiss();
		if (erreur) {
			Toast.makeText(context, context.getString(R.string.erreurReseau), Toast.LENGTH_LONG).show();
		}
		super.onPostExecute(result);
	};
}
