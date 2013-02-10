package apolloner.eu.timer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;

public class TimerService extends Service implements OnSharedPreferenceChangeListener {
	private static final String TAG = TimerService.class.getName();
	
	private PowerManager.WakeLock wl; 
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
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "TimerService");
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		sharedPref.registerOnSharedPreferenceChangeListener(this);
		Uri notification;
		if (sharedPref.contains(SettingsActivity.KEY_PREF_RINGTONE)) {
			notification = Uri.parse(sharedPref.getString(SettingsActivity.KEY_PREF_RINGTONE, null));
		} else {
			notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		}
		ringer = RingtoneManager.getRingtone(getApplicationContext(), notification);
		Log.i(TAG, "Service started");
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		killTasks();
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		sharedPref.unregisterOnSharedPreferenceChangeListener(this);		
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
				wl.acquire();
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
		if (wl.isHeld()) {
			wl.release();		
		}
		handler.removeCallbacks(runnable);
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals(SettingsActivity.KEY_PREF_RINGTONE)) {
			Uri notification = Uri.parse(sharedPreferences.getString(SettingsActivity.KEY_PREF_RINGTONE, null));
			ringer = RingtoneManager.getRingtone(getApplicationContext(), notification);
		}
	}
}
