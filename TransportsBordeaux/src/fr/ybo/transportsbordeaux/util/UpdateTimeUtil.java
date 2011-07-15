package fr.ybo.transportsbordeaux.util;

import java.util.Calendar;

import android.app.Activity;

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

	public class UpdateTimeThread extends Thread {
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
