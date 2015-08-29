package com.example.smartshake;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;



/*classe Java che implementa l’interfaccia SensorEventListener
 *  e che viene utilizzata per rispondere e gestire gli eventi di sistema
 */

public abstract class AccListener implements SensorEventListener {
	  private static final int FORCE_THRESHOLD = 500;  //350
	  private static final int TIME_THRESHOLD = 100;
	  private static final int SHAKE_TIMEOUT = 500;
	  private static final int SHAKE_DURATION = 1000;  //1000
	  private static final int SHAKE_COUNT = 3;
	 
	  private float mLastX=-1.0f, mLastY=-1.0f, mLastZ=-1.0f;
	  private long mLastTime;
	 
	  private int mShakeCount = 0;
	  private long mLastShake;
	  private long mLastForce;

	  public abstract void onShake();
	  
	@Override
	  public void onSensorChanged(SensorEvent event){
		//controllo solo l'accellerometro
		if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) return;
		
		 long now = System.currentTimeMillis();
	     
		    if ((now - mLastForce) > SHAKE_TIMEOUT) {
		      mShakeCount = 0;
		    }
		     
		    if ((now - mLastTime) > TIME_THRESHOLD) {
		      long diff = now - mLastTime;
		      float speed = Math.abs(event.values[0] +
		                    event.values[1] +
		                    event.values[2] - mLastX - mLastY - mLastZ) / diff * 10000;
		       
		      if (speed > FORCE_THRESHOLD) {
		        if ((++mShakeCount >= SHAKE_COUNT) && (now - mLastShake > SHAKE_DURATION)) {
		          mLastShake = now;
		          mShakeCount = 0;
		          onShake();
		          
		        }
		        mLastForce = now;
		      }
		      mLastTime = now;
		      mLastX = event.values[0];
		      mLastY = event.values[1];
		      mLastZ = event.values[2];
		    }
	}

	

	
	/*lo implemento perche SensorEventListener è un interfaccia e quindi
	  vanno implementati tutti i suoi metodi astratti,ma essendo deprecato
	 * da alcune versioni di android lo lascio vuoto
	 * @see android.hardware.SensorEventListener#onAccuracyChanged(android.hardware.Sensor, int)
	 */
	@Override
	  public void onAccuracyChanged(Sensor sensor, int accuracy) {}
	
}

