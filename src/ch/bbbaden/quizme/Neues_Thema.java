package ch.bbbaden.quizme;

import java.util.ArrayList;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Neues_Thema extends ActionBarActivity {
	private String neuesThemaString;
	private DBZugriff dbZugriff;
	private SQLiteDatabase sqLiteDatabase;
	private Intent starter;
	private ArrayList<String> themenNamen;
	private boolean vorhanden;
	private boolean bearbeiten;
	private String thema;
	private EditText neuesThema;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_neues_thema);

		getExtras();

		setLayout();
		dbZugriff = new DBZugriff(this);
		sqLiteDatabase = dbZugriff.getWritableDatabase();

		starter = new Intent("ch.bbbaden.quizme.STARTER");
		starter.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		starter.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	}

	private void getExtras() {
		Intent vorher = getIntent();
		Bundle index = vorher.getExtras();
		bearbeiten = index.getBoolean("bearbeiten");
		if (bearbeiten == true) {
			thema = index.getString("themenName");
		}
	}

	private void setLayout() {
		neuesThema = (EditText) findViewById(R.id.neuesThema);
		if (bearbeiten == true) {
			neuesThema.setText(thema);
		}
	}

	public void validateThemenName(View view) {
		neuesThemaString = neuesThema.getText().toString();

		if (neuesThemaString.length() > 15) {
			Toast.makeText(this, "Maximal sind 15 Buchstaben erlaubt.",
					Toast.LENGTH_LONG).show();
		} else if (neuesThemaString.isEmpty()) {
			Toast.makeText(this, "Geben Sie einen Namen ein.",
					Toast.LENGTH_LONG).show();
		}
		else {
			themenNamen = dbZugriff.getAllThemen(sqLiteDatabase);
			ArrayList<String> vorhand = abgleichThemenName();
			for (int i = 0; i < vorhand.size(); i++) {
				if (vorhand.get(i).equals("richtig")) {
					vorhanden = true;
				}
			}
			if (vorhanden == true) {
				Toast.makeText(this,
						"Dieser Themen-Name ist bereits vorhanden.",
						Toast.LENGTH_LONG).show();
			} else {
				executeThemenName();
			}
		}
	}

	private void executeThemenName() {
		if (bearbeiten == false) {
			dbZugriff.insertNewThema(sqLiteDatabase, neuesThemaString);

		} else {
			dbZugriff.updateThema(sqLiteDatabase, thema, neuesThemaString);
			Toast.makeText(this, "Das Thema wurde geupdatet.",
					Toast.LENGTH_LONG).show();
		}
		startActivity(starter);
	}

	private ArrayList<String> abgleichThemenName() {
		ArrayList<String> d = new ArrayList<String>();

		for (int i = 0; i < themenNamen.size(); i++) {
			if (themenNamen.get(i).equals(neuesThemaString)) {
				if (bearbeiten == true) {
					if (themenNamen.get(i).equals(thema)) {
						d.add("falsch");
					} else {
						d.add("richtig");
					}
				} else {
					d.add("richtig");
				}
			} else {
				d.add("falsch");
			}
		}
		return d;
	}

	@Override
	public void onBackPressed() {
		startActivity(starter);
		finish();
	}
}
