package ch.bbbaden.quizme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class Neue_Frage_Typen_Auswahl extends ActionBarActivity{
	private Intent starter;
	private String typ;
	private int themenID;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_neue_frage_typen_auswahl);
		setIntent();
		getAllExtras();
	}

	private void getAllExtras() {
		Intent vorher = getIntent();
		Bundle index = vorher.getExtras();
		themenID = index.getInt("themenID");
	}
	
	public void entscheidenRadioButton(View view){
		RadioGroup rg = (RadioGroup) findViewById(R.id.radioGroup1);
		RadioButton rb= (RadioButton) findViewById(rg.getCheckedRadioButtonId());
		typ = rb.getText().toString();
		validateTyp();
	}
	
	private void setIntent(){
		starter = new Intent("ch.bbbaden.quizme.STARTER");
		starter.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		starter.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	}
	
	private void validateTyp(){
		Intent next = null ;
		if(typ.equals("Schreiben")){
			next = new Intent("ch.bbbaden.quizme.NEUE_FRAGE_TYP_SCHREIBEN");
		}
		else if(typ.equals("Multiple Choice")){
			next = new Intent("ch.bbbaden.quizme.NEUE_FRAGE_TYP_MULTIPLECHOICE");
		}
		else if(typ.equals("A, B, C, D")){
			next = new Intent("ch.bbbaden.quizme.NEUE_FRAGE_TYP_ABCD");
		}
		else if(typ.equals("Wahr / Falsch")){
			next = new Intent("ch.bbbaden.quizme.NEUE_FRAGE_TYP_WAHRFALSCH");
		}
		next.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		next.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		next.putExtra("themenID", themenID);
		next.putExtra("typ", typ);
		startActivity(next);
	}
	
	@Override
	public void onBackPressed(){
		startActivity(starter);
		finish();
	}
}
