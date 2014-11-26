package ch.bbbaden.quizme;

import java.util.ArrayList;
import java.util.Random;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

public class Spielen_Aus_Thema extends ActionBarActivity{
	private Intent starter;
	private DBZugriff dbZugriff;
	private SQLiteDatabase sqLiteDatabase;
	private int themenID;
	private Intent typIntent;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getAllExtras();
		setIntent();
		setDatabase();
		
		ArrayList<Integer> fragenSizelist = dbZugriff.getAllFragenIDIntTABLE_FRAGEbyThemaANDGekonnt(sqLiteDatabase, themenID);
		int fragenSize = fragenSizelist.size();
		if(fragenSize != 0){
		Random rand = new Random();
		int  fragenIDnot = rand.nextInt(fragenSize);
		int fragenID = fragenSizelist.get(fragenIDnot);
		String frage = dbZugriff.getFrageByFrageID(sqLiteDatabase, fragenID);
		int typID = dbZugriff.getFID_TypByFrageID(sqLiteDatabase, fragenID);
		
		String typ = dbZugriff.getTypNamebyID(sqLiteDatabase, typID);
		
		if(typ.equals("Schreiben")){
			typIntent = new Intent("ch.bbbaden.quizme.SPIELEN_AUS_THEMA_SCHREIBEN");
		}
		else if(typ.equals("Wahr / Falsch")){
			typIntent = new Intent("ch.bbbaden.quizme.SPIELEN_AUS_THEMA_WAHRFALSCH");
		}
		else if(typ.equals("A, B, C, D")){
			typIntent = new Intent("ch.bbbaden.quizme.SPIELEN_AUS_THEMA_ABCD");
		}
		else{
			typIntent = new Intent("ch.bbbaden.quizme.SPIELEN_AUS_THEMA_MULTIPLECHOICE");
		}

		typIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		typIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		typIntent.putExtra("frage", frage);
		typIntent.putExtra("themenID", themenID);
		typIntent.putExtra("fragenID", fragenID);
		typIntent.putExtra("nochOffen", fragenSize);
		startActivity(typIntent);
		}
		else{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    	builder
	    	.setTitle("Neu beginnen")
	    	.setMessage("Sie haben das ganze Thema durchgearbeitet. Ihr Fortschritt wird nun zurückgesetzt.")
	    	.setIcon(android.R.drawable.ic_dialog_alert)
	    	.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
	    	    public void onClick(DialogInterface dialog, int which) {
	    	    	dbZugriff.setAllKorrektToFalse(sqLiteDatabase, themenID);
	    	    	Intent starter = new Intent("ch.bbbaden.quizme.STARTER");
	    	    	starter.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	    	    	startActivity(starter);
	    	    }
	    	})
	    	.show();
		}

		
	}
	
	private void setDatabase(){
		dbZugriff = new DBZugriff(this);
		sqLiteDatabase = dbZugriff.getWritableDatabase();
	}
	
	private void setIntent(){
		starter = new Intent("ch.bbbaden.quizme.STARTER");
		starter.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		starter.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	}
	
	private void getAllExtras() {
		Intent vorher = getIntent();
		Bundle index = vorher.getExtras();
		themenID = index.getInt("themenID");
	}
	
	@Override
	public void onBackPressed(){
		startActivity(starter);
		finish();
	}
	
}
