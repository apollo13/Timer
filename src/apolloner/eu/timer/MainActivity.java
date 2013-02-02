package apolloner.eu.timer;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {
	private Handler handler = new Handler();
	private Boolean running = false;
	private long timeout = 0;
	private EditText editText;
	private Ringtone ringer;


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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		editText = (EditText) findViewById(R.id.input);
		Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		ringer = RingtoneManager.getRingtone(getApplicationContext(), notification);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	public void onStartClicked(View v) {
		if (!running) {
			running = true;
			timeout = 1000 * Long.valueOf(editText.getText().toString());
			handler.postDelayed(runnable, timeout);
		}
	}

	public void onStopClicked(View v) {
		if (running) {
			running = false;
			handler.removeCallbacks(runnable);
		}
	}


}
