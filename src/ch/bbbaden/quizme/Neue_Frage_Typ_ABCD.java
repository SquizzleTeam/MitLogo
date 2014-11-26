package ch.bbbaden.quizme;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class Neue_Frage_Typ_ABCD extends ActionBarActivity{
	private Intent starter;
	private int themenID;
	private String typ, antwort, frage;
	private int durchgang = 0;
	private DBZugriff dbZugriff;
	private SQLiteDatabase sqLiteDatabase;
	private int TypID;
	private String[] abcd = {"A", "B", "C", "D"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_neue_frage_typ_abcd);
		getAllExtras();
		setIntent();
		setDatabase();
	}
	
	private void setIntent(){
		starter = new Intent("ch.bbbaden.quizme.STARTER");
		starter.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		starter.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	}
	
	private void setDatabase() {
		dbZugriff = new DBZugriff(this);
		sqLiteDatabase = dbZugriff.getWritableDatabase();
	}
	
	@Override
	public void onBackPressed(){
		startActivity(starter);
		finish();
	}
	
	private void getAllExtras() {
		Intent vorher = getIntent();
		Bundle index = vorher.getExtras();
		themenID = index.getInt("themenID");
		typ = index.getString("typ");
	}
	
	public void hinzufuegen(View view){
		EditText neueAnt = (EditText) findViewById(R.id.editText2);
		String antwortstr = neueAnt.getText().toString();
		TableLayout tab = (TableLayout) findViewById(R.id.tablelayout);
		
		if(antwortstr.isEmpty()){
			Toast.makeText(this, "Geben Sie eine Antwort ein.", Toast.LENGTH_LONG).show();
		}
		else if(durchgang > 3){
			Toast.makeText(this, "Es sind maximal 4 Antworten erlaubt.", Toast.LENGTH_LONG).show();	
		}
		else{
			antwortstr = abcd[durchgang] + ": " + antwortstr;
			TableRow tr = new TableRow(this);
			TextView tx = new TextView(this);
			tx.setText(antwortstr);
			tx.setId(durchgang);
			tr.addView(tx);
			tab.addView(tr);
			durchgang++;
			neueAnt.setText("");
		}
	}
	
	public void loeschen(View view){
		setContentView(R.layout.activity_neue_frage_typ_abcd);
	}
	
	public void speichern(View view){
		EditText edit = (EditText) findViewById(R.id.editText1);
		frage = edit.getText().toString();
		
		if(durchgang < 3){
			Toast.makeText(this, "Sie müssen 4 Antworten eingeben", Toast.LENGTH_LONG).show();
		}
		else if(frage.isEmpty()){
			Toast.makeText(this, "Sie müssen eine Frage eingeben.", Toast.LENGTH_LONG).show();
		}
		else{
			RadioGroup rg = (RadioGroup) findViewById(R.id.radioGroup1);
			int rbID = rg.getCheckedRadioButtonId();
			RadioButton rb = (RadioButton)findViewById(rbID);
			
			TypID = dbZugriff.getTypID(sqLiteDatabase, typ);
			
			long frageID = dbZugriff.setFrage(sqLiteDatabase, TypID, themenID, frage, 0);
			
			Toast.makeText(this, "Frage wurde gespeichert.", Toast.LENGTH_LONG).show();
			
			for(int i = 0; i < durchgang; i++){
				int korrekt = 0;
				if(rb.getText().toString().equals(abcd[i])){
					korrekt = 1;
				}
				
				TextView tx = (TextView) findViewById(i);
				antwort = tx.getText().toString();
				antwort.substring(3);
				
				dbZugriff.setAntwort(sqLiteDatabase, frageID, antwort, korrekt);

				onBackPressed();
			}
		}
	}
	
}
