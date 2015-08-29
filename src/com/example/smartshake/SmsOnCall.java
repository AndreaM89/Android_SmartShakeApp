package com.example.smartshake;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;



public class SmsOnCall extends BroadcastReceiver {
	  public static String numero; //in questa maniera posso utilizzarlo dal servizio
	  private Context context;
	

	@Override
	public void onReceive(Context context , Intent intent){
		this.context = context;
		
			Bundle extras = intent.getExtras();
		if (extras != null){
			//controllo che lo stato attuale coincida con lo stato RINGING
			String state = extras.getString(TelephonyManager.EXTRA_STATE);
			if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)){
				
//salvo il numero chiamante
			numero = extras.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
			avviaservizio();
			
			Log.i("SMARTSHAKE","Servizio avviato");
			}
		}
	}
	private void avviaservizio() {
		context.startService(new Intent(context, Servizio.class));
    }	

	
}
	