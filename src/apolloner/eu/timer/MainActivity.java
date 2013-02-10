package apolloner.eu.timer;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {
	private EditText editText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		editText = (EditText) findViewById(R.id.input);
		Intent intent = new Intent(this, TimerService.class);
		startService(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.activity_main, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void onStartClicked(View v) {
		long timeout = 1000 * Long.valueOf(editText.getText().toString());
		Intent intent = new Intent(this, TimerService.class);
		intent.putExtra("timeout", timeout);
		intent.putExtra("action", "start");
		startService(intent);
	}

	public void onStopClicked(View v) {
		Intent intent = new Intent(this, TimerService.class);
		intent.putExtra("action", "stop");
		startService(intent);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Intent intent = new Intent(this, TimerService.class);
		stopService(intent);		
	}
}
