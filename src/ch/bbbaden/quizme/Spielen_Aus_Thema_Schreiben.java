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
import android.widget.EditText;
import android.widget.TextView;

public class Spielen_Aus_Thema_Schreiben extends ActionBarActivity{
	private String frage;
	private int fragenID, themenID;
	private DBZugriff dbZugriff;
	private SQLiteDatabase sqLiteDatabase;
	private TextView frageView, nochOffenView;
	private boolean geprüft = false;
	private int nochOffen;
	private Intent weiter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_spielen_aus_thema_schreiben);
		
		getAllExtras();
		setDatabase();
		
		frageView = (TextView)findViewById(R.id.textView1);
		frageView.setText(frage);
		
		nochOffenView = (TextView)findViewById(R.id.textView2);
		nochOffenView.setText("Noch Offen: " + String.valueOf(nochOffen));
	}
	
	private void getAllExtras() {
		Intent vorher = getIntent();
		Bundle index = vorher.getExtras();
		frage = index.getString("frage");
		fragenID = index.getInt("fragenID");
		themenID = index.getInt("themenID");
		nochOffen = index.getInt("nochOffen");
	}
	
	private void setDatabase(){
		dbZugriff = new DBZugriff(this);
		sqLiteDatabase = dbZugriff.getWritableDatabase();
	}
	
	public void ueberpruefen(View view){
		EditText ed = (EditText)findViewById(R.id.editText1);
		String antwortBN = ed.getText().toString();
		
		ArrayList<String> antworten = dbZugriff.getAntwortenByFID_Fragen(sqLiteDatabase, fragenID, true);
		String antwort = antworten.get(0);
		
		if(geprüft == true){
			weiter = new Intent("ch.bbbaden.quizme.SPIELEN_AUS_THEMA");
			weiter.putExtra("themenID", themenID);
			weiter.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			weiter.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			startActivity(weiter);
		}
		else{
		if(antwort.equals(antwortBN)){
			frageView.setText("RICHTIG");
			geprüft = true;
			dbZugriff.updateFrageZuGekonnt(sqLiteDatabase, fragenID);
		}
		else{
			ed.setText(antwort);
			ed.setBackgroundColor(Color.parseColor("#FF0000"));
			geprüft = true;
		}
		Button btn = (Button)findViewById(R.id.button1);
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
