package com.ntvfx.autoairplane;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class BootStartupReciver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub		
		AirPlaneReciver airReceiver=new AirPlaneReciver();
		airReceiver.SetAlarm(context);
	}

}

