package com.ntvfx.autoairplane;
/*
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

public class AirPlaneServices extends Service {

	private Context mContext;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub

		mContext = getApplicationContext();
		setScheduleAlarm();

		return super.onStartCommand(intent, flags, startId);

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		log("onDestroy");
		super.onDestroy();
		
	}

	private void setScheduleAlarm() {
		// TODO Auto-generated method stub
		Intent reciverIntent = new Intent(mContext, AirPlaneReciver.class);
		reciverIntent.putExtra("isEnabled", 1);
		PendingIntent pi = PendingIntent.getBroadcast(mContext, 0, reciverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
		am.cancel(pi);
		am.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), 5000, pi);// (AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 1000, pi);
		AirPlaneReciver receiver=new AirPlaneReciver();
		receiver.SetAlarm(mContext);
	}

	private void log(String msg) {
		Log.e("Services", msg);
	}

}*/
