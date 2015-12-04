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

package fr.ybo.transportscommun.util;

import java.util.Calendar;
import java.util.Set;

import android.app.Activity;

public class UpdateTimeUtil {

	private final LogYbo LOG = new LogYbo(UpdateTimeUtil.class);

    private final UpdateTime update;
    private final Activity activity;

    private int oldNow;
	private int oldSecond;

    private UpdateTimeUtil(final UpdateTime update, final Activity activity, final int now) {
        this.update = update;
        this.activity = activity;
        oldNow = now;
		oldSecond = 0;
    }

    public UpdateTimeUtil(final UpdateTime update, final Activity activity) {
        this.update = update;
        this.activity = activity;
        final Calendar calendar = Calendar.getInstance();
        oldNow = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
		oldSecond = calendar.get(Calendar.SECOND);
	}

    private UpdateTimeThread updateTime;

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

    public interface UpdateTime {
        void update(Calendar calendar);

		boolean updateSecond();

		Set<Integer> secondesToUpdate();
    }

    private class UpdateTimeThread extends Thread {
        @Override
        public void run() {
            while (true) {
                final Calendar calendar = Calendar.getInstance();
                final int now = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
				final int second = calendar.get(Calendar.SECOND);

				boolean mustUpdate = false;
				if (now != oldNow) {
					// changement de minute.
					LOG.debug("now != oldNow : update");
					LOG.debug("now : " + now);
					LOG.debug("oldNow : " + oldNow);
					mustUpdate = true;
				}
				if (!mustUpdate && update.updateSecond()) {
					for (final int secondToUpdate : update.secondesToUpdate()) {
						if (oldSecond < secondToUpdate && secondToUpdate <= second) {
							LOG.debug("Update for seconds");
							LOG.debug("secondToUpdate : " + secondToUpdate);
							LOG.debug("second : " + second);
							LOG.debug("oldSecond : " + oldSecond);
							mustUpdate = true;
						}
					}
				}
				if (mustUpdate) {
                    oldNow = now;
					oldSecond = second;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            update.update(calendar);
                        }
                    });
                }
                try {
                    sleep(500);
                } catch (final InterruptedException e) {
                    break;
                }
            }
        }
    }

}
