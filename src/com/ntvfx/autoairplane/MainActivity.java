package com.ntvfx.autoairplane;



import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

public class MainActivity extends Activity {

	private Context mContext;
	private Button startButton, stopButton;
	private ToggleButton mToggleBtn;
	private boolean isOnServices;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext = this;
		initButton();
		initEditText();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		saveSharePrefence();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		restoreSharePrefence();
	}

	private void restoreSharePrefence() {
		// TODO Auto-generated method stub
		SharedPreferences mSharePref = getSharedPreferences("myData", MODE_PRIVATE);
		isOnServices = mSharePref.getBoolean("isOnServices", false);
		String timeStart = mSharePref.getString("timeStart", "23:00");
		String timeStop = mSharePref.getString("timeStop", "06:00");
		final EditText startTime = (EditText) findViewById(R.id.editText1);
		final EditText endTime = (EditText) findViewById(R.id.editText2);
		startTime.setText(timeStart);
		endTime.setText(timeStop);
		TextView alertText = (TextView) findViewById(R.id.alertText);
		if (isOnServices) {
			alertText.setText("Services is on");
		} else {
			alertText.setText("Services is off");
		}
	}

	private void saveSharePrefence() {
		// TODO Auto-generated method stub

		SharedPreferences mSharePref = getSharedPreferences("myData", MODE_PRIVATE);
		SharedPreferences.Editor mEditor = mSharePref.edit();
		mEditor.clear();

		final EditText startTime = (EditText) findViewById(R.id.editText1);
		final EditText endTime = (EditText) findViewById(R.id.editText2);
		String timeStart = startTime.getText().toString();
		String timeStop = endTime.getText().toString();

		mEditor.putString("timeStart", timeStart);
		mEditor.putString("timeStop", timeStop);
		mEditor.putBoolean("isOnServices", isOnServices);

		mEditor.commit();
	}

	private void initButton() {
		// TODO Auto-generated method stub
		startButton = (Button) findViewById(R.id.startBtn);
		stopButton = (Button) findViewById(R.id.stopBtn);

		mToggleBtn = (ToggleButton) findViewById(R.id.toggleButton);
		boolean isEnabled = Settings.System.getInt(getContentResolver(), "airplane_mode_on", 0) == 1;
		if (isEnabled) {
			mToggleBtn.setChecked(isEnabled);
		}

		mToggleBtn.setOnClickListener(toggleBtnHandler);
		startButton.setOnClickListener(startBtnHandler);
		stopButton.setOnClickListener(stopBtnHandler);
	}

	private View.OnClickListener startBtnHandler = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			/*Intent service = new Intent(mContext, AirPlaneServices.class);
			log("is My services running: " + isMyServiceRunning(AirPlaneServices.class.getName()));
			if (!isMyServiceRunning(AirPlaneServices.class.getName())) {
				startService(service);
			} else {
			
			}*/
			AirPlaneReciver receiver = new AirPlaneReciver();
			receiver.CancelAlarm(mContext);
			receiver.SetAlarm(mContext);
			TextView alertText = (TextView) findViewById(R.id.alertText);
			alertText.setText("Services is on");
			isOnServices = true;
			saveSharePrefence();
		}
	};
	private View.OnClickListener stopBtnHandler = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			stopServices();

		}
	};

	/*private boolean isMyServiceRunning(String servicesName) {
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (servicesName.equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}*/

	protected void stopServices() {
		// TODO Auto-generated method stub
		/*if (isMyServiceRunning(AirPlaneServices.class.getName())) {
			Intent service = new Intent(mContext, AirPlaneServices.class);
			log("is My services running: " + isMyServiceRunning(AirPlaneServices.class.getName()));			
			stopService(service);}*/
		TextView alertText = (TextView) findViewById(R.id.alertText);
		AirPlaneReciver receiver = new AirPlaneReciver();
		receiver.CancelAlarm(mContext);
		alertText.setText("Services is off");
		isOnServices = false;
		saveSharePrefence();

	}

	private View.OnClickListener toggleBtnHandler = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			boolean isEnabled = Settings.System.getInt(getContentResolver(), "airplane_mode_on", 0) == 1;

			// toggle airplane mode
			Settings.System.putInt(getContentResolver(), "airplane_mode_on", isEnabled ? 0 : 1);
			// Post an intent to reload
			Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
			intent.putExtra("state", !isEnabled);
			sendBroadcast(intent);
		}
	};

	private EditText currentEdit;

	private void initEditText() {
		// TODO Auto-generated method stub
		final EditText startTime = (EditText) findViewById(R.id.editText1);
		final EditText endTime = (EditText) findViewById(R.id.editText2);
		startTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					currentEdit = startTime;
					initDatepicker();
				}
			}
		});
		endTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					currentEdit = endTime;

					initDatepicker();
				}
			}
		});
	}

	private String checkDigit(int number) {
		return number <= 9 ? "0" + number : String.valueOf(number);
	}

	private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// TODO Auto-generated method stub
			String date = checkDigit(hourOfDay) + ":" + checkDigit(minute);
			currentEdit.setText(date);
			// TextView alertText=(TextView) MainActivity.this.findViewById(R.id.alertText);
			startButton.setFocusable(true);
			startButton.setFocusableInTouchMode(true);
			startButton.requestFocus();
			startButton.setFocusable(false);
			startButton.setFocusableInTouchMode(false);
			startButton.clearFocus();

			timePicker.dismiss();
			saveSharePrefence();
			// alertText.requestFocus();

		}
	};
	private TimePickerDialog timePicker;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	protected void initDatepicker() {
		// TODO Auto-generated method stub

		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			timePicker = new TimePickerDialog(mContext, android.R.style.Theme_Holo_Dialog_NoActionBar_MinWidth, timePickerListener, timeArr(currentEdit.getText().toString(), 0),
					timeArr(currentEdit.getText().toString(), 1), true);
		else
			timePicker = new TimePickerDialog(mContext, timePickerListener, timeArr(currentEdit.getText().toString(), 0), timeArr(currentEdit.getText().toString(), 1), true);

		timePicker.show();

	}

	private int timeArr(String mString, int pos) {
		int[] mArr = new int[2];
		String[] mStr = mString.split(":");
		mArr[0] = Integer.valueOf(mStr[0]);
		mArr[1] = Integer.valueOf(mStr[1]);
		int mNum = mArr[pos];
		return mNum;
	}

/*	private void log(String msg) {
		Log.e("MainActivity", msg);
	}*/

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
