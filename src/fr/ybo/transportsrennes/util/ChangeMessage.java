package fr.ybo.transportsrennes.util;

import android.app.ProgressDialog;

/**
 * Created by IntelliJ IDEA.
 * User: ybonnel
 * Date: 13/12/10
 * Time: 23:28
 * To change this template use File | Settings | File Templates.
 */
public class ChangeMessage implements Runnable {

    private final ProgressDialog progressDialog;
    private final String message;

    public ChangeMessage(final ProgressDialog progressDialog, final String message) {
        this.progressDialog = progressDialog;
        this.message = message;
    }

    public void run() {
        progressDialog.setMessage(message);
    }

}
