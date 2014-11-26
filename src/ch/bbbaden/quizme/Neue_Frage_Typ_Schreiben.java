package ch.bbbaden.quizme;

import java.util.ArrayList;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Neue_Frage_Typ_Schreiben extends ActionBarActivity {
	private String frage;
	private String antwort;
	private DBZugriff dbZugriff;
	private SQLiteDatabase sqLiteDatabase;
	private int themenID, fragenID;
	private String typ;
	private Intent starter;
	private boolean bearbeiten;
	private EditText frageView, antwortView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_neue_frage_typ_schreiben);

		dbZugriff = new DBZugriff(this);
		sqLiteDatabase = dbZugriff.getWritableDatabase();
		getAllExtras();
		setIntent();
		setLayout();

	}

	private void setLayout() {
		frageView = (EditText) findViewById(R.id.editText1);
		antwortView = (EditText) findViewById(R.id.editText2);

		if (bearbeiten == true) {
			String gesetzteFrage = dbZugriff.getFrageByFrageID(sqLiteDatabase,
					fragenID);
			ArrayList<String> gesetzeAntwort = dbZugriff
					.getAntwortenByFID_Fragen(sqLiteDatabase, fragenID, false);
			frageView.setText(gesetzteFrage);
			antwortView.setText(gesetzeAntwort.get(0));
		}
	}

	private void setIntent() {
		starter = new Intent("ch.bbbaden.quizme.STARTER");
		starter.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		starter.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
	}

	private void getAllExtras() {
		Intent vorher = getIntent();
		Bundle index = vorher.getExtras();
		themenID = index.getInt("themenID");
		typ = index.getString("typ");
		bearbeiten = index.getBoolean("Bearbeiten");
		fragenID = index.getInt("fragenID");
	}

	public void validateFormular(View view) {

		frage = frageView.getText().toString();
		antwort = antwortView.getText().toString();

		if (frage.isEmpty()) {
			Toast.makeText(this, "Sie müssen eine Frage eingeben.",
					Toast.LENGTH_LONG).show();
		} else if (antwort.isEmpty()) {
			Toast.makeText(this, "Sie müssen eine Antwort eingeben.",
					Toast.LENGTH_LONG).show();
		} else {
			executeFormular();
		}
	}

	private void executeFormular() {
		int typenID = dbZugriff.getTypID(sqLiteDatabase, typ);
		if (bearbeiten == false) {
			long frageID = dbZugriff.setFrage(sqLiteDatabase, typenID,
					themenID, frage, 0);
			dbZugriff.setAntwort(sqLiteDatabase, frageID, antwort, 1);
			Toast.makeText(this, "Die Frage wurde gespeichert.",
					Toast.LENGTH_LONG).show();
		} else {
			int frageID = (int) dbZugriff.updateFrage(sqLiteDatabase, themenID,
					typenID, frage, 0, fragenID);
			dbZugriff.updateAntwort(sqLiteDatabase, frageID, antwort, 1);
			Toast.makeText(this, "Die Frage wurde geupdated.",
					Toast.LENGTH_LONG).show();
		}
		startActivity(starter);
	}

	@Override
	public void onBackPressed() {
		startActivity(starter);
		finish();
	}
}
