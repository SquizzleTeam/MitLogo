package ch.bbbaden.quizme;

import java.util.ArrayList;

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

public class Spielen_Aus_Thema_Wahrfalsch extends ActionBarActivity {
	private String frage;
	private int fragenID;
	private int themenID;
	private DBZugriff dbZugriff;
	private SQLiteDatabase sqLiteDatabase;
	private TextView frageView, nochOffenView;
	private boolean geprüft = false;
	private int nochOffen;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_spielen_aus_thema_wahrfalsch);

		getAllExtras();
		setDatabase();
		
		frageView = (TextView) findViewById(R.id.textView1);
		frageView.setText(frage);
		
		nochOffenView = (TextView)findViewById(R.id.textView2);
		nochOffenView.setText("Noch Offen: " + String.valueOf(nochOffen));
	}

	private void setDatabase() {
		dbZugriff = new DBZugriff(this);
		sqLiteDatabase = dbZugriff.getWritableDatabase();
	}

	private void getAllExtras() {
		Intent vorher = getIntent();
		Bundle index = vorher.getExtras();
		frage = index.getString("frage");
		fragenID = index.getInt("fragenID");
		themenID = index.getInt("themenID");
		nochOffen = index.getInt("nochOffen");
	}

	public void ueberpruefen(View view) {
		RadioGroup rg = (RadioGroup) findViewById(R.id.radioGroup1);
		int rbID = rg.getCheckedRadioButtonId();
		RadioButton rb = (RadioButton) findViewById(rbID);
		String antwortBN = rb.getText().toString();

		ArrayList<String> antwort = dbZugriff.getAntwortenByFID_Fragen(
				sqLiteDatabase, fragenID, true);

		if (geprüft == true) {
			Intent weiter = new Intent("ch.bbbaden.quizme.SPIELEN_AUS_THEMA");
			weiter.putExtra("themenID", themenID);
			weiter.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			weiter.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			startActivity(weiter);
		} else {
			if (antwort.get(0).equals(antwortBN)) {
				frageView.setText("RICHTIG");
				geprüft = true;
				dbZugriff.updateFrageZuGekonnt(sqLiteDatabase, fragenID);
			} else {
				rb.setBackgroundColor(Color.parseColor("#FF0000"));
				geprüft = true;
			}
			Button btn = (Button) findViewById(R.id.button1);
			btn.setText("WEITER");
		}

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
