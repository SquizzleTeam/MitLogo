package ch.bbbaden.quizme;

import java.util.ArrayList;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class Neue_Frage_Typ_MultipleChoice2 extends ActionBarActivity {
	private int i = 0, x = 10;
	private String frage, typ;
	private int themenID;
	private DBZugriff dbZugriff;
	private SQLiteDatabase sqLiteDatabase;
	private Intent starter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_neue_frage_typ_multiplechoice2);
		getAllExtras();
		setDatabase();
		setIntent();
	}
	
	private void setIntent(){
		starter = new Intent("ch.bbbaden.quizme.STARTER");
		starter.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		starter.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
	}

	public void loeschen(View view) {
		setContentView(R.layout.activity_neue_frage_typ_multiplechoice2);
	}

	public void validateEintrag(View view) {
		EditText neueAntwort = (EditText) findViewById(R.id.editText1);
		String antwortStr = neueAntwort.getText().toString();

		CheckBox check = (CheckBox) findViewById(R.id.checkbox);
		boolean checked = check.isChecked();

		TableLayout table = (TableLayout) findViewById(R.id.table);

		if (antwortStr.isEmpty()) {
			Toast.makeText(this, "Geben Sie eine Antwort ein.",
					Toast.LENGTH_LONG).show();
		} else if (i > 9) {
			Toast.makeText(this, "Es sind maximal 10 Antworten erlaubt",
					Toast.LENGTH_LONG).show();
		} else {
			TableRow tr = new TableRow(this);
			TextView tx = new TextView(this);
			CheckBox ch = new CheckBox(this);
			ch.setClickable(false);
			ch.setChecked(checked);
			ch.setId(x);
			tx.setText(antwortStr);
			tx.setId(i);

			tr.addView(ch);
			tr.addView(tx);
			table.addView(tr);

			check.setChecked(false);
			neueAntwort.setText("");
			i++;
			x++;
		}

	}

	public void validateEingaben(View view) {
		ArrayList<Boolean> check = new ArrayList<Boolean>();
		for (int y = 0; y < i; y++) {
			CheckBox ch = (CheckBox) findViewById(y + 10);
			check.add(ch.isChecked());
		}

		if (!check.contains(true)) {
			Toast.makeText(this,
					"Geben Sie mindestens eine richtig Antwort ein.",
					Toast.LENGTH_LONG).show();
		} else {
			executeEingaben(check);
		}

	}

	private void executeEingaben(ArrayList<Boolean> check) {
		int korrekt;
		int typenID = dbZugriff.getTypID(sqLiteDatabase, typ);
		long frageID = dbZugriff.setFrage(sqLiteDatabase, typenID,
				themenID, frage, 0);

		ArrayList<String> antworten = new ArrayList<>();
		for (int y = 0; y < i; y++) {
			TextView tx = (TextView) findViewById(y);
			antworten.add(tx.getText().toString());
		}
		for (int p = 0; p < antworten.size(); p++) {
			if (check.get(p) == false) {
				korrekt = 0;
			} else {
				korrekt = 1;
			}
			dbZugriff.setAntwort(sqLiteDatabase, frageID, antworten.get(p),
					korrekt);
		}
		Toast.makeText(this, "Die Frage wurde gespeichert", Toast.LENGTH_LONG).show();

		onBackPressed();
	}

	@Override
	public void onBackPressed() {
		startActivity(starter);
		finish();
	}

	private void setDatabase() {
		dbZugriff = new DBZugriff(this);
		sqLiteDatabase = dbZugriff.getWritableDatabase();
	}

	private void getAllExtras() {
		Intent vorher = getIntent();
		Bundle index = vorher.getExtras();
		themenID = index.getInt("themenID");
		frage = index.getString("frage");
		typ = index.getString("typ");
	}
}
