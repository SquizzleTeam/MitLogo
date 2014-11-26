package ch.bbbaden.quizme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;

public class Neue_Frage_Typ_MultipleChoice extends ActionBarActivity{
	private String frage;
	private String typ;
	private int themenID;
	private Intent starter, weiter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_neue_frage_typ_multiplechoice);
		getAllExtras();
		setIntent();
	}
	
	private void setIntent(){
		starter = new Intent("ch.bbbaden.quizme.STARTER");
		starter.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		starter.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		
		weiter = new Intent("ch.bbbaden.quizme.NEUE_FRAGE_TYP_MULTIPLECHOICE2");
		weiter.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		weiter.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
	}
	
	public void weiter(View view){
		EditText frageview = (EditText) findViewById(R.id.editText1);
		frage = frageview.getText().toString();
		
		weiter.putExtra("frage", frage);
		weiter.putExtra("themenID", themenID);
		weiter.putExtra("typ", typ);
		
		startActivity(weiter);
	}
	
	private void getAllExtras() {
		Intent vorher = getIntent();
		Bundle index = vorher.getExtras();
		themenID = index.getInt("themenID");
		typ = index.getString("typ");
	}
	
	@Override
	public void onBackPressed(){
		startActivity(starter);
		finish();
	}
}
