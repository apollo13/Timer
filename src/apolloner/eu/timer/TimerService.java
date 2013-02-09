package apolloner.eu.timer;

import android.app.Service;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class TimerService extends Service {
	private static final String TAG = TimerService.class.getName();
	
	private Ringtone ringer;
	private boolean running = false;
	private long timeout = 0;
	private Handler handler = new Handler();


	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			if (!running) {
				return;
			}
	        ringer.play();
			handler.postDelayed(this, timeout);
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		ringer = RingtoneManager.getRingtone(getApplicationContext(), notification);
		Log.i(TAG, "Service started");
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		killTasks();
		Log.i(TAG, "Service destroyed");		
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Bundle extras = intent.getExtras();
		if (extras != null) {
			String action = extras.getString("action");
			if (action.equals("start")) {
				timeout = extras.getLong("timeout");
				if (running) {
					killTasks();
				}
				handler.postDelayed(runnable, timeout);
				running = true;
			} else if (action.equals("stop")){
				killTasks();
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}
	
	private void killTasks() {
		running = false;
		handler.removeCallbacks(runnable);
	}
}
