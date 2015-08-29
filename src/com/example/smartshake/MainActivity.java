package com.example.smartshake;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.TextView;
import android.text.InputFilter;
import android.content.SharedPreferences;
import android.content.Context;



/* Note di progetto:
 * L'idea per limitare l'utilizzo della batteria è utilizzare un servizio avviabile
 * da broadcast receiver al momento dell'avvenuta chiamata in modo che l'accellerometro
 * utilizzato dal servizio sia attivo solamente per il tempo necessario
 */

public class MainActivity extends Activity {
	//definizione degli oggetti utilizzati
	private Button hideButton;
	private ImageButton confirmButton;

	//definizione degli oggetti per sharedpreference
	
	private final static String prefmessage = "MyPref";
     // Costante relativa al nome della key del messaggio
	private final static String TEXT_DATA_KEY = "messaggio";
	public static String mess;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//chiamo il metodo originario della super classe 
		super.onCreate(savedInstanceState);
		//tutto quello aggiunto dopo è la mia ridefinizione
		setContentView(R.layout.activity_main);
		
		//definisco i bottoni e widget vari collegandoli ai layouts definiti 
		confirmButton =(ImageButton) findViewById(R.id.confirm);
		hideButton= (Button) findViewById(R.id.hidebutton);
		final TextView text_message = (TextView) findViewById(R.id.message);
		//editext con in piu filtro per inserire al massimo 40 caratteri
		final EditText edit_message=(EditText) findViewById(R.id.editText);
		InputFilter[] filtro = new InputFilter[1];
		filtro[0] = new InputFilter.LengthFilter(40);
		edit_message.setFilters(filtro);
		
		//definisco la mia sharedpreference di nome prefmessage e ristretta alla sola
		//applicazione
		SharedPreferences prefs = getSharedPreferences(prefmessage, Context.MODE_PRIVATE);
		//inizio dando un valore di default al messaggio inviato
		mess = prefs.getString(TEXT_DATA_KEY, "Non posso rispondere");
		text_message.setText(mess);
	
	
		hideButton.setOnClickListener(new View.OnClickListener() {
			@Override 
			public void onClick(View v){
				finish();
			}
		});
	
		//permette di acquisire il nuovo messaggio e registrarlo nelle preferenze
		confirmButton.setOnClickListener(new View.OnClickListener() {
			@Override 
			public void onClick(View v){
				SharedPreferences prefs = getSharedPreferences(prefmessage, Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = prefs.edit();
				//con questo if controllo che non venga salvata una stringa nulla come corpo dell' sms
					
					mess =edit_message.getText().toString();
					if (!mess.isEmpty()){
					editor.putString(TEXT_DATA_KEY, mess);
	                editor.commit();
	                text_message.setText(mess);
	                toastInsert();
					}  else {
						toastError();
					}
				}
		});
		
		
		
	}


		private void toastInsert(){
			Toast toast = Toast.makeText(this,
					"Nuovo messaggio salvato",
					Toast.LENGTH_SHORT);
			toast.show();
		}
		

		
		private void toastError(){
			Toast toast = Toast.makeText(this,
					"Inserire almeno un carattere",
					Toast.LENGTH_LONG);
			toast.show();
		}


}
