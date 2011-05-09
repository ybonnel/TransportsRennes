package fr.ybo.transportsbordeaux.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public abstract class TacheAvecProgressDialog<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

	private String message;

	private ProgressDialog myProgressDialog;
	private Context context;

	public TacheAvecProgressDialog(Context context, String message) {
		this.message = message;
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		myProgressDialog = ProgressDialog.show(context, "", message, true);
	}

	@Override
	protected void onPostExecute(Result result) {
		myProgressDialog.dismiss();
		super.onPostExecute(result);
	};
}
