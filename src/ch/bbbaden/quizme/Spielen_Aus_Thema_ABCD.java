package ch.bbbaden.quizme;

import java.util.ArrayList;
import java.util.Collections;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class Spielen_Aus_Thema_ABCD extends ActionBarActivity {
	private DBZugriff dbZugriff;
	private SQLiteDatabase sqLiteDatabase;
	private String frage;
	private int themenID, fragenID;
	private Intent starter;
	private TextView frageview, nochOffenView;
	private Button button;
	private boolean geprüft = false;
	private int nochOffen;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_spielen_aus_thema_abcd);

		getAllExtras();
		setDatabase();
		setIntent();
		setLayout();
	}

	public void ueberpruefen(View view) {
		RadioGroup rg = (RadioGroup) findViewById(R.id.radioGroup1);
		int rbid = rg.getCheckedRadioButtonId();
		RadioButton rb = (RadioButton) findViewById(rbid);
		String antwort = rb.getText().toString();
		ArrayList<String> richtigeAntwort = dbZugriff.getAntwortenByFID_Fragen(
				sqLiteDatabase, fragenID, true);
		if (geprüft == false) {
			if (antwort.equals(richtigeAntwort.get(0).substring(0, 3))) {
				frageview.setText("RICHTIG!");
				dbZugriff.updateFrageZuGekonnt(sqLiteDatabase, fragenID);
			} else {
				rb.setBackgroundColor(Color.parseColor("#FF0000"));
			}
			button.setText("WEITER");
			
			geprüft = true;
		} else {
			Intent weiter = new Intent("ch.bbbaden.quizme.SPIELEN_AUS_THEMA");
			weiter.putExtra("themenID", themenID);
			weiter.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			weiter.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			startActivity(weiter);
		}
	}

	private void getAllExtras() {
		Intent vorher = getIntent();
		Bundle index = vorher.getExtras();
		frage = index.getString("frage");
		fragenID = index.getInt("fragenID");
		themenID = index.getInt("themenID");
		nochOffen = index.getInt("nochOffen");
	}

	private void setDatabase() {
		dbZugriff = new DBZugriff(this);
		sqLiteDatabase = dbZugriff.getWritableDatabase();
	}

	private void setLayout() {
		frageview = (TextView) findViewById(R.id.textView1);
		frageview.setText(frage);

		button = (Button) findViewById(R.id.button1);

		ArrayList<String> antworten = dbZugriff.getAntwortenByFID_Fragen(
				sqLiteDatabase, fragenID, false);
		Collections.reverse(antworten);

		TextView antwort1 = (TextView) findViewById(R.id.textView2);
		antwort1.setText(antworten.get(0).substring(2));

		TextView antwort2 = (TextView) findViewById(R.id.textView3);
		antwort2.setText(antworten.get(1).substring(2));

		TextView antwort3 = (TextView) findViewById(R.id.textView4);
		antwort3.setText(antworten.get(2).substring(2));

		TextView antwort4 = (TextView) findViewById(R.id.textView5);
		antwort4.setText(antworten.get(3).substring(2));
		
		nochOffenView = (TextView)findViewById(R.id.textView6);
		nochOffenView.setText("Noch Offen: " + String.valueOf(nochOffen));
	}

	private void setIntent() {
		starter = new Intent("ch.bbbaden.quizme.STARTER");
		starter.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		starter.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
	}

	@Override
	public void onBackPressed() {
		startActivity(starter);
		finish();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.neubeginnen:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    	builder
	    	.setTitle("Neu beginnen")
	    	.setMessage("Wollen Sie den ganzen Fortschritt löschen und von neu beginnen?")
	    	.setIcon(android.R.drawable.ic_dialog_alert)
	    	.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
	    	    public void onClick(DialogInterface dialog, int which) {
	    	    	dbZugriff.setAllKorrektToFalse(sqLiteDatabase, themenID);
	    	    	Intent starter = new Intent("ch.bbbaden.quizme.STARTER");
	    	    	starter.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	    	    	startActivity(starter);
	    	    }
	    	})
	    	.setNegativeButton("Nein", null)
	    	.show();
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.spielen_menu, menu);
		return true;
	}	
}
