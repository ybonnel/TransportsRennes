package fr.ybo.transportsbordeaux.util;

import java.util.Calendar;

import android.app.Activity;

public class UpdateTimeUtil {

	private final UpdateTime update;
	private final Activity activity;

	private int oldNow;

	public UpdateTimeUtil(final UpdateTime update, final Activity activity, final int now) {
		this.update = update;
		this.activity = activity;
		oldNow = now;
	}

	public UpdateTimeUtil(final UpdateTime update, final Activity activity) {
		this.update = update;
		this.activity = activity;
		final Calendar calendar = Calendar.getInstance();
		oldNow = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
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
		void update();
	}

	private class UpdateTimeThread extends Thread {
		@Override
		public void run() {
			while (true) {
				final Calendar calendar = Calendar.getInstance();
				final int now = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
				if (now > oldNow) {
					oldNow = now;
					activity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							update.update();
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
