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

import android.app.Activity;

import java.util.Calendar;

public class UpdateTimeUtil {

    private UpdateTime update;
    private Activity activity;

    private int oldNow;

    public UpdateTimeUtil(UpdateTime update, Activity activity, int now) {
        this.update = update;
        this.activity = activity;
        oldNow = now;
    }

    public UpdateTimeUtil(UpdateTime update, Activity activity) {
        this.update = update;
        this.activity = activity;
        Calendar calendar = Calendar.getInstance();
        oldNow = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
    }

    private UpdateTimeThread updateTime = null;

    public void start() {
        if (updateTime == null) {
            updateTime = new UpdateTimeThread();
            updateTime.start();
        }
    }

    public void stop() {
        if (updateTime != null) {
            updateTime.interrupt();
            updateTime = null;
        }
    }

    public static interface UpdateTime {
        public void update(Calendar calendar);
    }

    private class UpdateTimeThread extends Thread {
        @Override
        public void run() {
            while (true) {
                final Calendar calendar = Calendar.getInstance();
                int now = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
                if (now > oldNow) {
                    oldNow = now;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            update.update(calendar);
                        }
                    });
                }
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

}
