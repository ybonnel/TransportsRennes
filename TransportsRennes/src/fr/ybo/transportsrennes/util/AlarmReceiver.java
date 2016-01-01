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

package fr.ybo.transportsrennes.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import fr.ybo.transportscommun.util.LogYbo;
import fr.ybo.transportsrennes.R;

public class AlarmReceiver extends BroadcastReceiver {

    private static final LogYbo LOG_YBO = new LogYbo(AlarmReceiver.class);

    @Override
    public void onReceive(final Context context, final Intent intent) {
        final boolean notifUpdateOn = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
                "TransportsRennes_notifUpdate", true);
		LOG_YBO.debug("Notif : " + notifUpdateOn);
        if (!notifUpdateOn) {
            return;
        }
		new ContextVoidVoidAsyncTask().execute(context);
    }



    private static final int NOTIFICATION_VERSION_ID = 1;

    private static void createNotification(final Context context, final String nouvelleVersion) {
        final int icon = R.drawable.icon;
        final CharSequence tickerText = context.getString(R.string.nouvelleVersion);
        final long when = System.currentTimeMillis();
        final CharSequence contentTitle = context.getString(R.string.nouvelleVersion);
        final CharSequence contentText = context.getString(R.string.versionDisponible, nouvelleVersion);

        final Uri uri = Uri.parse("market://details?id=fr.ybo.transportsrennes");
        final Intent notificationIntent = new Intent(Intent.ACTION_VIEW, uri);
        final PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        // the next two lines initialize the Notification, using the
        // configurations above
        final Notification notification = new Notification.Builder(context).setSmallIcon(icon).setTicker(tickerText).setWhen(when).setContentTitle(contentTitle).setContentText(contentText).setContentIntent(contentIntent).build();
        notification.defaults |= Notification.DEFAULT_ALL;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        final NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_VERSION_ID, notification);
    }

    private static class ContextVoidVoidAsyncTask extends AsyncTask<Context, Void, Void> {
        @Override
        protected Void doInBackground(final Context... params) {
            verifVersion(params[0]);
            return null;
        }

        private static void verifVersion(final Context context) {
            final String result = Version.getMarketVersion();

            LOG_YBO.debug("Version Market : " + result);
            LOG_YBO.debug("Version Courante : " + Version.getVersionCourante(context.getApplicationContext()));
            if (result != null && result.length() == 5 && result.compareTo(Version.getVersionCourante(context.getApplicationContext())) > 0) {
                final String lastVersion = PreferenceManager.getDefaultSharedPreferences(context).getString(
                        "TransportsRennesVersion", null);
                LOG_YBO.debug("Last Version : " + lastVersion);
                if (!result.equals(lastVersion)) {
                    PreferenceManager.getDefaultSharedPreferences(context).edit().putString("TransportsRennesVersion", result).commit();
                    createNotification(context, result);
                }
            }
        }
    }
}