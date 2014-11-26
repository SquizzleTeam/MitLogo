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
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class Spielen_Aus_Thema_Mulitplechoice extends ActionBarActivity{
	private DBZugriff dbZugriff;
	private String frage;
	private SQLiteDatabase sqLiteDatabase;
	private int fragenID;
	private int themenID;
	private ArrayList<String> antworten;
	private boolean bestanden = true, geprüft = false;
	private TextView tx, nochOffenView;
	private int nochOffen;

	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_spielen_aus_thema_multiplechoice);
		
		setDatabase();
		getAllExtras();
		setLayout();
	}
	private void setLayout(){
		TableLayout table = (TableLayout) findViewById(R.id.table);
		
		tx= (TextView) findViewById(R.id.textView1);
		tx.setText(frage);
		
		nochOffenView = (TextView)findViewById(R.id.textView2);
		nochOffenView.setText("Noch Offen: " + String.valueOf(nochOffen));
		
		antworten = dbZugriff.getAntwortenByFID_Fragen(sqLiteDatabase, fragenID, false);
		Collections.reverse(antworten);
		
		for(int i = 0; i < antworten.size(); i++){
			TableRow tr = new TableRow(this);
			CheckBox ch = new CheckBox(this);
			ch.setId(i);
			ch.setText(antworten.get(i));
			tr.addView(ch);
			table.addView(tr);
		}
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
	
	public void ueberpruefen(View view){
		if(geprüft == false){
		ArrayList<String> richtigeAntworten = dbZugriff.getKorrektheitByFID_Fragen(sqLiteDatabase, fragenID);
		
		ArrayList<Boolean> recht = new ArrayList<>();
		for(int i = 0; i< richtigeAntworten.size(); i++){
			if(richtigeAntworten.get(i).equals("0")){
				recht.add(false);
			}
			else{
				recht.add(true);
			}
		}

		ArrayList<Boolean> richtig = new ArrayList<>();
		for(int i = 0; i < antworten.size(); i++){
			CheckBox ch = (CheckBox) findViewById(i);
			richtig.add(ch.isChecked());
		}
		
		for(int i = 0; i< richtigeAntworten.size(); i++){
			if(recht.get(i).equals(richtig.get(i))){
				bestanden = true;
			}
			else{
				bestanden = false;
				CheckBox ch = (CheckBox)findViewById(i);
				ch.setBackgroundColor(Color.parseColor("#FF0000"));
			}
		}
		if(bestanden == true){
			tx.setText("RICHTIG!");
			dbZugriff.updateFrageZuGekonnt(sqLiteDatabase, fragenID);
		}
		
		Button btn = (Button)findViewById(R.id.button1);
		btn.setText("weiter");
		geprüft = true;
		}
		else{
			Intent weiter = new Intent("ch.bbbaden.quizme.SPIELEN_AUS_THEMA");
			weiter.putExtra("themenID", themenID);
			weiter.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			weiter.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			startActivity(weiter);
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
