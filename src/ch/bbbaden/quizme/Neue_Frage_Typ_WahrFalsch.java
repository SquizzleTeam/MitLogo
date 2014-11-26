package ch.bbbaden.quizme;

import java.util.ArrayList;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class Neue_Frage_Typ_WahrFalsch extends ActionBarActivity {
	private String frage;
	private SQLiteDatabase sqLiteDatabase;
	private DBZugriff dbZugriff;
	private int themenID;
	private String typ;
	private Intent starter;
	private boolean bearbeiten;
	private int fragenID, fragenIDb;
	private EditText frageView;
	private RadioGroup rg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_neue_frage_typ_wahrfalsch);

		setDatabase();
		getAllExtras();
		setIntent();
		setLayout();

	}

	private void setLayout() {
		frageView = (EditText) findViewById(R.id.editText1);
		rg = (RadioGroup) findViewById(R.id.radioGroup1);

		if (bearbeiten == true) {
			ArrayList<String> antworten = dbZugriff.getAntwortenByFID_Fragen(
					sqLiteDatabase, fragenID, true);
			String frage = dbZugriff
					.getFrageByFrageID(sqLiteDatabase, fragenID);
			frageView.setText(frage);

			int rID = rg.getCheckedRadioButtonId();
			RadioButton r0 = (RadioButton) findViewById(rID);

			if (antworten.get(0).equals(r0.getText().toString())) {
				r0.setChecked(true);
			} else {
				r0.setChecked(false);
			}
		}
	}

	private void setIntent() {
		starter = new Intent("ch.bbbaden.quizme.STARTER");
		starter.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		starter.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	}

	private void setDatabase() {
		dbZugriff = new DBZugriff(this);
		sqLiteDatabase = dbZugriff.getWritableDatabase();
	}

	private void getAllExtras() {
		Intent vorher = getIntent();
		Bundle index = vorher.getExtras();
		themenID = index.getInt("themenID");
		typ = index.getString("typ");
		bearbeiten = index.getBoolean("Bearbeiten");
		fragenID = index.getInt("fragenID");
	}

	public void validateEintrag(View view) {
		frage = frageView.getText().toString();

		if (frage.isEmpty()) {
			Toast.makeText(this, "Geben Sie bitte eine Frage ein.",
					Toast.LENGTH_LONG).show();
		} else {
			executeEintrag();
		}
	}

	private void executeEintrag() {
		int typID = dbZugriff.getTypID(sqLiteDatabase, typ);

		int rbID = rg.getCheckedRadioButtonId();
		RadioButton rb = (RadioButton) findViewById(rbID);

		String antwort = rb.getText().toString();
		if (bearbeiten == false) {
			fragenIDb = (int) dbZugriff.setFrage(sqLiteDatabase, typID,
					themenID, frage, 0);
			dbZugriff.setAntwort(sqLiteDatabase, fragenIDb, antwort, 1);
		} else {
			dbZugriff.updateAntwort(sqLiteDatabase, fragenID, antwort, 1);
		}

		if (antwort.equals("Wahr")) {
			antwort = "Falsch";
		} else {
			antwort = "Wahr";
		}

		if (bearbeiten == false) {
			dbZugriff.setAntwort(sqLiteDatabase, fragenIDb, antwort, 0);
			Toast.makeText(this, "Die Frage wurde gespeichert.",
					Toast.LENGTH_LONG).show();
			onBackPressed();
		} else {
			dbZugriff.updateAntwort(sqLiteDatabase, fragenID, antwort, 0);
			dbZugriff.updateFrage(sqLiteDatabase, themenID, typID, antwort, 0,
					fragenID);
		}
	}

	@Override
	public void onBackPressed() {
		startActivity(starter);
		finish();
	}

}
