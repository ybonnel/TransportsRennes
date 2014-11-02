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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import fr.ybo.transportsbordeaux.R;
import fr.ybo.transportscommun.util.LogYbo;

public class AlarmReceiver extends BroadcastReceiver {

    private static final LogYbo LOG_YBO = new LogYbo(AlarmReceiver.class);

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean notifUpdateOn = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
                "TransportsBordeaux_notifUpdate", true);
		LOG_YBO.debug("Notif : " + notifUpdateOn);
        if (!notifUpdateOn) {
            return;
        }
		new AsyncTask<Context, Void, Void>() {
			@Override
			protected Void doInBackground(Context... params) {
				verifVersion(params[0]);
				return null;
			}
		}.execute(context);
    }

	private void verifVersion(Context context) {
		String result = Version.getMarketVersion();

		LOG_YBO.debug("Version Market : " + result);
		LOG_YBO.debug("Version Courante : " + Version.getVersionCourante(context.getApplicationContext()));
		if (result != null && result.length() == 5 && result.compareTo(Version.getVersionCourante(context.getApplicationContext())) > 0) {
			String lastVersion =
					PreferenceManager.getDefaultSharedPreferences(context).getString("TransportsBordeauxVersion", null);
			LOG_YBO.debug("Last Version : " + lastVersion);
			if (!result.equals(lastVersion)) {
				SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
				editor.putString("TransportsBordeauxVersion", result);
				editor.commit();
				createNotification(context, result);
			}
		}
	}

    private final int NOTIFICATION_VERSION_ID = 1;

    private void createNotification(Context context, String nouvelleVersion) {
        int icon = R.drawable.icon;
        CharSequence tickerText = context.getString(R.string.nouvelleVersion);
        long when = System.currentTimeMillis();
        CharSequence contentTitle = context.getString(R.string.nouvelleVersion);
        CharSequence contentText = context.getString(R.string.versionDisponible, nouvelleVersion);

        Uri uri = Uri.parse("market://details?id=fr.ybo.transportsbordeaux");
        Intent notificationIntent = new Intent(Intent.ACTION_VIEW, uri);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        // the next two lines initialize the Notification, using the
        // configurations above
        Notification notification = new Notification(icon, tickerText, when);
        notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
        notification.defaults |= android.app.Notification.DEFAULT_ALL;
        notification.flags |= android.app.Notification.FLAG_AUTO_CANCEL;


        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_VERSION_ID, notification);
    }

}