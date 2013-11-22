package com.ntvfx.autoairplane;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;

@SuppressLint("DefaultLocale")
public class AirPlaneReciver extends BroadcastReceiver {

	private boolean isEnabled;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		final Context ctx = context;

		boolean mEnabled = intent.getBooleanExtra("enabled", true);
		log("onReciver: " + mEnabled);
		Settings.System.putInt(context.getContentResolver(), "airplane_mode_on", mEnabled ? 0 : 1);
		// boolean isEnabled2 = isEnable == 0 ? false : true;
		Intent mIntent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
		mIntent.putExtra("state", !mEnabled);
		context.sendBroadcast(mIntent);
		CancelAlarm(ctx);
		SetAlarm(ctx);

	}

	public AirPlaneReciver() {
		// TODO Auto-generated constructor stub
	}

	public void SetAlarm(Context context) {

		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(context, AirPlaneReciver.class);
		long updateTime = getNextUpdate(context);
		String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(updateTime),
				TimeUnit.MILLISECONDS.toMinutes(updateTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(updateTime)), TimeUnit.MILLISECONDS.toSeconds(updateTime)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(updateTime)));

		i.putExtra("enabled", isEnabled);
		log("next update: " + hms + " --- " + isEnabled);
		/*SharedPreferences mSharePref = mContext.getSharedPreferences("myData", Context.MODE_PRIVATE);
		SharedPreferences.Editor mEditor = mSharePref.edit();*/
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
		am.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + updateTime, pi);
	}

	private long getNextUpdate(Context context) {
		// TODO Auto-generated method stub
		long nextMilis = 0;
		SharedPreferences mSharePref = context.getSharedPreferences("myData", Context.MODE_PRIVATE);

		Calendar now = Calendar.getInstance();

		int currentHour = now.get(Calendar.HOUR_OF_DAY);// * 1000 * 60 * 60;
		int currentMin = now.get(Calendar.MINUTE);// * 1000 * 60;
		log("currentHour:" + currentHour + ":" + currentMin);
		String timeStart = mSharePref.getString("timeStart", "23:00");
		String timeStop = mSharePref.getString("timeStop", "06:00");

		int hourStart = timeArr(timeStart, 0);
		int minStart = timeArr(timeStart, 1);

		int hourStop = timeArr(timeStop, 0);
		int minStop = timeArr(timeStop, 1);

		log("hourStart: " + hourStart + ":" + minStart);
		log("hourStop: " + hourStop + ":" + minStop);

		int currentTime = currentHour * 60 + currentMin;
		int startTime = hourStart * 60 + minStart;
		int stopTime = hourStop * 60 + minStop;

		if (startTime <= currentTime) {
			startTime = startTime + 24 * 60;
		}

		if (stopTime <= currentTime) {
			stopTime = stopTime + 24 * 60;
		}

		if (startTime - currentTime < stopTime - currentTime) {
			isEnabled = false;
			nextMilis = startTime - currentTime;
		} else {
			isEnabled = true;
			nextMilis = stopTime - currentTime;
		}

		log("next milis update: " + nextMilis + " time: " + (startTime - currentTime) + ":" + (stopTime - currentTime));
		nextMilis = nextMilis * 60 * 1000;

		return nextMilis;
	}

	public void CancelAlarm(Context context) {
		Intent intent = new Intent(context, AirPlaneReciver.class);
		PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(sender);
		log("Alarm canceled");
	}

	private int timeArr(String mString, int pos) {
		int[] mArr = new int[2];
		String[] mStr = mString.split(":");
		mArr[0] = Integer.valueOf(mStr[0]);
		mArr[1] = Integer.valueOf(mStr[1]);
		int mNum = mArr[pos];
		return mNum;
	}

	private void log(String msg) {
		Log.e("Receiver ", msg);
	}
}
