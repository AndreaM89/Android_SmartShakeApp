package com.example.smartshake;
 
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.content.SharedPreferences;



public class Servizio extends Service
{
    
    private static BackgroundThread backgroundThread;
    private AudioManager audioManager;
    private SensorManager sensorManager;
    private Sensor mAccelerometer;
    private boolean sent;
    private int ringerMode;
    private static String mex;
    
    @Override
    public void onCreate() {
       
      super.onCreate();
    //  sent = false;
      audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
      sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
      mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
      //salvo lo stato della suoneria per ripristinarlo successivamente
      ringerMode = audioManager.getRingerMode();
	      if (sensorManager == null) {
	        throw new UnsupportedOperationException("Sensors not supported");
	      }

// definisco il primo messaggio che viene visualizzato al primo avvio dell'applicazione
      SharedPreferences prefs = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
      mex= prefs.getString("messaggio", "Non posso rispondere");
      Log.i("SMARTSHAKE","Il messaggio dalle pref è " +mex);
      // Faccio partire il BackgroundThread
      backgroundThread = new BackgroundThread();
      backgroundThread.start();
     
    }
   
    @Override
    public void onDestroy() {
 
      audioManager.setRingerMode(ringerMode);
      backgroundThread.running = false;
      audioManager = null;
      mAccelerometer = null;
      sensorManager = null;
      sent= false;
      super.onDestroy();
   
    }
   

    //metodo utile per fare in modo che il servizio sia accessibile da altre app,
    //poiche cio non è richiesto ritorna semplicemente null
    @Override
    public IBinder onBind(Intent arg0) {
      return null;
    }
   
//creo il thread
    private final class BackgroundThread extends Thread {
    	
      private static final long DELAY = 500L;
      public boolean running = true;
      
      
      public void run()
      {
    
        AccListener myListener = new AccListener() {
          @Override
          public void onShake()
          {
        	  //allo shake disabilito la suoneria e invio il messaggio
        	  
        	  Log.i("SMARTSHAKE", "RICEVUTO LO SHAKE");
        	audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        	//controllo per inviare un solo messaggio
           if (!sent) {	
        	   sendSMS();
        	   Log.i("SMARTSHAKE","Il messaggio è " +mex);
        	   sent=true;
           }
        	backgroundThread.running =false;
           Log.i("SMARTSHAKE","Messaggio inviato al numero" +SmsOnCall.numero);
           
          
          }
        };
         
      
        boolean supported = sensorManager.registerListener(myListener,
                  mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
         
        
        if (!supported) {
          running = false;
          throw new UnsupportedOperationException("Accelerometer not supported");
        }
   
        while(running) {
            try {
              Thread.sleep(DELAY);
            } catch (InterruptedException e) {
              e.printStackTrace();
           
            }
          }
    
        sensorManager.unregisterListener(myListener, mAccelerometer);
        // Al termine del metodo run terminiamo il servizio
      Log.i("SMARTSHAKE","SENSORE DEREGISTRATO");
        stopSelf();
        Log.i("SMARTSHAKE","SERVIZIO MORTO");
      }
    } // chiusura del worker thread
    
	public static void sendSMS() {
		//prendo il numero dal broadcastreceiver e il messaggio dalle shared preference
	    String phoneNumber = SmsOnCall.numero;
	    String message = mex;

	    SmsManager smsManager = SmsManager.getDefault();
	    
	    try {
	    smsManager.sendTextMessage(phoneNumber, null, message, null, null);
	    }catch 	(IllegalArgumentException e) { 
	    Log.e("SMARTSHAKE","Messaggio vuoto");
	    /*in realta viene fatto un controllo nel main per evitare che il messaggio sia nullo
	    //ma in ogni caso se dovesse essere nullo il messaggio
	      evita che l'eccezione si propaghi e causi il crash
	    dell'app */
	}
	}
}
